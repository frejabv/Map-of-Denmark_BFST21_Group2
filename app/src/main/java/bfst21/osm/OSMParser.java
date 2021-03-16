package bfst21.osm;

import java.io.*;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import bfst21.Model;
import bfst21.exceptions.UnsupportedFileTypeException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class OSMParser {

    public static void readMapElements(String filepath, Model model) throws IOException, XMLStreamException {
        if (filepath.endsWith(".osm")) {
            loadOSM(new FileInputStream(filepath), model);
        } else if (filepath.endsWith(".zip")) {
            loadZIP(new FileInputStream(filepath), model);
        } else if (filepath.endsWith(".obj")) {
            // TODO
            System.out.println("missing object loader");
        } else {
            String[] splitFileName = filepath.split("\\.");
            if (splitFileName.length > 0) {
                throw new UnsupportedFileTypeException("." + splitFileName[splitFileName.length - 1]);
            }
        }
    }

    private static void loadOSM(InputStream inputStream, Model model)
            throws XMLStreamException, FactoryConfigurationError {
        XMLStreamReader xmlReader = XMLInputFactory.newInstance()
                .createXMLStreamReader(new BufferedInputStream(inputStream));
        ArrayList<Tag> tags = new ArrayList<>();
        Way way = null;
        Relation relation = null;

        boolean isWay = false;
        boolean isRelation = false;

        while (xmlReader.hasNext()) {
            switch (xmlReader.next()) {
            case XMLStreamReader.START_ELEMENT:
                switch (xmlReader.getLocalName()) {
                case "bounds":
                    model.setMinX(Float.parseFloat(xmlReader.getAttributeValue(null, "minlon")));
                    model.setMaxX(Float.parseFloat(xmlReader.getAttributeValue(null, "maxlon")));
                    model.setMaxY(Float.parseFloat(xmlReader.getAttributeValue(null, "maxlat")) / -0.56f);
                    model.setMinY(Float.parseFloat(xmlReader.getAttributeValue(null, "minlat")) / -0.56f);
                    break;
                case "node":
                    var id = Long.parseLong(xmlReader.getAttributeValue(null, "id"));
                    var lon = Float.parseFloat(xmlReader.getAttributeValue(null, "lon"));
                    var lat = Float.parseFloat(xmlReader.getAttributeValue(null, "lat"));
                    model.addToNodeIndex(new Node(lon, lat, id));
                    break;
                case "way":
                    isWay = true;
                    var wayId = Long.parseLong(xmlReader.getAttributeValue(null, "id"));
                    way = new Way(wayId);
                    model.addToWayIndex(way);
                    tags.clear();
                    break;
                case "nd":
                    if (isWay && way != null) {
                        var ref = Long.parseLong(xmlReader.getAttributeValue(null, "ref"));
                        way.addNode((Node) model.getNodeIndex().getMember(ref));
                    }
                    break;
                case "tag":
                    var k = xmlReader.getAttributeValue(null, "k");
                    var v = xmlReader.getAttributeValue(null, "v");

                    if (k.equals("highway") && isWay) {
                        System.out.println("Road!");
                    }

                    try {
                        var tag = Tag.valueOf(v.toUpperCase());
                        tags.add(tag);
                    } catch (IllegalArgumentException e) {
                        // We don't care about tags not in our Tag enum
                    }

                    try {
                        var tag = Tag.valueOf(k.toUpperCase());
                        tags.add(tag);
                    } catch (IllegalArgumentException e) {
                        // We don't care about tags not in our Tag enum
                    }
                    break;
                case "relation":
                    isRelation = true;
                    var relationId = Long.parseLong(xmlReader.getAttributeValue(null, "id"));
                    relation = new Relation(relationId);
                    model.addToRelationIndex(relation);
                    tags.clear();
                    break;
                case "member":
                    var type = xmlReader.getAttributeValue(null, "type");
                    var ref = Long.parseLong(xmlReader.getAttributeValue(null, "ref"));
                    var role = xmlReader.getAttributeValue(null, "role");
                    Member memberRef = null;
                    switch (type) {
                    case "node":
                        memberRef = model.getNodeIndex().getMember(ref);
                        break;
                    case "way":
                        memberRef = model.getWayIndex().getMember(ref);
                        break;
                    case "relation":
                        memberRef = model.getRelationIndex().getMember(ref);
                        break;
                    }
                    if (memberRef != null) {
                        relation.addMember(memberRef);
                        memberRef.addRole(relation.getId(), role);
                    }
                    break;

                }
                break;
            case XMLStreamReader.END_ELEMENT:
                switch (xmlReader.getLocalName()) {
                case "way":
                    for (var tag : tags) {
                        if (tag == Tag.COASTLINE) {
                            model.addCoastline(way);
                        } else {
                            var drawableMap = model.getDrawableMap();

                            drawableMap.putIfAbsent(tag, new ArrayList<>());
                            drawableMap.get(tag).add(way);
                        }
                    }
                case "relation":

                    isRelation = false;
                    relation = null;
                    break;
                }
                break;
            }
        }
        // TODO: Please fix (kinda fixed)
        model.setIslands(mergeCoastlines(model.getCoastlines()));
        System.out.println(model.getCoastlines());
        if (model.getCoastlines() == null || model.getCoastlines().isEmpty()) {
            System.out.println("you fool, you think it is that simple? hahahahah");
        }
    }

    public static List<Drawable> mergeCoastlines(ArrayList<Way> coastlines) {
        Map<Node, Way> pieces = new HashMap<>();
        for (var coast : coastlines) {
            var before = pieces.remove(coast.first());
            var after = pieces.remove(coast.last());
            if (before == after)
                after = null;
            var merged = Way.merge(before, coast, after);
            pieces.put(merged.first(), merged);
            pieces.put(merged.last(), merged);
        }
        List<Drawable> merged = new ArrayList<>();
        pieces.forEach((node, way) -> {
            if (way.last() == node) {
                merged.add(way);
            }
        });
        return merged;
    }

    public static void loadZIP(InputStream inputStream, Model model) throws IOException, XMLStreamException {
        var zip = new ZipInputStream(inputStream);
        zip.getNextEntry();
        loadOSM(zip, model);
    }
}
