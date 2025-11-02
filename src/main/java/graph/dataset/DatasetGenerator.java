package graph.dataset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class DatasetGenerator {

    private static final String DATA_DIR = "data";
    private static final Random rand = new Random();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static class JsonEdge {
        int u;
        int v;
        int w;
        JsonEdge(int u, int v, int w) { this.u = u; this.v = v; this.w = w; }
    }

    private static class JsonGraph {
        boolean directed = true;
        int n;
        List<JsonEdge> edges;
        int source;
        String weight_model = "edge";
        String description;

        JsonGraph(int n, String description) {
            this.n = n;
            this.edges = new ArrayList<>();
            this.source = rand.nextInt(n);
            this.description = description;
        }
    }

    public static void main(String[] args) throws IOException {
        Path dataPath = Paths.get(DATA_DIR);
        if (!Files.exists(dataPath)) {
            Files.createDirectories(dataPath);
        }

        generateDataset("small-1-dag.json", 6, 8, false, "Small DAG");
        generateDataset("small-2-cycle.json", 8, 10, true, "Small with 1 cycle");
        generateDataset("small-3-multiscc.json", 10, 15, true, "Small with multiple SCCs");

        generateDataset("medium-1-dag.json", 15, 25, false, "Medium DAG, sparse");
        generateDataset("medium-2-multiscc.json", 18, 40, true, "Medium with multiple SCCs");
        generateDataset("medium-3-densecycle.json", 20, 80, true, "Medium dense with cycles");

        generateDataset("large-1-dag.json", 30, 60, false, "Large DAG, sparse");
        generateDataset("large-2-multiscc.json", 40, 100, true, "Large with many SCCs");
        generateDataset("large-3-densedag.json", 50, 300, false, "Large dense DAG");

        System.out.println("Generated 9 datasets in '" + DATA_DIR + "' directory.");
    }

    private static void generateDataset(String filename, int n, int m, boolean allowCycles, String description) throws IOException {
        JsonGraph g = new JsonGraph(n, description);
        Set<String> edgeSet = new HashSet<>();


        List<Integer> nodeOrder = new ArrayList<>();
        for (int i = 0; i < n; i++) nodeOrder.add(i);
        Collections.shuffle(nodeOrder);
        int[] pos = new int[n];
        for (int i = 0; i < n; i++) pos[nodeOrder.get(i)] = i;

        int edgesAdded = 0;
        while (edgesAdded < m) {
            int u = rand.nextInt(n);
            int v = rand.nextInt(n);
            if (u == v) continue;

            if (!allowCycles) {

                if (pos[u] >= pos[v]) {
                    continue;
                }
            }

            String edgeKey = u + "->" + v;
            if (edgeSet.contains(edgeKey)) continue;

            int w = rand.nextInt(10) + 1;
            g.edges.add(new JsonEdge(u, v, w));
            edgeSet.add(edgeKey);
            edgesAdded++;
        }


        if (allowCycles && n > 1) {
            int u = rand.nextInt(n);
            int v = rand.nextInt(n);
            if (pos[u] > pos[v]) {
                int w = rand.nextInt(10) + 1;
                g.edges.add(new JsonEdge(u, v, w));
            }
        }


        try (FileWriter writer = new FileWriter(Paths.get(DATA_DIR, filename).toString())) {
            gson.toJson(g, writer);
        }
    }

}