package graph.dagsp;

import graph.common.AlgorithmMetrics;
import graph.common.Edge;
import graph.common.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class DagShortestPath {

    private Graph dag;
    private int n;
    private List<Integer> topologicalOrder;
    private AlgorithmMetrics metrics;

    public static final int INFINITY = Integer.MAX_VALUE;

    public DagShortestPath(Graph dag, List<Integer> topologicalOrder) {
        this.dag = dag;
        this.n = dag.getNumVertices();
        this.topologicalOrder = topologicalOrder;
        this.metrics = new AlgorithmMetrics();
    }

    public PathResult getShortestPaths(int source) {
        return computePaths(source, false);
    }

    public PathResult getLongestPaths(int source) {
        return computePaths(source, true);
    }

    private PathResult computePaths(int source, boolean findLongest) {
        metrics.start();
        int[] dist = new int[n];
        int[] parent = new int[n];

        if (findLongest) {
            Arrays.fill(dist, -INFINITY);
        } else {
            Arrays.fill(dist, INFINITY);
        }
        Arrays.fill(parent, -1);

        dist[source] = 0;

        for (int u : topologicalOrder) {

            if (dist[u] == (findLongest ? -INFINITY : INFINITY)) {
                continue;
            }

            for (Edge edge : dag.getNeighbors(u)) {
                int v = edge.to;
                int w = edge.weight;

                metrics.incrementCounter("Relaxations");
                if (findLongest) {

                    if (dist[v] < dist[u] + w) {
                        dist[v] = dist[u] + w;
                        parent[v] = u;
                    }
                } else {

                    if (dist[v] > dist[u] + w) {
                        dist[v] = dist[u] + w;
                        parent[v] = u;
                    }
                }
            }
        }

        metrics.stop();
        return new PathResult(dist, parent, metrics, findLongest);
    }

    public static class PathResult {
        public final int[] distances;
        public final int[] parents;
        public final AlgorithmMetrics metrics;
        public final boolean isLongestPath;

        PathResult(int[] distances, int[] parents, AlgorithmMetrics metrics, boolean isLongest) {
            this.distances = distances;
            this.parents = parents;
            this.metrics = metrics;
            this.isLongestPath = isLongest;
        }

        public List<Integer> getPath(int target) {
            if (target < 0 || target >= parents.length) {
                throw new IllegalArgumentException("Invalid target node");
            }

            Stack<Integer> pathStack = new Stack<>();
            int curr = target;


            if (distances[curr] == (isLongestPath ? -INFINITY : INFINITY)) {
                return new ArrayList<>();
            }

            while (curr != -1) {
                pathStack.push(curr);
                curr = parents[curr];
            }

            List<Integer> path = new ArrayList<>();
            while (!pathStack.isEmpty()) {
                path.add(pathStack.pop());
            }
            return path;
        }


        public int getCriticalPathLength() {
            if (!isLongestPath) {
                return 0;
            }
            int maxDist = -INFINITY;
            for (int d : distances) {
                if (d > maxDist) {
                    maxDist = d;
                }
            }
            return maxDist == -INFINITY ? 0 : maxDist;
        }
    }

}

