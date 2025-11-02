package graph;

import graph.common.Graph;
import graph.scc.TarjanSCC;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

public class SccTest {

    @Test
    public void testSimpleCycle() {

        Graph g = new Graph(4);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);
        g.addEdge(0, 3, 1);

        TarjanSCC sccFinder = new TarjanSCC(g);
        TarjanSCC.SccResult result = sccFinder.findSccs();

        assertEquals(2, result.sccCount);


        int scc012 = result.sccMap[0];
        assertEquals(scc012, result.sccMap[1]);
        assertEquals(scc012, result.sccMap[2]);
        assertNotEquals(scc012, result.sccMap[3]);


        Graph dag = result.condensedGraph;
        assertEquals(2, dag.getNumVertices());

        int scc3 = result.sccMap[3];

        assertEquals(1, dag.getNumEdges());
        assertTrue(dag.getNeighbors(scc012).stream().anyMatch(e -> e.to == scc3));
    }

    @Test
    public void testTasksJsonGraph() {
        Graph g = new Graph(8);
        g.addEdge(0, 1, 3);
        g.addEdge(1, 2, 2);
        g.addEdge(2, 3, 4);
        g.addEdge(3, 1, 1);
        g.addEdge(4, 5, 2);
        g.addEdge(5, 6, 5);
        g.addEdge(6, 7, 1);
        g.addEdge(1, 4, 6);

        TarjanSCC sccFinder = new TarjanSCC(g);
        TarjanSCC.SccResult result = sccFinder.findSccs();

        assertEquals(6, result.sccCount);

        int scc123 = result.sccMap[1];
        assertEquals(scc123, result.sccMap[2]);
        assertEquals(scc123, result.sccMap[3]);

        Set<Integer> otherSccs = Set.of(result.sccMap[0], result.sccMap[4], result.sccMap[5], result.sccMap[6], result.sccMap[7]);
        assertEquals(5, otherSccs.size());
        assertFalse(otherSccs.contains(scc123));


        Graph dag = result.condensedGraph;
        int scc0 = result.sccMap[0];
        int scc4 = result.sccMap[4];


        assertTrue(dag.getNeighbors(scc0).stream().anyMatch(e -> e.to == scc123));

        assertTrue(dag.getNeighbors(scc123).stream().anyMatch(e -> e.to == scc4));
    }
}



