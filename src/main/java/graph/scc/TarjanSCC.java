package graph.scc;

import graph.common.AlgorithmMetrics;
import graph.common.Edge;
import graph.common.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class TarjanSCC {

    private int n;
    private Graph graph;
    private int[] disc;
    private int[] low;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private int time;
    private int sccCount;
    private int[] sccMap;
    private List<List<Integer>> sccs;
    private AlgorithmMetrics metrics;

    public TarjanSCC(Graph graph) {
        this.graph = graph;
        this.n = graph.getNumVertices();
        this.metrics = new AlgorithmMetrics();
    }

    public SccResult findSccs() {
        metrics.start();

        disc = new int[n];
        low = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        sccMap = new int[n];
        sccs = new ArrayList<>();
        time = 0;
        sccCount = 0;

        Arrays.fill(disc, -1);
        Arrays.fill(low, -1);
        Arrays.fill(sccMap, -1);

        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i);
            }
        }

        Graph condensedGraph = buildCondensationGraph();

        metrics.stop();
        return new SccResult(sccs, sccMap, sccCount, condensedGraph, metrics);
    }

    private void dfs(int u) {
        metrics.incrementCounter("DFS Visits (Nodes)");
        disc[u] = low[u] = time++;
        stack.push(u);
        onStack[u] = true;

        for (Edge edge : graph.getNeighbors(u)) {
            metrics.incrementCounter("DFS Edges Processed");
            int v = edge.to;
            if (disc[v] == -1) {

                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {

                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) {
            List<Integer> component = new ArrayList<>();
            while (true) {
                int v = stack.pop();
                onStack[v] = false;
                sccMap[v] = sccCount;
                component.add(v);
                if (u == v) {
                    break;
                }
            }
            sccs.add(component);
            sccCount++;
        }
    }

    private Graph buildCondensationGraph() {
        Graph condensedGraph = new Graph(sccCount);
        for (int u = 0; u < n; u++) {
            int sccU = sccMap[u];
            for (Edge edge : graph.getNeighbors(u)) {
                int v = edge.to;
                int sccV = sccMap[v];
                if (sccU != sccV) {
                    condensedGraph.addEdge(sccU, sccV, edge.weight);
                }
            }
        }
        return condensedGraph;
    }

    public static class SccResult {
        public final List<List<Integer>> sccs;
        public final int[] sccMap;
        public final int sccCount;
        public final Graph condensedGraph;
        public final AlgorithmMetrics metrics;

        SccResult(List<List<Integer>> sccs, int[] sccMap, int sccCount,
                  Graph condensedGraph, AlgorithmMetrics metrics) {
            this.sccs = sccs;
            this.sccMap = sccMap;
            this.sccCount = sccCount;
            this.condensedGraph = condensedGraph;
            this.metrics = metrics;
        }
    }

}
