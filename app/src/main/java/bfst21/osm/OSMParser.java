package bfst21.osm;

import java.io.*;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import bfst21.Model;
import bfst21.exceptions.UnsupportedFileTypeException;

import javafx.geometry.Point2D;

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
            //TODO
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
                            var nodeId = Long.parseLong(xmlReader.getAttributeValue(null, "id"));
                            var lon = Float.parseFloat(xmlReader.getAttributeValue(null, "lon"));
                            var lat = Float.parseFloat(xmlReader.getAttributeValue(null, "lat"));
                            System.out.println("input: nodeID = " + nodeId + " lon: " + lon + " lat: " + lat);
                            Node n = model.getKdTree().insert(new Point2D(lon,lat), nodeId);
                            System.out.println("node successfully created");
                            model.addToNodeIndex(n);
                            System.out.println("node successfully added to nodeIndex");
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
                        case "tag":
                            var k = xmlReader.getAttributeValue(null, "k");
                            var v = xmlReader.getAttributeValue(null, "v");
                            switch (k) {
                                case "natural":
                                    switch (v) {
                                        case "coastline":
                                            tags.add(Tag.COASTLINE);
                                            break;
                                        case "water":
                                            tags.add(Tag.WATER);
                                            break;
                                    }
                                    break;
                                case "leisure":
                                    switch (v) {
                                        case "park":
                                            tags.add(Tag.PARK);
                                            break;
                                    }
                                    break;
                                case "highway":
                                    switch (v) {
                                        case "cycleway":
                                            tags.add(Tag.CYCLEWAY);
                                            break;
                                        case "footway":
                                            tags.add(Tag.FOOTWAY);
                                            break;
                                        case "junction":
                                            tags.add(Tag.JUNCTION);
                                            break;
                                        case "living_street":
                                            tags.add(Tag.LIVING_STREET);
                                            break;
                                        case "motorway":
                                            tags.add(Tag.MOTORWAY);
                                            break;
                                        case "path":
                                            tags.add(Tag.PATH);
                                            break;
                                        case "primary":
                                            tags.add(Tag.PRIMARY);
                                            break;
                                        case "pedestrian":
                                            tags.add(Tag.PEDESTRIAN);
                                            break;
                                        case "residential":
                                            tags.add(Tag.RESIDENTIAL);
                                            break;
                                        case "road":
                                            tags.add(Tag.ROAD);
                                            break;
                                        case "secondary":
                                            tags.add(Tag.SECONDARY);
                                            break;
                                        case "service":
                                            tags.add(Tag.SERVICE);
                                            break;
                                        case "tertiary":
                                            tags.add(Tag.TERTIARY);
                                            break;
                                        case "track":
                                            tags.add(Tag.TRACK);
                                            break;
                                        case "trunk":
                                            tags.add(Tag.TRUNK);
                                            break;
                                        case "unclassified":
                                            tags.add(Tag.UNCLASSIFIED);
                                            break;
                                    }
                                    break;
                                case "building":
                                    switch (v) {
                                        default:
                                            tags.add(Tag.BUILDING);

                                    }
                                case "border_type":
                                    switch (v) {
                                        case "territorial":
                                            tags.add(Tag.TERRITORIALBORDER);
                                            break;
                                    }
                                    break;
                            }
                            break;
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    switch (xmlReader.getLocalName()) {
                        case "way":
                            System.out.println(tags);
                            if (tags.isEmpty()) {
                                model.addWay(way);
                            }
                            for (Tag tag : tags) {
                                switch (tag) {
                                    case BUILDING:
                                        model.addBuilding(way);
                                        break;
                                    case COASTLINE:
                                        model.addCoastline(way);
                                        break;
                                    case CYCLEWAY:
                                        model.addCycleway(way);
                                        break;
                                    case FOOTWAY:
                                        model.addFootway(way);
                                        break;
                                    case HIGHWAY:
                                        model.addHighway(way);
                                        break;
                                    case JUNCTION:
                                        model.addJunction(way);
                                        break;
                                    case LIVING_STREET:
                                        model.addLiving_street(way);
                                        break;
                                    case MOTORWAY:
                                        model.addMotorway(way);
                                        break;
                                    case PARK:
                                        model.addPark(way);
                                        break;
                                    case PATH:
                                        model.addPath(way);
                                        break;
                                    case PEDESTRIAN:
                                        model.addPedestrianWay(way);
                                        break;
                                    case PRIMARY:
                                        model.addPrimaryWay(way);
                                        break;
                                    case RESIDENTIAL:
                                        model.addResidentialWay(way);
                                        break;
                                    case ROAD:
                                        model.addRoad(way);
                                        break;
                                    case SECONDARY:
                                        model.addSecondaryWay(way);
                                        break;
                                    case SERVICE:
                                        model.addServiceWay(way);
                                        break;
                                    case TERTIARY:
                                        model.addTertiaryWay(way);
                                        break;
                                    case TRUNK:
                                        model.addTrunkWay(way);
                                        break;
                                    case TRACK:
                                        model.addTrackWay(way);
                                        break;
                                    case TERRITORIALBORDER:
                                        break;
                                    case UNCLASSIFIED:
                                        model.addUnclassifiedWay(way);
                                        break;
                                    case WATER:
                                        model.addWater(way);
                                        break;
                                }
                            }
                            isWay = false;
                            way = null;

                            break;
                        case "relation":

                            isRelation = false;
                            relation = null;
                            break;
                    }
                    break;
            }
        }
        System.out.println("hi");
        //TODO: Please fix (kinda fixed)
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
}
