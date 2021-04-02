package bfst21.osm;

import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import bfst21.Model;
import bfst21.exceptions.UnsupportedFileTypeException;

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
        Way way = null;
        Relation relation = null;
        RadixTree tree = new RadixTree();
        HashSet<String> set = new HashSet<>();

        boolean isWay = false;
        boolean isRelation = false;
        boolean isNode = false;
        boolean addressReceived = false;

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
                    isNode = true;
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
                    if(k.equals("building")){
                        tags.add(Tag.BUILDING);
                        break;
                    }
                    if(k.equals("service")){
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

                    if(isNode) {
                        if(k.contains("addr:")){
                            saveAddressData(k.replace("addr:", ""), v);
                            addressReceived = true;
                        }
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
                        addDrawableToList(way, tags, model);
                        break;
                    case "relation":
                        if(isRelation) {
                            List<Member> members = relation.getMembers();
                            for(Member member : members) {
                                addDrawableToList(member, tags, model);
                            }
                        }
                        isRelation = false;
                        relation = null;
                        break;
                    case "node":
                        if(addressReceived) {
                            int size = addresses.get("street").size();
                            String one = addresses.get("street").get(size - 1);
                            size = addresses.get("housenumber").size();
                            String two = addresses.get("housenumber").get(size - 1);
                            String strNumber = one + " " + two;
                            tree.insert(one, 1);
                            set.add(one);
                            size = addresses.get("postcode").size();
                            one = addresses.get("postcode").get(size-1);
                            size = addresses.get("city").size();
                            two = addresses.get("city").get(size-1);
                            //System.out.println(strNumber + ", " + one + " " + two);
                        }
                        isNode = false;
                        addressReceived = false;
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

        //RadixTree tree = new RadixTree();
        /*tree.insert("test",1);
        tree.insert("highroad",1);
        tree.insert("roadkill",1);
        tree.insert("road",1);
        tree.insert("tester",1);
        tree.insert("toast",1);
        tree.insert("road",1);
        System.out.println("lookup result for roadkill: " + tree.lookup("roadkill"));
        System.out.println("lookup result for toast: " + tree.lookup("toast"));
        System.out.println("lookup result for tester: " + tree.lookup("tester"));*/
        System.out.println(tree.getSize());
        System.out.println(set.size());
        ArrayList<RadixNode> suggestions =  tree.getSuggestions("A");
        for (RadixNode r: suggestions) {
            System.out.println(r.getContent());
        }
        System.out.println("lookup result for Grøvten: " + tree.lookup("Grøvten"));
        System.out.println("lookup result for Anemonevej: " + tree.lookup("Anemonevej"));
        System.out.println("lookup result for Alstrup: " + tree.lookup("Alstrup"));
        System.out.println("lookup result for Alstrupvej: " + tree.lookup("Alstrupvej"));
        System.out.println("lookup result for Agerup: " + tree.lookup("Agerup"));





        /*try {
            writeAddressesToFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }*/
    }

    public static void addDrawableToList(Member drawable, List<Tag> tags, Model model) {
        var drawableMap = model.getDrawableMap();
        var fillMap = model.getFillMap();
        RenderingStyle renderingStyle = new RenderingStyle();

        for (var tag : tags) {
            if (tag == Tag.COASTLINE) {
                model.addCoastline((Way) drawable);
            } else {
                var drawStyle = renderingStyle.getDrawStyleByTag(tag);

                if (drawStyle == DrawStyle.FILL) {
                    fillMap.putIfAbsent(tag, new ArrayList<>());
                    if(!isDublet(drawable, tag, fillMap)) {
                        fillMap.get(tag).add((Drawable) drawable);
                    }
                } else {
                    drawableMap.putIfAbsent(tag, new ArrayList<>());
                    if(!isDublet(drawable, tag, drawableMap)) {
                        drawableMap.get(tag).add((Drawable) drawable);
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
        loadOSM(zip,model);
    }

    public static void saveAddressData(String dataset, String data){
        addresses.putIfAbsent(dataset, new ArrayList<>());
        addresses.get(dataset).add(data);
    }

    public static void writeAddressesToFile() throws IOException{
        for(Map.Entry<String,List<String>> entry : addresses.entrySet()) {
            String dataset = entry.getKey();
            System.out.println(dataset);
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/" + dataset + ".txt"));
            for(String data : entry.getValue()) {
                writer.append(data + '\n');
            }
            writer.close();
        }
    }

    private static boolean isDublet(Member drawable, Tag tag, Map<Tag, List<Drawable>> map){
        List listToCheck = map.get(tag);
        boolean isDublet = true;
        if(listToCheck.size()==0 || listToCheck.get(listToCheck.size()-1) != drawable) {
            isDublet = false;
        }
        return isDublet;
    }
}
