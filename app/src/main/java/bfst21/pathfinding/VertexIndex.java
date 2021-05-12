package bfst21.pathfinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VertexIndex<T extends Vertex> implements Iterable<T>{
        private List<T> vertices;
        private boolean isSorted;

        public VertexIndex() {
            this.vertices = new ArrayList<>();
            this.isSorted = true;
        }

        public void addVertex(T vertex) {
            vertices.add(vertex);
            isSorted = false;
        }

        public Vertex getVertex(long id) {
            if (!isSorted) {
                vertices.sort((a, b) -> Long.compare(a.getId(), b.getId()));
                isSorted = true;
            }

            long lo = 0;
            long hi = vertices.size();
            while (lo + 1 < hi) {
                long mid = (lo + hi) / 2;
                if (vertices.get((int) mid).getId() <= id) {
                    lo = mid;
                } else {
                    hi = mid;
                }
            }
            Vertex vertex = vertices.get((int) lo);

            if (vertex.getId() == id) {
                return vertex;
            } else {
                return null;
            }
        }

        public VertexIndex<T> mergeVertices() {
            vertices.sort((a, b) -> Long.compare(a.getId(), b.getId()));
            isSorted = true;

            //List<T> newVertices = new ArrayList<>();
            int count = 0;
            for(int i = 1; i < vertices.size(); i++) {
                Vertex previous = vertices.get(count);
                Vertex current = vertices.get(i);
                long valueOnPreviousVertex = previous.getId();
                long valueOnCurrentVertex = current.getId();
                if(valueOnCurrentVertex == valueOnPreviousVertex){
                    if(current.getAdjacencies() != null) {
                        for (Edge adjecentEdge : current.getAdjacencies()) {
                            previous.addAdjacencies(adjecentEdge);
                        }
                    }
                    current = previous;
                } else {
                    //newVertices.add((T) previous);
                    count = i;
                }
            }

            /*int counter = 0;
            while (counter < vertices.size()-1) {
                long valueOnCurrentVertex = vertices.get(counter).getId();
                long valueOnNextVertex = vertices.get(counter+1).getId();
                if(valueOnCurrentVertex == valueOnNextVertex){
                    for(Edge adjecentEdge: vertices.get(counter+1).getAdjacencies()){
                        vertices.get(counter).addAdjacencies(adjecentEdge);
                    }
                }
                else{
                    counter++;
                }
            }

            /*Iterator<T> itr = vertices.iterator();
            Vertex previous = itr.next();
            while (itr.hasNext()) {
                Vertex current = itr.next();
                long valueOnCurrentVertex = previous.getId();
                long valueOnNextVertex = current.getId();
                if(valueOnCurrentVertex == valueOnNextVertex){
                    if(current.getAdjacencies() != null) {
                        for (Edge adjecentEdge : current.getAdjacencies()) {
                            previous.addAdjacencies(adjecentEdge);
                        }
                    } else {
                        System.out.println("adjacencies was null");
                    }
                } else {
                    previous = current;
                }
            }*/
            return this;
        }

        public int size() {
            return vertices.size();
        }

        public List<T> getVertices(){
            return vertices;
        }

        @Override
        public Iterator<T> iterator() {
            return vertices.iterator();
        }
    }
