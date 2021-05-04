package bfst21.osm;

import bfst21.Model;
import bfst21.POI;
import bfst21.exceptions.UnsupportedFileTypeException;
import bfst21.search.RadixTree;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipInputStream;

public class OSMParser {
    private static HashMap<String, List<String>> addresses = new HashMap<>();
    private static List<String> systemPOITags;
    static List<String> systemPoi = new ArrayList<>(Arrays.asList("cinema", "theatre", "sculpture", "statue", "aerodrome", "zoo", "aquarium", "attraction", "gallery", "museum", "theme_park", "viewpoint", "artwork", "building", "castle", "castle_wall", "windmill", "lighthouse", "bust", "statue", "sculpture"));


    public static void readMapElements(String filepath, Model model) throws IOException, XMLStreamException {
        if (filepath.endsWith(".osm")) {
            InputStream in = OSMParser.class.getResourceAsStream("/bfst21/data/" + filepath);
            loadOSM(in, model);
        } else if (filepath.endsWith(".zip")) {
            loadZIP(OSMParser.class.getResourceAsStream("/bfst21/data/" + filepath), model);
            //saveOBJ(filepath + ".obj", model);
        } else if (filepath.endsWith(".obj")) {
            try {
                loadOBJ(filepath, model);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            String[] splitFileName = filepath.split("\\.");
            if (splitFileName.length > 0) {
                throw new UnsupportedFileTypeException("." + splitFileName[splitFileName.length - 1]);
            }
        }
    }

    public static void loadOBJ(String filename, Model model) throws IOException, ClassNotFoundException {
        try (var input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(OSMParser.class.getResource("/bfst21/data/" + filename).getFile())))) {
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

    public static void saveOBJ(String filename, Model model) throws IOException {
        File file = null;
        try {
            file = Paths.get(OSMParser.class.getResource("/bfst21/data/" + filename).toURI()).toFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try (var output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath())))) {
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
        systemPOITags = new ArrayList<>();
        Node node = null;
        Way way = null;
        Relation relation = null;
        String systemPOIName = "";

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
                            model.getPOITree().setBounds();
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
                            if (k.equals("name")) {
                                systemPOIName = v;
                            }
                            if (k.equals("building")) {
                                tags.add(Tag.BUILDING);
                                break;
                            }
                            if (k.equals("service")) {
                                break;
                            }
                            if (k.equals("amenity") || k.equals("artwork_type") || k.equals("aeroway") || k.equals("tourism") || k.equals("historic") || k.equals("man_made")) {
                                if (systemPoi.contains(v)) {
                                    systemPOITags.add(v);
                                }
                            }
                            if (k.equals("name")) {
                                name = v;
                            }
                            if (k.equals("place")) {
                                if (v.equals("island") || v.equals("city") || v.equals("borough") || v.equals("suburb")
                                        || v.equals("quarter") || v.equals("neighbourhood") || v.equals("town")
                                        || v.equals("village") || v.equals("hamlet") || v.equals("islet")) {
                                    CityTypes cityType = CityTypes.valueOf(v.toUpperCase());
                                    if (isNode) {
                                        model.addToCityIndex(new City(name, cityType, node.getX(), node.getY()));
                                    } else if (isWay && relation == null) {
                                        model.addToCityIndex(new City(name, cityType, way.first().getX(), way.first().getY()));
                                    } else if (isWay && relation != null) {
                                        model.addToCityIndex(new City(name, cityType, relation.ways.get(0).first().getX(),
                                                relation.ways.get(0).first().getY()));
                                    }
                                }
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
                                // Example from samsoe.osm of an addr tag:
                                // <tag k="addr:street" v="Havnevej"/>
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
                            if (systemPOITags.size() > 0 && systemPOIName != "") {
                                //POI list can be kd-tree only
                                POI poi = createSystemPOI(systemPOIName, systemPOITags, node.getX(), node.getY());
                                model.addSystemPOI(poi);
                                model.getPOITree().insert(poi);
                                systemPOIName = "";
                                systemPOITags = new ArrayList<>();
                            }
                            isNode = false;
                            systemPOIName = "";
                            systemPOITags = new ArrayList<>();
                            break;
                        case "way":
                            if (systemPOITags.size() > 0 && systemPOIName != "") {
                                POI poi = createSystemPOI(systemPOIName, systemPOITags, way.first().getX(), way.first().getY());
                                model.addSystemPOI(poi);
                                model.getPOITree().insert(poi);
                                systemPOIName = "";
                                systemPOITags = new ArrayList<>();
                            }
                            addWayToList(way, tags, model);
                            systemPOIName = "";
                            systemPOITags = new ArrayList<>();
                            break;
                        case "relation":
                            if (systemPOITags.size() > 0 && systemPOIName != "") {
                                POI poi = createSystemPOI(systemPOIName, systemPOITags, relation.ways.get(0).first().getX(), relation.ways.get(0).first().getY());
                                model.addSystemPOI(poi);
                                model.getPOITree().insert(poi);
                                systemPOIName = "";
                                systemPOITags = new ArrayList<>();
                            }

                            relation.setTags(tags);
                            relation = null;
                            systemPOIName = "";
                            systemPOITags = new ArrayList<>();
                            break;
                    }
                    break;
            }
        }
        // TODO: Please fix (kinda fixed)
        model.setIslands(mergeCoastlines(model.getCoastlines()));
        System.out.println("coastlines: " + model.getCoastlines());
    }

    private static POI createSystemPOI(String systemPOIName, List<String> systemPOITags, float x, float y) {
        String type = "default";
        String imageType = "default";
        int priority = 0;
        for (String tag : systemPOITags) {
            if (priority != 10) {
                if (systemPOITags.contains("windmill")) {
                    imageType = "mill";
                    type = tag;
                    priority = 10;
                } else if (systemPOITags.contains("gallery") || systemPOITags.contains("museum")) {
                    imageType = "museum";
                    type = tag;
                    priority = 10;
                } else if (systemPOITags.contains("theme_park")) {
                    imageType = "theme_park";
                    type = tag;
                    priority = 10;
                } else if (systemPOITags.contains("aerodrome")) {
                    imageType = "aerodrome";
                    type = tag;
                    priority = 10;
                } else if (systemPOITags.contains("cinema") || systemPOITags.contains("theatre")) {
                    imageType = "cinema";
                    type = tag;
                    priority = 10;
                } else if (systemPOITags.contains("castle") || systemPOITags.contains("castle_wall")) {
                    imageType = "castle";
                    type = tag;
                    priority = 10;
                } else if (systemPOITags.contains("lighthouse")) {
                    imageType = "viewpoint";
                    type = tag;
                    priority = 10;
                } else if (systemPOITags.contains("statue") || systemPOITags.contains("bust") || systemPOITags.contains("sculpture")) {
                    imageType = "statue";
                    type = tag;
                    priority = 10;
                } else if (systemPOITags.contains("zoo")) {
                    imageType = "zoo";
                    type = tag;
                    priority = 10;
                } else if (systemPOITags.contains("attraction")) {
                    imageType = "suitcase";
                    type = tag;
                    priority = 5;
                } else if (systemPOITags.contains("viewpoint")) {
                    imageType = "viewpoint";
                    type = tag;
                    priority = 5;
                }
            }
        }
        //sanitise type
        POI result = new POI(systemPOIName, type, imageType, x, y);
        return result;
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
            if (before == after) after = null;
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
}
