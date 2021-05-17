package bfst21.osm;

import bfst21.Model;
import bfst21.POI.POI;
import bfst21.POI.POI_KDTree;
import bfst21.pathfinding.Vertex;
import bfst21.search.RadixTree;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipInputStream;

public class OSMParser {
    private static List<String> systemPoiTags;
    private static List<String> systemPoi = new ArrayList<>(Arrays.asList("cinema", "theatre", "sculpture", "statue",
            "aerodrome", "zoo", "aquarium", "attraction", "gallery", "museum", "theme_park", "viewpoint", "artwork",
            "building", "castle", "castle_wall", "windmill", "lighthouse", "bust", "statue", "sculpture"));

    public static void readMapElements(InputStream in, FileExtension fileExtension, String fileName, Model model)
            throws IOException, XMLStreamException, ClassNotFoundException {
        switch (fileExtension) {
            case OSM:
                loadOSM(in, model);
                break;
            case ZIP:
                loadZIP(in, model);
                saveOBJ(fileName, model);
                break;
            case OBJ:
                loadOBJ(in, model);
                break;
        }
    }

    public static void loadOBJ(InputStream in, Model model) throws IOException, ClassNotFoundException {
        var input = new ObjectInputStream(new BufferedInputStream(in));
        {
            Object fillMap = input.readObject();
            if (fillMap instanceof Map) {
                model.setFillMap((Map<Tag, List<Drawable>>) fillMap);
            }
            Object nodeIndex = input.readObject();
            if (nodeIndex instanceof MemberIndex) {
                model.setNodeIndex((MemberIndex<Node>) nodeIndex);
            }
            Object wayIndex = input.readObject();
            if (wayIndex instanceof MemberIndex) {
                model.setWayIndex((MemberIndex<Way>) wayIndex);
            }
            Object relationIndex = input.readObject();
            if (relationIndex instanceof MemberIndex) {
                model.setRelationIndex((MemberIndex<Relation>) relationIndex);
            }
            Object streetTree = input.readObject();
            if (streetTree instanceof RadixTree) {
                model.setStreetTree((RadixTree) streetTree);
            }
            Object islands = input.readObject();
            if (islands instanceof ArrayList) {
                model.setIslands((ArrayList<Drawable>) islands);
            }
            Object coastlines = input.readObject();
            if (coastlines instanceof ArrayList) {
                model.setCoastlines((ArrayList<Way>) coastlines);
            }
            model.setMinX(input.readFloat());
            model.setMinY(input.readFloat());
            model.setMaxX(input.readFloat());
            model.setMaxY(input.readFloat());
            Object drawableMap = input.readObject();
            if (drawableMap instanceof Map) {
                model.setDrawableMap((Map<Tag, List<Drawable>>) drawableMap);
            }

            Object poiTree = input.readObject();
            if (poiTree instanceof POI_KDTree) {
                model.setPOITree((POI_KDTree) poiTree);
            }

            Object areaNames = input.readObject();
            if (areaNames instanceof ArrayList) {
                model.setAreaNames((List<Drawable>) areaNames);
            }

            Object vertexIndex = input.readObject();
            if (vertexIndex instanceof List) {
                model.setVertexIndex((ArrayList<Vertex>) vertexIndex);
            }
        }
    }

    public static void saveOBJ(String fileName, Model model) throws IOException {
        // Point java to the correct folder on the host machine
        URL fileURL = OSMParser.class.getResource("/bfst21/data/");
        File file = new File(fileURL.getPath() + "/" + fileName + ".obj");
        file.createNewFile();

        var output = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath())));
        {
            output.writeObject(model.getFillMap());
            output.writeObject(model.getNodeIndex());
            output.writeObject(model.getWayIndex());
            output.writeObject(model.getRelationIndex());
            output.writeObject(model.getStreetTree());
            output.writeObject(model.getIslands());
            output.writeFloat(model.getMinX());
            output.writeFloat(model.getMinY());
            output.writeFloat(model.getMaxX());
            output.writeFloat(model.getMaxY());
            output.writeObject(model.getDrawableMap());
            output.writeObject(model.getPOITree());
            output.writeObject(model.getAreaNames());
            output.writeObject(model.getVertexIndex());
        }
    }

    private static void loadOSM(InputStream inputStream, Model model)
            throws XMLStreamException, FactoryConfigurationError {
        XMLStreamReader xmlReader = XMLInputFactory.newInstance()
                .createXMLStreamReader(new BufferedInputStream(inputStream));
        Tag tag = null;
        systemPoiTags = new ArrayList<>();
        Node node = null;
        Way way = null;
        Relation relation = null;

        boolean isWay = false;
        boolean isNode = false;
        String name = "";
        String systemPOIName = "";

        String streetName = "";
        String houseNumber = "";
        String postcode = "";
        String city = "";

        while (xmlReader.hasNext()) {
            switch (xmlReader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    switch (xmlReader.getLocalName()) {
                        case "bounds":
                            model.setMinX(Float.parseFloat(xmlReader.getAttributeValue(null, "minlon")));
                            model.setMaxX(Float.parseFloat(xmlReader.getAttributeValue(null, "maxlon")));
                            model.setMaxY(Float.parseFloat(xmlReader.getAttributeValue(null, "maxlat"))
                                    / -Model.scalingConstant);
                            model.setMinY(Float.parseFloat(xmlReader.getAttributeValue(null, "minlat"))
                                    / -Model.scalingConstant);
                            model.getPOITree().setBounds(model.getMinX(), model.getMaxY(), model.getMaxX(),
                                    model.getMinY());
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

                            if (k.equals("service") || k.equals("surface") || v.equals("construction")
                                    || k.equals("construction") || v.equals("proposed") || k.equals("proposed")
                                    || k.startsWith("destination")) {
                                break;
                            }

                            if (k.equals("building")) {
                                tag = Tag.BUILDING;
                                break;
                            }

                            if (k.equals("amenity") || k.equals("artwork_type") || k.equals("aeroway")
                                    || k.equals("tourism") || k.equals("historic")) {
                                if (systemPoi.contains(v)) {
                                    systemPoiTags.add(v);
                                }
                            }

                            if (k.equals("name")) {
                                name = v;
                                systemPOIName = v;
                                if (isWay && way != null) {
                                    way.setName(name);
                                }
                            }

                            if (k.equals("place")) {
                                if (v.equals("island") || v.equals("city") || v.equals("borough") || v.equals("suburb")
                                        || v.equals("quarter") || v.equals("neighbourhood") || v.equals("town")
                                        || v.equals("village") || v.equals("hamlet") || v.equals("islet")) {
                                    AreaType areaType = AreaType.valueOf(v.toUpperCase());
                                    if (isNode) {
                                        model.addToAreaNamesIndex(new AreaName(name, areaType, node));
                                    } else if (isWay && relation == null) {
                                        model.addToAreaNamesIndex(new AreaName(name, areaType, way));
                                    } else if (isWay && relation != null) {
                                        model.addToAreaNamesIndex(new AreaName(name, areaType, relation));
                                    }
                                }
                            }

                            if (way != null) {
                                if (k.equals("maxspeed")) {
                                    v = v.replaceAll("\\D+", "");
                                    if (!v.equals("")) {
                                        int speed = (int) Math.round(Double.parseDouble(v));
                                        way.setMaxSpeed(speed);
                                    }
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
                                    if (!v.equals("no")) {
                                        way.setIsCyclable();
                                    }
                                    break;
                                }

                                if ((k.equals("sidewalk") || k.startsWith("foot")) && !v.equals("no")) {
                                    way.setIsWalkable();
                                    break;
                                }
                            }

                            if (k.equals("landuse") && v.equals("residential")) {
                                tag = Tag.CITYBORDER;
                                break;
                            }

                            if (k.equals("ferry")) {
                                tag = Tag.FERRY;
                                break;
                            }

                            if (v.equals("sand") || v.equals("beach")) {
                                if (k.equals("natural")) {
                                    tag = Tag.BEACH;
                                }
                                break;
                            }

                            try {
                                tag = Tag.valueOf(v.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                // We don't care about tags not in our Tag enum
                            }

                            try {
                                tag = Tag.valueOf(k.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                // We don't care about tags not in our Tag enum
                            }

                            if (isNode) {
                                if (k.equals("addr:street")) {
                                    streetName = v;
                                }
                                if (k.equals("addr:housenumber")) {
                                    houseNumber = v;
                                }
                                if (k.equals("addr:postcode")) {
                                    postcode = v;
                                }
                                if (k.equals("addr:city")) {
                                    city = v;
                                }
                            }
                            break;
                        case "relation":
                            var relationId = Long.parseLong(xmlReader.getAttributeValue(null, "id"));
                            relation = new Relation(relationId);
                            model.addToRelationIndex(relation);
                            break;
                        case "member":
                            var type = xmlReader.getAttributeValue(null, "type");
                            var ref = Long.parseLong(xmlReader.getAttributeValue(null, "ref"));
                            var role = xmlReader.getAttributeValue(null, "role");
                            if (type.equals("way")) {
                                Way memberRef = model.getWayIndex().getMember(ref);
                                if (memberRef != null) {
                                    relation.addWay(memberRef);
                                    memberRef.addRole(relation.getId(), role);
                                }
                            }
                            break;
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    switch (xmlReader.getLocalName()) {
                        case "node":
                            if (systemPoiTags.size() > 0 && systemPOIName != "") {
                                newSystemPOI(model, systemPOIName, node.getX(), node.getY());
                            }
                            if (!streetName.equals("") && !houseNumber.equals("") && !postcode.equals("")
                                    && !city.equals("")) {

                                model.getStreetTree().insert(streetName,
                                        " " + houseNumber + " " + postcode + " " + city, node.getId());
                            }
                            isNode = false;
                            tag = null;
                            systemPOIName = "";
                            systemPoiTags = new ArrayList<>();
                            name = "";
                            break;
                        case "way":
                            if (systemPoiTags.size() > 0 && !systemPOIName.equals("")) {
                                newSystemPOI(model, systemPOIName, way.first().getX(), way.first().getY());
                            }
                            if (tag != null) {
                                way.setTag(tag);
                                addWayToList(way, tag, model);
                            }
                            way.checkSpeed();
                            way.createRectangle();
                            tag = null;
                            systemPOIName = "";
                            systemPoiTags = new ArrayList<>();
                            name = "";
                            break;
                        case "relation":
                            if (systemPoiTags.size() > 0 && !systemPOIName.equals("")) {
                                if (relation.getWays() != null || !relation.getWays().isEmpty()) {
                                    newSystemPOI(model, systemPOIName, relation.getWays().get(0).first().getX(), relation.getWays().get(0).first().getY());
                                }
                            }
                            if (tag != null) {
                                relation.setTag(tag);
                            }
                            relation.createRectangle();
                            relation = null;
                            tag = null;
                            systemPOIName = "";
                            systemPoiTags = new ArrayList<>();
                            name = "";
                            break;
                    }
                    break;
            }
        }
        model.setIslands(mergeCoastlines(model.getCoastlines()));
        model.setCoastlines(null);
    }

    private static void newSystemPOI(Model model, String systemPOIName, float x, float y) {
        POI poi = createSystemPOI(systemPOIName, systemPoiTags, x, y);
        model.addSystemPOI(poi);
        model.getPOITree().insert(poi);
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
                } else if (systemPOITags.contains("statue") || systemPOITags.contains("bust")
                        || systemPOITags.contains("sculpture")) {
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
        return new POI(systemPOIName, type, imageType, x, y);
    }

    public static void addWayToList(Way way, Tag tag, Model model) {
        var drawableMap = model.getDrawableMap();
        var fillMap = model.getFillMap();
        RenderingStyle renderingStyle = new RenderingStyle();

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
                    for (Node node : way.getNodes()) {
                        model.getVertexMap().putIfAbsent(node, new Vertex(node.getX(), node.getY(), node.getId()));
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

    public static FileExtension genFileExtension(String filePath) throws IOException {
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
                throw new IOException("Unsupported file type: " + filePathParts[filePathParts.length - 1]);
        }
        return toReturn;
    }
}

