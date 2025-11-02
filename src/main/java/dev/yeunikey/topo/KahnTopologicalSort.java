package dev.yeunikey.topo;

import dev.yeunikey.common.AlgorithmMetrics;
import dev.yeunikey.common.Edge;
import dev.yeunikey.common.Graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class KahnTopologicalSort {

    private Graph dag;
    private int n;
    private int[] inDegree;
    private AlgorithmMetrics metrics;

    public KahnTopologicalSort(Graph dag) {
        if (dag == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }
        this.dag = dag;
        this.n = dag.getNumVertices();
        this.inDegree = new int[n];
        this.metrics = new AlgorithmMetrics();
    }

    public TopoSortResult sort() {
        metrics.start();
        calculateInDegrees();

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
                metrics.incrementCounter("Pushes");
            }
        }

        List<Integer> topologicalOrder = new ArrayList<>();
        int visitedCount = 0;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.incrementCounter("Pops");
            topologicalOrder.add(u);
            visitedCount++;

            for (Edge edge : dag.getNeighbors(u)) {
                int v = edge.to;
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.add(v);
                    metrics.incrementCounter("Pushes");
                }
            }
        }

        metrics.stop();

        if (visitedCount != n) {
            throw new IllegalStateException("Graph has a cycle! Cannot topologically sort.");
        }

        return new TopoSortResult(topologicalOrder, metrics);
    }

    private void calculateInDegrees() {
        for (int u = 0; u < n; u++) {
            for (Edge edge : dag.getNeighbors(u)) {
                inDegree[edge.to]++;
            }
        }
    }

    public static class TopoSortResult {
        public final List<Integer> topologicalOrder;
        public final AlgorithmMetrics metrics;

        TopoSortResult(List<Integer> topologicalOrder, AlgorithmMetrics metrics) {
            this.topologicalOrder = topologicalOrder;
            this.metrics = metrics;
        }
    }

}
