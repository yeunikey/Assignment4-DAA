package dev.yeunikey.common;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class GraphLoader {

    private static class JsonEdge {
        int u;
        int v;
        int w;
    }

    private static class JsonGraph {
        boolean directed;
        int n;
        List<JsonEdge> edges;
        int source;
        String weight_model;
    }

    public static LoadedGraph loadGraph(String filepath) throws IOException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(filepath));
        JsonGraph jsonGraph = gson.fromJson(reader, JsonGraph.class);
        reader.close();

        if (!jsonGraph.directed) {
            System.err.println("Warning: Graph is not directed. Proceeding as directed.");
        }

        Graph g = new Graph(jsonGraph.n);
        for (JsonEdge edge : jsonGraph.edges) {
            g.addEdge(edge.u, edge.v, edge.w);
        }

        return new LoadedGraph(g, jsonGraph.source);
    }

    public static class LoadedGraph {
        public final Graph graph;
        public final int sourceNode;

        public LoadedGraph(Graph graph, int sourceNode) {
            this.graph = graph;
            this.sourceNode = sourceNode;
        }
    }

}
