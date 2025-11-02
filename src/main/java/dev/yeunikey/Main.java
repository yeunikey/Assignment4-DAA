package dev.yeunikey;

import dev.yeunikey.common.Graph;
import dev.yeunikey.common.GraphLoader;
import dev.yeunikey.dagsp.DagShortestPath;
import dev.yeunikey.scc.TarjanSCC;
import dev.yeunikey.topo.KahnTopologicalSort;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java graph.Main <path_to_json_file>");
            System.err.println("Example: Running with default 'data/tasks.json'");
            args = new String[]{"data/tasks.json"};
        }

        String filepath = args[0];
        try {
            System.out.println("Loading graph from: " + filepath);
            GraphLoader.LoadedGraph loadedGraph = GraphLoader.loadGraph(filepath);
            Graph g = loadedGraph.graph;
            int originalSourceNode = loadedGraph.sourceNode;
            System.out.printf("Graph loaded: %d vertices, %d edges. Source node: %d\n",
                    g.getNumVertices(), g.getNumEdges(), originalSourceNode);
            System.out.println("---------------------------------------------");


            System.out.println("1. Running Tarjan's SCC Algorithm...");
            TarjanSCC sccFinder = new TarjanSCC(g);
            TarjanSCC.SccResult sccResult = sccFinder.findSccs();

            System.out.printf("   Found %d SCC(s).\n", sccResult.sccCount);
            for (int i = 0; i < sccResult.sccs.size(); i++) {
                System.out.printf("   - Component %d: %s\n", i, sccResult.sccs.get(i));
            }
            System.out.println("   Node-to-Component Map: " + Arrays.toString(sccResult.sccMap));
            System.out.println("   SCC Metrics: " + sccResult.metrics.getSummary());
            System.out.println("---------------------------------------------");

            System.out.println("2. Running Kahn's Topological Sort on Condensation Graph...");
            Graph dag = sccResult.condensedGraph;
            KahnTopologicalSort topoSort = new KahnTopologicalSort(dag);
            KahnTopologicalSort.TopoSortResult topoResult = topoSort.sort();

            List<Integer> topoOrder = topoResult.topologicalOrder;
            System.out.println("   Topological Order (of components): " + topoOrder);
            System.out.println("   Topo Sort Metrics: " + topoResult.metrics.getSummary());

            List<Integer> taskOrder = topoOrder.stream()
                    .flatMap(sccId -> sccResult.sccs.get(sccId).stream())
                    .collect(Collectors.toList());
            System.out.println("   Derived Full Task Order: " + taskOrder);
            System.out.println("---------------------------------------------");

            System.out.println("3. Running DAG Shortest/Longest Path Algorithms...");
            DagShortestPath dagSP = new DagShortestPath(dag, topoOrder);


            int sourceComponent = sccResult.sccMap[originalSourceNode];
            System.out.printf("   (Original Source %d -> Component %d)\n", originalSourceNode, sourceComponent);


            DagShortestPath.PathResult spResult = dagSP.getShortestPaths(sourceComponent);
            System.out.println("\n   --- Shortest Paths from Component " + sourceComponent + " ---");
            System.out.println("   Distances: " + formatDistances(spResult.distances, DagShortestPath.INFINITY));
            System.out.println("   Parents:   " + Arrays.toString(spResult.parents));
            System.out.println("   SP Metrics: " + spResult.metrics.getSummary());


            int lastComponent = topoOrder.get(topoOrder.size() - 1);
            List<Integer> spPath = spResult.getPath(lastComponent);
            System.out.printf("   Example Shortest Path (to C%d): %s\n", lastComponent, spPath);


            DagShortestPath.PathResult lpResult = dagSP.getLongestPaths(sourceComponent);
            int criticalPathLength = lpResult.getCriticalPathLength();
            System.out.println("\n   --- Longest Paths (Critical Path) from Component " + sourceComponent + " ---");
            System.out.println("   Distances: " + formatDistances(lpResult.distances, -DagShortestPath.INFINITY));
            System.out.println("   Parents:   " + Arrays.toString(lpResult.parents));
            System.out.println("   Critical Path Length (from source): " + criticalPathLength);
            System.out.println("   LP Metrics: " + lpResult.metrics.getSummary());


            int criticalPathEndNode = -1;
            for(int i=0; i<lpResult.distances.length; i++) {
                if (lpResult.distances[i] == criticalPathLength) {
                    criticalPathEndNode = i;
                    break;
                }
            }
            if(criticalPathEndNode != -1) {
                List<Integer> lpPath = lpResult.getPath(criticalPathEndNode);
                System.out.printf("   Example Critical Path (to C%d): %s\n", criticalPathEndNode, lpPath);
            }
            System.out.println("---------------------------------------------");


        } catch (IOException e) {
            System.err.println("Failed to load graph file: " + filepath);
            e.printStackTrace();
        } catch (IllegalStateException e) {
            System.err.println("Algorithm error (e.g., cycle in DAG): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String formatDistances(int[] dist, int inf) {
        String[] s = new String[dist.length];
        for (int i = 0; i < dist.length; i++) {
            s[i] = (dist[i] == inf ? "INF" : String.valueOf(dist[i]));
        }
        return Arrays.toString(s);
    }

}

