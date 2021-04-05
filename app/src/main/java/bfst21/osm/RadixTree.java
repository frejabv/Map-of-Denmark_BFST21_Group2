package bfst21.osm;

import java.util.ArrayList;
import java.util.LinkedList;

public class RadixTree {
    private RadixNode root;
    private int size;
    private int places;
    private String fullName;

    public RadixTree() {
        root = new RadixNode("");
        size = 1;
        places = 0;
    }

    public int getSize() {
        System.out.println("places: " + places);
        return size;
    }

    public ArrayList<RadixNode> getSuggestions(String searchTerm) {
        searchTerm = searchTerm.substring(0,1).toUpperCase() + searchTerm.substring(1);
        ArrayList<RadixNode> suggestions = new ArrayList<>();
        LinkedList<RadixNode> queue = new LinkedList<>();
        int listItems = 0;
        boolean first = true;
        RadixNode currentNode;

        queue.add(lookupNode(searchTerm));
        while (listItems <= 8 && !queue.isEmpty()) {
            currentNode = queue.remove();
            if (first && currentNode.isPlace()) {
                suggestions.add(currentNode);
            }
            System.out.println("Suggestions, currentnode: " + currentNode.getContent());
            ArrayList<RadixNode> children = currentNode.getChildren();
            for (int i = 0; i < children.size(); i++) {
                System.out.println("Suggestions, child: " + children.get(i).getContent());
                queue.add(children.get(i));
                if (children.get(i).isPlace()) {
                    suggestions.add(children.get(i));
                    listItems++;
                }
            }
            first = false;
        }
        return suggestions;
    }

    //looking through the tree using (...) to find a full match for the searchterm
    //if that does not exist we settle on any part matches we found, if none exist we return null
    public RadixNode lookupNode(String searchTerm) {
        RadixNode currentNode = root;
        int charLength = 0;
        String result = "";
        RadixNode safeNode = null;

        while (!result.equals(searchTerm)) {
            boolean foundChild = false;
            ArrayList<RadixNode> children = currentNode.getChildren();
            System.out.println("Currentnode: " + currentNode.getContent());

            for (int i = 0; i < children.size(); i++) {
                System.out.println(charLength);
                System.out.println("A child: " + children.get(i).getContent());
                if (searchTerm.length() > charLength) {
                    //somehow find part matches, but still be strict
                    //System.out.println(children.get(i).getContent().startsWith(searchTerm.substring(charLength)) + " a child starts with: " + searchTerm);
                    if (children.get(i).getContent().startsWith(searchTerm.substring(charLength))) {
                        System.out.println("A safe node was created");
                        safeNode = children.get(i);
                    }
                    if (searchTerm.substring(charLength).startsWith(children.get(i).getContent())) { //|| children.get(i).getContent().startsWith(searchTerm)
                        System.out.println("We got something bois, reel it in");
                        safeNode = children.get(i);
                        currentNode = children.get(i);
                        foundChild = true;
                        charLength += currentNode.getContent().length();
                    }
                }
            }

            result += currentNode.getContent();
            System.out.println("Result: " + result);

            if (!foundChild) {
                if (safeNode != null) {
                    System.out.println("Safenode was used");
                    return safeNode;
                }
                return null;
            }
        }
        return currentNode;
    }

    public String lookup(String searchTerm) {
        RadixNode currentNode = root;
        int charLength = 0;
        String result = "";

        while (!result.equals(searchTerm)) { //!currentNode.isPlace() &&
            boolean foundChild = false;
            ArrayList<RadixNode> children = currentNode.getChildren();

            for (int i = 0; i < children.size(); i++) {
                if (searchTerm.substring(charLength).startsWith(children.get(i).getContent())) {
                    currentNode = children.get(i);
                    foundChild = true;
                    charLength += currentNode.getContent().length();
                }
            }

            result += currentNode.getContent();
            System.out.println(result);

            if (!foundChild) {
                return null;
            }
        }
        return result;
    }


    public void insert(String road, long id) {
        //System.out.println("insertion called for: " + road);
        fullName = road;
        insert(road, id, root);
    }

    private void insert(String road, long id, RadixNode currentNode) {
        if (currentNode != root && (currentNode == null || currentNode.getContent().equals("") || road.equals(""))) {
            //adding road to the check took the number of nodes from 393 to 148
            System.out.println("Something is fishy?");
            return;
        }

        if (currentNode.getChildren().size() == 0) {
            RadixNode newNode = new RadixNode(road, fullName, id);
            newNode.setIsPlace(true);
            currentNode.getChildren().add(newNode);
            System.out.println(road + " inserted");
            size++;
            places++;
            return;
        }

        ArrayList<RadixNode> children = currentNode.getChildren();
        for (int i = 0; i < children.size(); i++) {
            //System.out.println("currently looking at node: " + children.get(i).getContent());
            if (road.startsWith(children.get(i).getContent())) { //the child is a prefix to road, like child: test and road: tester
                System.out.println("recursion called for: " + road);
                System.out.println("with new string: " + road.substring(children.get(i).getContent().length()));
                insert(road.substring(children.get(i).getContent().length()), id, currentNode.getChildren().get(i));
                //System.out.println("recursion came back for: " + road);
                return;
            } else if (children.get(i).getContent().startsWith(road)) { //road is a prefix to child, like child: tester and road: test
                RadixNode temp = children.get(i);
                RadixNode node = new RadixNode(road, fullName, id);
                node.setIsPlace(true);
                children.set(i, node);
                temp.setContent(temp.getContent().substring(road.length()));
                children.get(i).addChild(temp);
                size++;
                places++;
                System.out.println("current node is longer than: " + road);
                System.out.println(temp.getContent() + " was moved down");
                return;
            } else if (children.get(i).getContent().charAt(0) == road.charAt(0)) { //they are partly equal like test and team
                String nodeContent = children.get(i).getContent();
                for (int j = 0; j < (Math.min(road.length(), nodeContent.length())); j++) {
                    if (road.charAt(j) != nodeContent.charAt(j) && j > 0) {
                        RadixNode temp = children.get(i);
                        temp.setContent(nodeContent.substring(j));
                        RadixNode temp2 = new RadixNode(road.substring(j), fullName, id);
                        temp2.setIsPlace(true);
                        RadixNode node = new RadixNode(road.substring(0, j));
                        children.set(i, node);
                        children.get(i).addChild(temp);
                        children.get(i).addChild(temp2);
                        System.out.println("more than zero chars was in common between: " + nodeContent + " " + road);
                        System.out.println("new parent: " + node.getContent());
                        System.out.println("children: " + temp.getContent() + " " + temp2.getContent());
                        size += 2;
                        places++;
                        return;
                    }
                }
            } else if (i == children.size() - 1) { //none of the children contain road
                RadixNode newNode = new RadixNode(road, fullName, id);
                currentNode.getChildren().add(newNode);
                newNode.setIsPlace(true);
                size++;
                places++;
                //System.out.println("there were children but " + road + " was added");
                return;
            }
        }
    }

}
