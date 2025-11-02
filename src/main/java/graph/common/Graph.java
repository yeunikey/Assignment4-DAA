package graph.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {

    private final int numVertices;
    private int numEdges;
    private final List<Set<Edge>> adj;

    public Graph(int numVertices) {
        this.numVertices = numVertices;
        this.numEdges = 0;
        this.adj = new ArrayList<>(numVertices);
        for (int i = 0; i < numVertices; i++) {
            adj.add(new HashSet<>());
        }
    }

    public int getNumVertices() {
        return numVertices;
    }


    public int getNumEdges() {
        return numEdges;
    }

    public void addEdge(int from, int to, int weight) {
        if (from < 0 || from >= numVertices || to < 0 || to >= numVertices) {
            throw new IllegalArgumentException("Invalid vertex index");
        }

        if (adj.get(from).add(new Edge(to, weight))) {
            this.numEdges++;
        }
    }

    public Set<Edge> getNeighbors(int v) {
        if (v < 0 || v >= numVertices) {
            throw new IllegalArgumentException("Invalid vertex index");
        }
        return adj.get(v);
    }

    public Graph getTranspose() {
        Graph transpose = new Graph(numVertices);
        for (int u = 0; u < numVertices; u++) {
            for (Edge edge : adj.get(u)) {
                transpose.addEdge(edge.to, u, edge.weight);
            }
        }
        return transpose;
    }

}
