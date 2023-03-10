package bfst21.pathfinding;

import bfst21.Model;
import bfst21.osm.Drawable;
import bfst21.osm.Node;
import bfst21.osm.Tag;
import bfst21.osm.Way;

import java.util.*;

public class AStar {
    private Model model;
    private boolean isRoundabout;
    private double totalDistance = 0;
    private double totalTime = 0;
    private int exits = 0;
    private List<Vertex> path;
    private TransportType type;

    private final ArrayList<Tag> driveable = new ArrayList<>(Arrays.asList(Tag.MOTORWAY_LINK, Tag.LIVING_STREET, Tag.MOTORWAY, Tag.PEDESTRIAN, Tag.PRIMARY, Tag.RESIDENTIAL, Tag.ROAD, Tag.SECONDARY, Tag.SERVICE, Tag.TERTIARY, Tag.TRACK, Tag.TRUNK, Tag.UNCLASSIFIED));
    private final ArrayList<Tag> cyclable = new ArrayList<>(Arrays.asList(Tag.CYCLEWAY, Tag.LIVING_STREET, Tag.PATH, Tag.PEDESTRIAN, Tag.RESIDENTIAL, Tag.ROAD, Tag.SECONDARY, Tag.SERVICE, Tag.TERTIARY, Tag.TRACK, Tag.UNCLASSIFIED));
    private final ArrayList<Tag> walkable = new ArrayList<>(Arrays.asList(Tag.FOOTWAY, Tag.LIVING_STREET, Tag.PATH, Tag.PEDESTRIAN, Tag.RESIDENTIAL, Tag.ROAD, Tag.SERVICE, Tag.TERTIARY, Tag.TRACK, Tag.UNCLASSIFIED));

    public AStar(Model model) {
        this.model = model;
        if (model.getVertexIndex() == null) {
            createGraph();
        }
    }

    /**
     * CreateGraph iterates through the drawableMap adding weighted directed Edges to vertices.
     * The Edges are given one or more pathtypes (driveable, walkable and cyclable) depending on the properties
     * of the Way the Vertex belongs to.
     * <p>
     * The vertices are stored in an ArrayList sorted by the id of the vertices, so it can be
     * used for binary searches when initialising a new AStarSearch.
     */

    private void createGraph() {
        for (Map.Entry<Tag, List<Drawable>> entry : model.getDrawableMap().entrySet()) {
            List<Drawable> value = entry.getValue();
            for (Drawable way : value) {
                Way tempWay = (Way) way;
                for (int i = 0; i < tempWay.getNodes().size(); i++) {
                    Vertex vertex = model.getVertexMap().get(tempWay.getNodes().get(i));
                    if (i != (tempWay.getNodes().size() - 1)) {
                        Vertex nextVertex = model.getVertexMap().get(tempWay.getNodes().get(i + 1));
                        Edge edge = new Edge(nextVertex, distanceToVertex(vertex, nextVertex), tempWay.getId());
                        edge.setPathTypes(tempWay, this);
                        vertex.addAdjacencies(edge);
                    }
                    if (i > 0 && !tempWay.isOneway()) {
                        Vertex previousVertex = model.getVertexMap().get(tempWay.getNodes().get(i - 1));
                        Edge edge = new Edge(previousVertex, distanceToVertex(vertex, previousVertex), tempWay.getId());
                        edge.setPathTypes(tempWay, this);
                        vertex.addAdjacencies(edge);
                    }
                }
            }
        }
        ArrayList<Vertex> tempListOfVertices = new ArrayList<>(model.getVertexMap().values());
        model.nullifyVertexMap();
        tempListOfVertices.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        model.setVertexIndex(tempListOfVertices);
    }

    /**
     * AStarSearch creates a priority queue with the start vertex being inserted. We take out the first element of
     * the queue and check if it is our end node, if not we add all the children that our current vertex's edges are
     * pointing to. We take into account that the edge matches the transport type of the path. Like this we continue
     * until we find the end or there are no more vertices to be explored, in which case no path was found.
     * <p>
     * Here we set the scores of each child vertex.
     * The h-score is the heuristic, a measure of how far the node is from the goal (in our case distance in a straight
     * line from the current vertex to the end, divided by max speed for the type of edge).
     * The g-score is the weight of the path taken so far.
     * The f-score is the result of the g- and h-score and therefore the lowest f-score will be the best path, as it
     * will be short, fast and closer to the goal.
     * <p>
     * After we have created the path we reset all values for the vertices that were explored, so that these values
     * don't faultily affect the next traversal through the graph.
     *
     * @param startNode The node on which the path is taken from
     * @param endNode   The node on which the path should end, the goal.
     * @param type      The transport type, affecting which edges can be taken and the scores.
     */
    public void AStarSearch(Node startNode, Node endNode, TransportType type) {
        Vertex start = getVertex(startNode.getId());
        Vertex end = getVertex(endNode.getId());

        this.type = type;
        model.setAStarPath(null);
        ArrayList<Vertex> listOfCheckedVertices = new ArrayList<>();

        PriorityQueue<Vertex> pq = new PriorityQueue<>(20, new VertexComparator());

        //cost from start
        start.setG_scores(0);

        pq.add(start);

        boolean found = false;

        while ((!pq.isEmpty()) && (!found)) { //While content in PQ and goal not found
            //the vertex in having the lowest f_score value
            Vertex current = pq.poll(); //Gets the first element in the PQ

            current.setExplored(true); //Adds current to the explored set to remember that it is explored
            listOfCheckedVertices.add(current);

            //Checks if current is goal
            if (current.getId() == end.getId()) {
                found = true;
            }

            if (current.getAdjacencies() == null) {
                continue;
            }

            //Checks every child of current vertex
            for (Edge e : current.getAdjacencies()) {
                if (type == TransportType.CAR && e.isDriveable()
                        || type == TransportType.BICYCLE && e.isCyclable()
                        || type == TransportType.WALK && e.isWalkable()) {
                    Vertex child = e.getTarget();
                    //if we have already looked at the child vertex we skip
                    if (child.isExplored()) {
                        continue;
                    }
                    child.setH_scores(distanceToVertex(child, end) / type.maxSpeed);
                    float cost = e.getWeight(type, model.getWayIndex().getMember(e.getWayID()).getSpeed());
                    float temp_g_scores = current.getG_scores() + cost;
                    float temp_f_scores = temp_g_scores + child.getH_scores();

                    //add child vertex to queue and update f_score
                    child.setG_scores(temp_g_scores);
                    child.setF_scores(temp_f_scores);
                    child.setParent(current);
                    pq.add(child);
                }
            }
        }

        createPath(end);

        List<Vertex> debugPath = new ArrayList<>();
        for (Vertex vertex : listOfCheckedVertices) {
            debugPath.add(vertex);
            vertex.setH_scores(0);
            vertex.setF_scores(0);
            vertex.setG_scores(0);
            vertex.setParent(null);
            vertex.setExplored(false);
        }
        model.setAStarDebugPath(debugPath);
    }

    /**
     * CreatePath iterates through the vertices by starting at the destination Vertex and adding
     * the parent vertex to an ArrayList until we find a null reference. Since our starting node has no parent node,
     * finding a null reference means that we found our starting point. The ArrayList is reversed to store
     * the path from start to end instead of end to start.
     * <p>
     * The ArrayList with the path is added to Model.
     *
     * @param destination the destination Vertex of the AStarSearch.
     */

    public void createPath(Vertex destination) {
        path = new ArrayList<>();
        for (Vertex vertex = destination; vertex != null; vertex = vertex.getParent()) { //Starts on the target and work back to start
            path.add(vertex);
        }
        Collections.reverse(path);
        model.setAStarPath(path);
    }

    /**
     * This method creates the description of the AStar path. This is done by looping through every vertex in the path
     * and detecting when a change on the path happens, then creating a new {@link Step} with the roadname, direction
     * and distance driven. A Step can also have exits added if we are going out of a roundabout.
     * <p>
     * We have to handle the last piece of road differently as our method looks back and creates a step when we detect
     * the next change. Therefore we create two steps when we get to the end in
     * {@link #createArrivalStep(ArrayList, double, int, Direction, long)}
     * <p>
     * We also handle the paths that would not be part of the for loop, that is a very short path or no path.
     * <p>
     * The method runs through the whole path and therefore sums up the distance driven and the time this took.
     * In the end we return the list with all the steps created when going through the path.
     *
     * @return ArrayList of Step objects which include the direction of the change in path, the road name of the road
     * which the change goes onto and the distance of which you have to follow said road.
     */
    public ArrayList<Step> getPathDescription() {
        ArrayList<Step> routeDescription = new ArrayList<>();
        double currentDistance = 0;
        int currentMaxSpeed;
        totalTime = 0;
        totalDistance = 0;
        Direction direction = Direction.FOLLOW;

        for (int i = 1; i < path.size() - 1; i++) {
            Vertex vertex = path.get(i);
            Vertex nextVertex = path.get(i + 1);
            Vertex previousVertex = path.get(i - 1);
            currentDistance += distanceToVertex(previousVertex, vertex);

            long firstId = 0;
            long secondId = 0;

            firstId = getWayId(previousVertex, vertex, firstId);
            secondId = getWayId(vertex, nextVertex, secondId);

            String lastRoadName;
            if (model.getWayIndex().getMember(firstId).getName().equals("")) {
                lastRoadName = "unknown road";
            } else {
                lastRoadName = model.getWayIndex().getMember(firstId).getName();
            }

            //count exits in roundabout
            if (isRoundabout && vertex.getAdjacencies().size() > 1) {
                exits++;
            }

            //the next way is different than the current
            if (firstId != secondId && !lastRoadName.equals(model.getWayIndex().getMember(secondId).getName())) {
                currentMaxSpeed = model.getWayIndex().getMember(firstId).getSpeed();
                if (type.equals(TransportType.WALK)) {
                    currentMaxSpeed = TransportType.WALK.maxSpeed;
                } else if (type.equals(TransportType.BICYCLE)) {
                    currentMaxSpeed = TransportType.BICYCLE.maxSpeed;
                }

                if (!isRoundabout) { //if we're in a roundabout we don't want to give a description
                    Step step = new Step(direction, lastRoadName, currentDistance);
                    if (exits > 0) {
                        step.setExits(exits);
                    }
                    routeDescription.add(step);
                }

                totalDistance += currentDistance;
                totalTime += currentDistance / currentMaxSpeed;
                currentDistance = 0;
                direction = getDirection(vertex, previousVertex, nextVertex);
            }

            //we handle the last piece of road
            if (i == path.size() - 2) {
                currentMaxSpeed = model.getWayIndex().getMember(firstId).getSpeed();
                currentDistance += distanceToVertex(vertex, nextVertex);
                createArrivalStep(routeDescription, currentDistance, currentMaxSpeed, direction, secondId);
            }
        }

        // we handle very short paths
        if (2 == path.size()) {
            long firstId = 0;
            for (Edge e : path.get(0).getAdjacencies()) {
                if (e.getTarget() == path.get(1)) {
                    firstId = e.getWayID();
                }
            }

            currentMaxSpeed = model.getWayIndex().getMember(firstId).getSpeed();
            currentDistance += distanceToVertex(path.get(0), path.get(1));
            createArrivalStep(routeDescription, currentDistance, currentMaxSpeed, direction, firstId);
        }

        if (routeDescription.size() == 0) {
            routeDescription.add(new Step(Direction.NO_PATH, "", 0));
        }

        return routeDescription;
    }

    private long getWayId(Vertex vertex, Vertex nextVertex, long wayId) {
        for (Edge e : vertex.getAdjacencies()) {
            if (e.getTarget() == nextVertex && (type == TransportType.CAR && e.isDriveable() || type == TransportType.BICYCLE && e.isCyclable() || type == TransportType.WALK && e.isWalkable())) {
                wayId = e.getWayID();
            }
        }
        return wayId;
    }

    private void createArrivalStep(ArrayList<Step> routeDescription, double currentDistance, int currentMaxSpeed, Direction direction, long firstId) {
        totalDistance += currentDistance;
        totalTime += currentDistance / currentMaxSpeed;

        String lastRoadName;
        if (model.getWayIndex().getMember(firstId).getName().equals("")) {
            lastRoadName = "unknown road";
        } else {
            lastRoadName = model.getWayIndex().getMember(firstId).getName();
        }

        Step step = new Step(direction, lastRoadName, currentDistance);
        if (exits > 0) {
            step.setExits(exits);
        }
        routeDescription.add(step);
        routeDescription.add(new Step(Direction.ARRIVAL, lastRoadName, 0));
    }

    /**
     * GetDirection determines which direction we are going when going in to an intersection or a roundabout.
     * The method uses the three input vertices to determine the angle from the previousVertex to the nextVertex,
     * with the currentVertex as orego.
     * <p>
     * If the angle is above 190 degrees we need to go left
     * If the angle is under 165 degrees we check if we intersect with a roundabout. If we intersect with a
     * roundabout we count exits otherwise we just need to turn right.
     * If the angle is between 165 and 190 we need to continue straight ahead.
     *
     * @param currentVertex  the intersection Vertex
     * @param previousVertex the first Vertex before the intersection
     * @param nextVertex     the first Vertex after the intersection
     * @return enum Direction
     */

    private Direction getDirection(Vertex currentVertex, Vertex previousVertex, Vertex nextVertex) {
        Direction direction = null;
        double theta = Math.atan2(nextVertex.getY() - currentVertex.getY(), nextVertex.getX() - currentVertex.getX()) -
                Math.atan2(previousVertex.getY() - currentVertex.getY(), previousVertex.getX() - currentVertex.getX());

        double result = Math.toDegrees(theta); //the same as multiplying theta by 180/pi
        //we handle if the result has the wrong sign, in this case plus instead of minus
        if (result > 0) result = 360 - result;
        result = Math.abs(result);

        if (result > 190) {
            direction = Direction.LEFT;
        } else if (result < 165) {
            for (Edge e : currentVertex.getAdjacencies()) {
                if (e.getTarget() == nextVertex) {
                    if (model.getWayIndex().getMember(e.getWayID()).isJunction()) {
                        isRoundabout = true;
                        exits = 0;
                        break;
                    } else if (isRoundabout) {
                        isRoundabout = false;
                        if (exits == 1) {
                            direction = Direction.ROUNDABOUT_FIRST_EXIT;
                        } else if (exits == 2) {
                            direction = Direction.ROUNDABOUT_SECOND_EXIT;
                        } else {
                            direction = Direction.ROUNDABOUT_OTHER_EXIT;
                        }
                        break;
                    } else {
                        direction = Direction.RIGHT;
                        break;
                    }
                }
            }
        } else {
            direction = Direction.CONTINUE;
        }
        return direction;
    }

    private static class VertexComparator implements Comparator<Vertex> {
        public int compare(Vertex i, Vertex j) {
            return Float.compare(i.getF_scores(), j.getF_scores());
        }
    }

    private float distanceToVertex(Vertex current, Vertex destination) {
        float distance = 0;
        if (current != destination) {
            float deltaX = Math.abs(destination.getX()) - Math.abs(current.getX());
            float deltaY = Math.abs(destination.getY()) - Math.abs(current.getY());
            distance = (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        }
        return distance * 111.320f * Model.scalingConstant;
    }

    public String getTotalDistance() {
        String result = "Distance: ";
        double distance = Math.round(totalDistance * 10.0) / 10.0;
        if (distance < 1) {
            result += distance * 1000 + " m";
        } else {
            result += distance + " km";
        }
        return result;
    }

    public String getTotalTime() {
        String result = "Estimated Time: ";
        double time = totalTime * 60;

        if (time < 1) {
            result += "1 min";
        } else {
            int timeInMinutes = (int) time;
            int minutes = timeInMinutes % 60;
            int hours = timeInMinutes / 60;

            if (hours >= 1) {
                result += hours + " hours";
            }
            if (hours >= 1 && minutes != 0) {
                result += " and ";
            }
            if (minutes != 0) {
                result += minutes + " min";
            }
        }
        return result;
    }

    public Vertex getVertex(long id) {
        long lo = 0;
        long hi = model.getVertexIndex().size();
        while (lo + 1 < hi) {
            long mid = (lo + hi) / 2;
            if (model.getVertexIndex().get((int) mid).getId() <= id) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        Vertex vertex = model.getVertexIndex().get((int) lo);

        if (vertex.getId() == id) {
            return vertex;
        } else {
            return null;
        }
    }

    public ArrayList<Tag> getDriveableTags() {
        return driveable;
    }

    public ArrayList<Tag> getCyclableTags() {
        return cyclable;
    }

    public ArrayList<Tag> getWalkableTags() {
        return walkable;
    }
}
