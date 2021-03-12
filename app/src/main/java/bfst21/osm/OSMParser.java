package bfst21.osm;

import java.io.*;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import bfst21.Model;
import bfst21.osm.Node;
import bfst21.osm.Way;
import bfst21.exceptions.UnsupportedFileTypeException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import bfst21.osm.Drawable;


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
            if(splitFileName.length > 0){
                throw new UnsupportedFileTypeException("." + splitFileName[splitFileName.length - 1]);
            }
        }
    }

    private static void loadOSM(InputStream inputStream, Model model)
            throws XMLStreamException, FactoryConfigurationError {
        XMLStreamReader xmlReader = XMLInputFactory.newInstance()
                .createXMLStreamReader(new BufferedInputStream(inputStream));

        Tag tag = Tag.EMPTY;
        boolean isWay = false;
        Way way = null;

        ArrayList<Drawable> waysForRelation = null;
        //only relevant when we change from one tag to list of tags
        //ArrayList<Tag> tagsForRelation = null;

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
                    way = new Way();
                    //var wayID = Long.parseLong(xmlReader.getAttributeValue(null, "id"));
                    //model.addToWayIndex(way, id); ?? or new Way(id), or way.setID(id);
                    break;
                case "nd":
                    if (isWay && way != null) {
                        var ref = Long.parseLong(xmlReader.getAttributeValue(null, "ref"));
                        way.addNode(model.getNodeIndex().getNode(ref));
                    }
                    break;
                case "relation":
                    //isWay = true;
                    waysForRelation = new ArrayList<>();
                    //tagsForRelation = new ArrayList<>();
                    break;
                case "member":
                    /*if (xmlReader.getAttributeValue(null, "type").equals("way")) { //isWay && way != null
                        var ref = Long.parseLong(xmlReader.getAttributeValue(null, "ref"));
                        waysForRelation.addWay(model.getWayIndex().getWay(ref));
                    }*/
                    break;
                case "tag":
                    var k = xmlReader.getAttributeValue(null, "k");
                    var v = xmlReader.getAttributeValue(null, "v");
                    switch(k) {
                        case "natural":
                            switch(v) {
                                case "coastline":
                                    tag = Tag.COASTLINE;
                                    break;
                                case "water":
                                    tag = Tag.WATER;
                                    break;
                            }
                            break;
                        case "leisure":
                            switch(v) {
                                case "park":
                                    tag = Tag.PARK;
                                    break;
                            }
                            break;
                        case "highway":
                            switch (v) {
                                case "cycleway":
                                    tag = Tag.CYCLEWAY;
                                    break;
                                case "footway":
                                    tag = Tag.FOOTWAY;
                                    break;
                                case "junction":
                                    tag = Tag.JUNCTION;
                                    break;
                                case "living_street":
                                    tag = Tag.LIVING_STREET;
                                    break;
                                case "motorway":
                                    tag = Tag.MOTORWAY;
                                    break;
                                case "path":
                                    tag = Tag.PATH;
                                    break;
                                case "primary":
                                    tag = Tag.PRIMARY;
                                    break;
                                case "pedestrian":
                                    tag = Tag.PEDESTRIAN;
                                    break;
                                case "residential":
                                    tag = Tag.RESIDENTIAL;
                                    break;
                                case "road":
                                    tag = Tag.ROAD;
                                    break;
                                case "secondary":
                                    tag = Tag.SECONDARY;
                                    break;
                                case "service":
                                    tag = Tag.SERVICE;
                                    break;
                                case "tertiary":
                                    tag = Tag.TERTIARY;
                                    break;
                                case "track":
                                    tag = Tag.TRACK;
                                    break;
                                case "trunk":
                                    tag = Tag.TRUNK;
                                    break;
                                case "unclassified":
                                    tag = Tag.UNCLASSIFIED;
                                    break;
                            }
                            break;
                        case "building":
                            switch (v) {
                                default:
                                    tag = Tag.BUILDING;

                            }
                        case "border_type":
                            switch(v){
                                case "territorial":
                                    tag = Tag.TERRITORIALBORDER;
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
                    System.out.println(tag);
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
                        case EMPTY:
                            System.out.println("added way");
                            model.addWay(way);
                    }
                    way = null;
                    tag = Tag.EMPTY;
                    break;
                case "relation":
                    for(Drawable w : waysForRelation) {
                        //run the method with adding the way to the correct ArrayList with the help of tags.
                    }
                    break;
                }
                break;
            }
        }
        //TODO: Please fix (kinda fixed)
        model.setIslands(mergeCoastlines(model.getCoastlines()));
    }

    public static List<Drawable> mergeCoastlines(ArrayList<Way> coastlines){
        Map<Node,Way> pieces = new HashMap<>();
        for(var coast : coastlines){
            var before = pieces.remove(coast.first());
            var after = pieces.remove(coast.last());
            if(before == after) after = null;
            var merged = Way.merge(before,coast,after);
            pieces.put(merged.first(),merged);
            pieces.put(merged.last(),merged);
        }
        List<Drawable> merged = new ArrayList<>();
        pieces.forEach((node,way) -> {
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
