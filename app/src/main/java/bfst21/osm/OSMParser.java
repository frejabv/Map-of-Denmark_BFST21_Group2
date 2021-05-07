package bfst21.osm;

import bfst21.Model;
import bfst21.exceptions.UnsupportedFileTypeException;
import bfst21.search.RadixTree;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.zip.ZipInputStream;

public class OSMParser {
    private static HashMap<String, List<String>> addresses = new HashMap<>();

    public static void readMapElements(InputStream in, FileExtension fileExtension, String fileName, Model model)
            throws IOException, XMLStreamException {
        switch (fileExtension) {
        case OSM:
            loadOSM(in, model);
            break;
        case ZIP:
            loadZIP(in, model);
            //saveOBJ(fileName, model);
            break;
        case OBJ:
            loadOBJ(in, model);
            break;
        }
    }

    public static void loadOBJ(InputStream in, Model model) {
        try (var input = new ObjectInputStream(new BufferedInputStream(in))) {
            model.setFillMap((Map<Tag, List<Drawable>>) input.readObject());
            model.setNodeIndex((MemberIndex<Node>) input.readObject());
            model.setWayIndex((MemberIndex<Way>) input.readObject());
            model.setRelationIndex((MemberIndex<Relation>) input.readObject());
            model.setStreetTree((RadixTree) input.readObject());
            model.setIslands((ArrayList<Drawable>) input.readObject());
            model.setCoastlines((ArrayList<Way>) input.readObject());
            model.setMinX(input.readFloat());
            model.setMinY(input.readFloat());
            model.setMaxX(input.readFloat());
            model.setMaxY(input.readFloat());
            model.setDrawableMap((Map<Tag, List<Drawable>>) input.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveOBJ(String fileName, Model model) throws IOException {
        // Point java to the correct folder on the host machine
        URL fileURL = OSMParser.class.getResource("/bfst21/data/");
        File file = new File(fileURL.getPath() + fileName);

        if (!file.createNewFile()) {
            // Figure out whether or not we need to freak out if we are overwriting an existing obj file
        }

        try (var output = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath())))) {
            output.writeObject(model.getFillMap());
            output.writeObject(model.getNodeIndex());
            output.writeObject(model.getWayIndex());
            output.writeObject(model.getRelationIndex());
            output.writeObject(model.getStreetTree());
            output.writeObject(model.getIslands());
            output.writeObject(model.getCoastlines());
            output.writeFloat(model.getMinX());
            output.writeFloat(model.getMinY());
            output.writeFloat(model.getMaxX());
            output.writeFloat(model.getMaxY());
            output.writeObject(model.getDrawableMap());
        } catch (Exception e) {
            e.printStackTrace();
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
                            model.setMaxY(
                                    Float.parseFloat(xmlReader.getAttributeValue(null, "maxlat")) / -Model.scalingConstant);
                            model.setMinY(
                                    Float.parseFloat(xmlReader.getAttributeValue(null, "minlat")) / -Model.scalingConstant);
                            break;
                        case "node":
                            var id = Long.parseLong(xmlReader.getAttributeValue(null, "id"));
                            var lon = Float.parseFloat(xmlReader.getAttributeValue(null, "lon"));
                            var lat = Float.parseFloat(xmlReader.getAttributeValue(null, "lat"));
                            node = new Node(lon, lat, id);
                            model.addToNodeIndex(node);
                            isNode = true;
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

                            if (k.equals("service") || k.equals("surface")) {
                                break;
                            }

                            if (k.equals("name")) {
                                name = v;
                            }

                            if (k.equals("name") && isWay) {
                                way.setName(v);
                            }

                            if (k.equals("place")) {
                                if (v.equals("island") || v.equals("city") || v.equals("borough") || v.equals("suburb")
                                        || v.equals("quarter") || v.equals("neighbourhood") || v.equals("town")
                                        || v.equals("village") || v.equals("hamlet") || v.equals("islet")) {
                                    CityTypes cityType = CityTypes.valueOf(v.toUpperCase());
                                    if (isNode) {
                                        model.addToCityIndex(new City(name, cityType, node));
                                    } else if (isWay && relation == null) {
                                        model.addToCityIndex(new City(name, cityType, way));
                                    } else if (isWay && relation != null) {
                                        model.addToCityIndex(new City(name, cityType, relation));
                                    }
                                }
                            }

                            if (k.equals("maxspeed")) {
                                v.replace(" km", "").replace(" mph", "");
                                way.setMaxSpeed(Integer.parseInt(v));
                            }

                            if (k.equals("oneway")) {
                                if (v.equals("yes") || v.equals("true") || v.equals("1")) {
                                    way.setIsOneway();
                                }
                                if (v.equals("-1")) {
                                    Collections.reverse(way.getNodes());
                                    way.setIsOneway();
                                }
                            }

                            if (k.equals("junction") || v.equals("roundabout")) {
                                way.setIsJunction();
                                break;
                            }

                            if (k.startsWith("cycleway") || k.startsWith("bicycle")) {
                                if(!v.equals("no")) {
                                    way.getTags().add(Tag.CYCLEWAY);
                                }
                                break;
                            }

                            if ((k.equals("sidewalk") || k.equals("foot")) && !v.equals("no")) {
                                way.getTags().add(Tag.FOOTWAY);
                                break;
                            }

                            if (k.equals("landuse") && v.equals("residential")){
                                tags.add(Tag.CITYBOARDER);
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
                                // example from samsoe.osm of an addr tag:
                                // <tag k="addr:street" v="havnevej"/>
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
                            break;
                        case "way":
                            way.getTags().addAll(tags);
                            way.checkSpeed();
                            way.createRectangle();
                            addWayToList(way, tags, model);
                            break;
                        case "relation":
                            relation.setTags(tags);
                            relation.createRectangle();
                            relation = null;
                            break;
                    }
                    break;
            }
        }
        // TODO: Please fix (kinda fixed)
        model.setIslands(mergeCoastlines(model.getCoastlines()));
        if (model.getCoastlines() == null || model.getCoastlines().isEmpty()) {
            System.out.println("you fool, you think it is that simple? hahahahah");
        }
    }

    public static void addWayToList(Way way, List<Tag> tags, Model model) {
        var drawableMap = model.getDrawableMap();
        var fillMap = model.getFillMap();
        RenderingStyle renderingStyle = new RenderingStyle();

        for (var tag : tags) {
            if (tag == Tag.COASTLINE) {
                model.addCoastline(way);
            } else {
                var drawStyle = renderingStyle.getDrawStyleByTag(tag);

                if (drawStyle == DrawStyle.FILL) {
                    fillMap.putIfAbsent(tag, new ArrayList<>());
                    if (!isDublet(way, tag, fillMap)) {
                        fillMap.get(tag).add(way);
                    }
                } else {
                    drawableMap.putIfAbsent(tag, new ArrayList<>());
                    if (!isDublet(way, tag, drawableMap)) {
                        drawableMap.get(tag).add(way);
                    }
                }
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
        List listToCheck = map.get(tag);
        boolean isDublet = true;
        if (listToCheck.size() == 0 || listToCheck.get(listToCheck.size() - 1) != drawable) {
            isDublet = false;
        }
        return isDublet;
    }

    public static FileExtension genFileExtension(String filePath) {
        String[] filePathParts = filePath.split("\\.");

        FileExtension toReturn;

        switch (filePathParts[filePathParts.length - 1]) {
        case "osm":
            toReturn = FileExtension.OSM;
            break;
        case "zip":
            toReturn = FileExtension.ZIP;
            break;
        case "obj":
            toReturn = FileExtension.OBJ;
            break;
        default:
            throw new UnsupportedFileTypeException("Unsupported file type: " + filePathParts[filePathParts.length - 1]);
        }
        return toReturn;
    }
}
