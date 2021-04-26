package bfst21.osm;

import bfst21.Model;
import bfst21.exceptions.UnsupportedFileTypeException;
import org.checkerframework.checker.units.qual.A;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

public class OSMParser {
    private static HashMap<String, List<String>> addresses = new HashMap<>();

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
        Node node = null;
        Way way = null;
        Relation relation = null;

        boolean isWay = false;
        boolean isNode = false;
        String name = "";

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
                            node = new Node(lon, lat, id);
                            model.addToNodeIndex(node);
                            isNode = true;
                            break;
                        case "way":
                            isWay = true;
                            var wayId = Long.parseLong(xmlReader.getAttributeValue(null, "id"));
                            way = new Way(wayId);
                            model.addToWayIndex(way);
                            tags = new ArrayList<>();
                            break;
                        case "nd":
                            if (isWay && way != null) {
                                var ref = Long.parseLong(xmlReader.getAttributeValue(null, "ref"));
                                way.addNode(model.getNodeIndex().getMember(ref));
                            }
                            break;
                        case "tag":
                            var k = xmlReader.getAttributeValue(null, "k");
                            var v = xmlReader.getAttributeValue(null, "v");
                            if (k.equals("building")) {
                                tags.add(Tag.BUILDING);
                                break;
                            }
                            if (k.equals("service")) {
                                break;
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

                            if (isNode) {
                                //Example from samsoe.osm of an addr tag:
                                //<tag k="addr:street" v="Havnevej"/>
                                if (k.equals("addr:street")) {
                                    model.getStreetTree().insert(v, node.getId());
                                }
                            }

                            break;
                        case "relation":
                            var relationId = Long.parseLong(xmlReader.getAttributeValue(null, "id"));
                            relation = new Relation(relationId);
                            model.addToRelationIndex(relation);
                            tags = new ArrayList<>();
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
                                    if (memberRef != null) {
                                        relation.addWay((Way) memberRef);
                                    }
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
                        case "node":
                            isNode = false;
                            tags = new ArrayList<>();
                            break;
                        case "way":
                            way.createRectangle();
                            addWayToList(way, tags, model);
                            tags = new ArrayList<>();
                            break;
                        case "relation":
                            relation.setTags(tags);
                            relation.createRectangle();
                            relation = null;
                            tags = new ArrayList<>();
                            break;
                    }
                    break;
            }
        }
        model.setIslands(mergeCoastlines(model.getCoastlines()));
    }

    public static void addWayToList(Way way, ArrayList<Tag> tags, Model model) {
        var drawableList = model.getDrawableList();
        var fillableList = model.getFillableList();
        RenderingStyle renderingStyle = new RenderingStyle();

        if (tags.contains(Tag.COASTLINE)) {
            model.addCoastline(way);
        } else {
            // naively determine whether or not the way is fillable by the first tagRenderingStyle
            if (tags.size() <= 0) return;

            way.setTags(tags);

            var drawStyle = renderingStyle.getDrawStyleByTag(tags.get(0));
            if (drawStyle == DrawStyle.FILL) {
                fillableList.add(way);
            } else {
                drawableList.add(way);
            }
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

    private static boolean isDublet(Member drawable, Tag tag, Map<Tag, List<Drawable>> map) {
        List<Drawable> listToCheck = map.get(tag);
        boolean isDublet = true;
        if (listToCheck.size() == 0 || listToCheck.get(listToCheck.size() - 1) != drawable) {
            isDublet = false;
        }
        return isDublet;
    }
}
