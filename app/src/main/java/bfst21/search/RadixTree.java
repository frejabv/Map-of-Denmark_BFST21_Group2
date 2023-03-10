package bfst21.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class RadixTree implements Serializable {
    private final RadixNode root;
    private int size;
    private int places;
    private String fullName;

    public RadixTree() {
        root = new RadixNode("");
        size = 1;
        places = 0;
    }

    public int getSize() {
        return size;
    }

    public int getPlaces() {
        return places;
    }

    /**
     * From a searchterm we find the node with the best match using
     * {@link #lookupNode} From this node we look at the children and add them to a
     * list of suggestions if they are valid nodes (isPlace = true).
     *
     * @return A list of RadixNodes which are places and children of the searchTerm
     * node.
     */
    public ArrayList<RadixNode> getSuggestions(String searchTerm) {
        searchTerm = searchTerm.substring(0, 1).toUpperCase() + searchTerm.substring(1);
        ArrayList<RadixNode> suggestions = new ArrayList<>();
        LinkedList<RadixNode> queue = new LinkedList<>();
        int listItems = 0;
        boolean first = true;
        RadixNode currentNode;
        RadixNode initialNode = lookupNode(searchTerm);

        if (initialNode != null) {
            queue.add(initialNode);
        }

        while (listItems <= 8 && !queue.isEmpty()) {
            currentNode = queue.remove();

            if (first && currentNode.isPlace()) {
                suggestions.add(currentNode);
            }

            ArrayList<RadixNode> children = currentNode.getChildren();
            for (RadixNode child : children) {
                queue.add(child);

                if (child.isPlace()) {
                    suggestions.add(child);
                    listItems++;
                }
            }
            first = false;
        }
        return suggestions;

    }

    /**
     * With a given searchterm this method looks through the radixtree starting at
     * the root to find a full match for the searchterm, if that does not exist we
     * settle on any part matches we found along the way, if none exist we return
     * null.
     *
     * @return A RadixNode which perfectly or partly matches the searchTerm, else
     * null.
     */
    public RadixNode lookupNode(String searchTerm) {
        RadixNode currentNode = root;
        int charLength = 0;
        String result = "";
        RadixNode safeNode = null;

        while (!result.equals(searchTerm)) {
            boolean foundChild = false;
            ArrayList<RadixNode> children = currentNode.getChildren();

            for (RadixNode child : children) {
                if (searchTerm.length() > charLength) {
                    if (child.getValue().startsWith(searchTerm.substring(charLength))) {
                        safeNode = child;
                    }
                    if (searchTerm.substring(charLength).startsWith(child.getValue())) {
                        currentNode = child;
                        foundChild = true;
                        charLength += currentNode.getValue().length();
                        break;
                    }
                }
            }

            result += currentNode.getValue();

            if (!foundChild) {
                return safeNode;
            }
        }
        return currentNode;
    }

    /**
     * Starts the recursive call to insert. Saves the stringToInsert, so that we can
     * insert the fullName into every RadixNode we create in insert.
     */
    public void insert(String stringToInsert, long id) {
        fullName = stringToInsert;
        insert(stringToInsert, id, root, true);
    }

    public void insert(String roadName, String restOfAddress, long id) {
        fullName = roadName;
        insert(roadName, id, root, false);
        var roadNameNode = lookupNode(roadName);
        fullName = roadName + restOfAddress;
        if (roadNameNode != null) {
            insert(restOfAddress, id, roadNameNode, true);
        } else {
            insert(roadName + restOfAddress, id);
        }
    }

    /**
     * There are several cases for the insertion of a new node, they are represented
     * in if-statements.
     * Case 0: The currentNode we are looking at is null or
     * contains nothing, or what we are trying to insert is nothing.
     * Case 1: No children exist.
     * Case 2: The child node we are looking at is a prefix to stringToInsert.
     * Case 3: stringToInsert is a prefix to the child node we are looking at.
     * Case 4: The child node we are looking at and stringToInsert are partly equal.
     * Case 5: None of the children have something in common with stringToInsert.
     */
    private void insert(String stringToInsert, long id, RadixNode currentNode, boolean secondary) {
        if (currentNode != root
                && (currentNode == null || currentNode.getValue().equals("") || stringToInsert.equals(""))) {
            return;
        }

        if (currentNode.getChildren().size() == 0) {
            RadixNode node = new RadixNode(stringToInsert, fullName, id, secondary);
            currentNode.getChildren().add(node);
            size++;
            places++;
            return;
        }

        ArrayList<RadixNode> children = currentNode.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (stringToInsert.startsWith(children.get(i).getValue())) {
                // the child is a prefix to stringToInsert, like child: test and stringToInsert: tester
                insert(stringToInsert.substring(children.get(i).getValue().length()), id,
                        currentNode.getChildren().get(i), secondary);
                return;
            } else if (children.get(i).getValue().startsWith(stringToInsert)) {
                // stringToInsert is a prefix to child, like child: tester and stringToInsert: test
                RadixNode originalNode = children.get(i);
                RadixNode newParentNode = new RadixNode(stringToInsert, fullName, id, secondary);
                children.set(i, newParentNode);
                originalNode.setValue(originalNode.getValue().substring(stringToInsert.length()));
                children.get(i).addChild(originalNode);
                size++;
                places++;
                return;
            } else if (children.get(i).getValue().charAt(0) == stringToInsert.charAt(0)) {
                // they are partly equal like test and team
                String nodeContent = children.get(i).getValue();
                for (int j = 0; j < (Math.min(stringToInsert.length(), nodeContent.length())); j++) {
                    if (stringToInsert.charAt(j) != nodeContent.charAt(j) && j > 0) {
                        /*
                         * This child and the node we want to insert are partly equal, so we want to add
                         * a new node which contains the part they have in common and then set them both
                         * as the children of this new node.
                         */
                        RadixNode originalNode = children.get(i);
                        originalNode.setValue(nodeContent.substring(j));
                        RadixNode newChildNode = new RadixNode(stringToInsert.substring(j), fullName, id, secondary);
                        RadixNode newParentNode = new RadixNode(stringToInsert.substring(0, j));
                        children.set(i, newParentNode);
                        children.get(i).addChild(originalNode);
                        children.get(i).addChild(newChildNode);
                        size += 2;
                        places++;
                        return;
                    }
                }
            } else if (i == children.size() - 1) { // none of the children contain stringToInsert
                RadixNode node = new RadixNode(stringToInsert, fullName, id, secondary);
                currentNode.getChildren().add(node);
                size++;
                places++;
                return;
            }
        }
    }

}
