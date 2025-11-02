package dev.yeunikey;

import dev.yeunikey.common.Graph;
import dev.yeunikey.dagsp.DagShortestPath;
import dev.yeunikey.topo.KahnTopologicalSort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class DagSpTest {

    private Graph g;
    private List<Integer> topoOrder;

    @BeforeEach
    public void setUp() {
        g = new Graph(5);
        g.addEdge(0, 1, 3);
        g.addEdge(0, 2, 5);
        g.addEdge(1, 4, 8);
        g.addEdge(2, 3, 2);
        g.addEdge(3, 4, 6);

        topoOrder = new KahnTopologicalSort(g).sort().topologicalOrder;
    }

    @Test
    public void testShortestPaths() {
        DagShortestPath sp = new DagShortestPath(g, topoOrder);
        DagShortestPath.PathResult result = sp.getShortestPaths(0);

        int[] expectedDist = {0, 3, 5, 7, 11};
        assertArrayEquals(expectedDist, result.distances);

        int[] expectedParents = {-1, 0, 0, 2, 1};
        assertArrayEquals(expectedParents, result.parents);
    }

    @Test
    public void testLongestPaths() {
        DagShortestPath lp = new DagShortestPath(g, topoOrder);
        DagShortestPath.PathResult result = lp.getLongestPaths(0);

        int[] expectedDist = {0, 3, 5, 7, 13};
        assertArrayEquals(expectedDist, result.distances);

        int[] expectedParents = {-1, 0, 0, 2, 3};
        assertArrayEquals(expectedParents, result.parents);

        List<Integer> path = result.getPath(4);
        assertEquals(List.of(0, 2, 3, 4), path);

        assertEquals(13, result.getCriticalPathLength());
    }

    @Test
    public void testUnreachableNode() {
        Graph g2 = new Graph(7);
        g2.addEdge(0, 1, 3); g2.addEdge(0, 2, 5); g2.addEdge(1, 4, 8);
        g2.addEdge(2, 3, 2); g2.addEdge(3, 4, 6); g2.addEdge(5, 6, 1);

        topoOrder = new KahnTopologicalSort(g2).sort().topologicalOrder;

        DagShortestPath sp = new DagShortestPath(g2, topoOrder);
        DagShortestPath.PathResult result = sp.getShortestPaths(0);

        assertEquals(0, result.distances[0]);
        assertEquals(11, result.distances[4]);
        assertEquals(DagShortestPath.INFINITY, result.distances[5]);
        assertEquals(DagShortestPath.INFINITY, result.distances[6]);
    }

}

