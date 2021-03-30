package bfst21.osm;

import java.util.ArrayList;

public class RadixTree {
    //some kind of thing to store them all in, a graph right?
    //or whatever kd-tree does.
    private RadixNode root;
    private int size;

    public RadixTree() {
        root = new RadixNode("");
        size = 1;
    }

    public int getSize() {
        return size;
    }

    public String lookup(String searchTerm) {
        RadixNode currentNode = root;
        int charLength = 0;
        String result = "";

        while(!result.equals(searchTerm)) { //!currentNode.isPlace() &&
            boolean foundChild = false;
            ArrayList<RadixNode> children = currentNode.getChildren();

            for(int i = 0; i < children.size(); i++) {
                if(searchTerm.substring(charLength).startsWith(children.get(i).getContent())) {
                    currentNode = children.get(i);
                    foundChild = true;
                    charLength += currentNode.getContent().length();
                }
            }

            result += currentNode.getContent();

            if(!foundChild) {
                return null;
            }
        }
        return result;
    }

    public void insert(String road) {
        System.out.println("insertion called for: " + road);
        insert(road, root);
    }

    private void insert(String road, RadixNode currentNode) {
        //this would be some kind of break?
        if (currentNode != root && (currentNode == null || currentNode.getContent().equals(""))) {
            System.out.println("Something is fishy?");
        }

        if (currentNode.getChildren().size() == 0) {
            //put it in here.
            RadixNode newNode = new RadixNode(road);
            newNode.setIsPlace(true);
            currentNode.getChildren().add(newNode);
            System.out.println(road + " inserted");
            size++;
            return;
        }

        ArrayList<RadixNode> children = currentNode.getChildren();
        for (int i = 0; i < children.size(); i++) {
            System.out.println("currently looking at node: " + children.get(i).getContent());
            if (road.startsWith(children.get(i).getContent())) { //the child is a prefix to road, like child: test and road: tester
                System.out.println("recursion called for: " + road);
                System.out.println("with new string: " + road.substring(children.get(i).getContent().length()));
                insert(road.substring(children.get(i).getContent().length()), currentNode.getChildren().get(i));
                System.out.println("recursion came back for: " + road);
                return;
            } else if (children.get(i).getContent().startsWith(road)) { //road is a prefix to child, like child: tester and road: test
                System.out.println("current node is longer than: " + road);
                RadixNode temp = children.get(i);
                RadixNode node = new RadixNode(road);
                children.set(i, node);
                temp.setContent(temp.getContent().substring(road.length()));
                children.get(i).addChild(temp);
                size++;
                System.out.println(temp.getContent() + " was moved down");
                return;
            } else if (children.get(i).getContent().charAt(0) == road.charAt(0)) { //they are partly equal like test and team
                String nodeContent = children.get(i).getContent();
                for (int j = 0; j < (Math.min(road.length(), nodeContent.length())); j++) {
                    if (road.charAt(j) != nodeContent.charAt(i) && j > 0) {
                        System.out.println("more than zero chars was in common between: " + nodeContent + " " + road);
                        RadixNode temp = children.get(i);
                        temp.setContent(nodeContent.substring(j));
                        RadixNode node = new RadixNode(road.substring(0, j));
                        System.out.println("new parent: " + node.getContent());
                        children.set(i, node);
                        children.get(i).addChild(new RadixNode(road.substring(j)));
                        children.get(i).addChild(temp);
                        RadixNode temp2 = new RadixNode(road.substring(j));
                        System.out.println("children: " + temp.getContent() + " " + temp2.getContent());
                        return;
                    }
                }
            } else if (i == children.size() - 1) { //none of the children contain road
                System.out.println("there were children but " + road + " was added");
                RadixNode newNode = new RadixNode(road);
                currentNode.getChildren().add(newNode);
                size++;
                return;
            }
        }
    }
}
