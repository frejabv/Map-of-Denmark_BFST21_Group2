package bfst21.osm;

import bfst21.Model;
import bfst21.POI;
import bfst21.exceptions.UnsupportedFileTypeException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;

import bfst21.Model;
import bfst21.exceptions.UnsupportedFileTypeException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class OSMParser {
    private static HashMap<String, List<String>> addresses = new HashMap<>();
    private static List<String> systemPOITags;
    static List<String> systemPoi = new ArrayList<>(Arrays.asList("cinema", "theatre", "sculpture", "statue", "aerodrome", "zoo", "aquarium", "attraction", "gallery", "museum", "theme_park", "viewpoint", "artwork", "building", "castle", "castle_wall"));


    public static void readMapElements(String filepath, Model model) throws IOException, XMLStreamException {
        if (filepath.endsWith(".osm")) {
            InputStream in = OSMParser.class.getResourceAsStream("/bfst21/data/" + filepath);
            loadOSM(in, model);
        } else if (filepath.endsWith(".zip")) {
            loadZIP(OSMParser.class.getResourceAsStream("/bfst21/data/" + filepath), model);
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
        systemPOITags = new ArrayList<>();
        Node node = null;
        Way way = null;
        Relation relation = null;
        String systemPOIName = "";

        boolean isWay = false;
        boolean isNode = false;

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
                    model.getKdTree().setBounds();
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
                    if (k.equals("name")){
                        systemPOIName = v;
                    }
                    if (k.equals("building")) {
                        tags.add(Tag.BUILDING);
                        break;
                    }
                    if (k.equals("service")) {
                        break;
                    }
                    if (k.equals("amenity") || k.equals("artwork_type") || k.equals("aeroway") || k.equals("tourism") || k.equals("historic")){
                        if (systemPoi.contains(v)) {
                            systemPOITags.add(v);
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
                        if (systemPOITags.size() > 0 && systemPOIName != ""){
                            //POI list can be kd-tree only
                            POI poi = createSystemPOI(systemPOIName,systemPOITags,node.getX(),node.getY());
                            model.addSystemPOI(poi);
                            model.getKdTree().insert(poi);
                            System.out.println(systemPOITags.size());
                            systemPOIName = "";
                            systemPOITags = new ArrayList<>();
                        }
                        isNode = false;
                        systemPOIName = "";
                        systemPOITags = new ArrayList<>();
                        break;
                    case "way":
                        if (systemPOITags.size() > 0 && systemPOIName != ""){
                            model.addSystemPOI(createSystemPOI(systemPOIName,systemPOITags, way.first().getX(),way.first().getY()));
                            System.out.println(systemPOITags.size());
                            systemPOIName = "";
                            systemPOITags = new ArrayList<>();
                        }
                        addWayToList(way, tags, model);
                        systemPOIName = "";
                        systemPOITags = new ArrayList<>();
                        break;
                    case "relation":
                        if (systemPOITags.size() > 0 && systemPOIName != ""){
                            model.addSystemPOI(createSystemPOI(systemPOIName,systemPOITags, relation.ways.get(0).first().getX(),relation.ways.get(0).first().getY()));
                            System.out.println(systemPOITags.size());
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
        System.out.println("Illegal Argument Exceptions: T3:" + KDTree.IAE3Counter + " T4:"+KDTree.IAE4Counter);
        System.out.println("Nodes out of bounds = " + KDTree.outOfBoundsCounter);
    }

    private static POI createSystemPOI(String systemPOIName, List<String> systemPOITags, float x, float y) {
        String type = "car";
        String imageType = "car";
        /*int priority = 10;
        //List<String> systemPoi = new ArrayList<>(Arrays.asList("attraction", "viewpoint", "artwork", "building"));
        //From specific tags to more general
        for(String tag :systemPOITags) {
            if (imageType.equals("")) {
                switch (tag) {
                    case "bust":
                    case "statue":
                    case "sculpture":
                        imageType = "statue";
                        priority = 10;
                        break;
                    case "theme_park":
                        imageType = "theme_park";
                        priority = 10;
                        break;
                    case "attraction":
                        if (priority < 10){
                            imageType = "attraction";
                            priority = 5;
                        }
                        break;
                }
            }
        }
        else if (systemPOITags.contains("cinema") || systemPOITags.contains("theatre")){
            imageType = "cinema";
        } else if (systemPOITags.contains("gallery") || systemPOITags.contains("museum")){
            imageType = "museum";
        } else if (systemPOITags.contains("castle") || systemPOITags.contains("castle_wall")){
            imageType = "castle";
        } else if (systemPOITags.contains("aerodrome")){
            imageType = "aerodrome";
        } else if (systemPOITags.contains("zoo")){
            imageType = "zoo";
        } else if (systemPOITags.contains("aquarium")){
            imageType = "aquarium";
        } else if (systemPOITags.contains("theme_park")){
            imageType = "theme_park";
        }*/
        //sanitise type
        POI result = new POI(systemPOIName,type, imageType,x,y);
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
