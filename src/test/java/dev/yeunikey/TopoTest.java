package dev.yeunikey;

import dev.yeunikey.common.Graph;
import dev.yeunikey.topo.KahnTopologicalSort;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TopoTest {

    @Test
    public void testLinearDag() {

        Graph g = new Graph(4);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 3, 1);

        KahnTopologicalSort sorter = new KahnTopologicalSort(g);
        List<Integer> order = sorter.sort().topologicalOrder;

        assertEquals(List.of(0, 1, 2, 3), order);
    }

    @Test
    public void testForkJoinDag() {
        Graph g = new Graph(5);
        g.addEdge(0, 1, 1);
        g.addEdge(0, 2, 1);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 4, 1);

        KahnTopologicalSort sorter = new KahnTopologicalSort(g);
        List<Integer> order = sorter.sort().topologicalOrder;

        assertEquals(5, order.size());
        assertEquals(0, (int)order.get(0));
        assertEquals(4, (int)order.get(4));


        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    public void testDetectCycle() {

        Graph g = new Graph(2);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 0, 1);

        KahnTopologicalSort sorter = new KahnTopologicalSort(g);


        assertThrows(IllegalStateException.class, sorter::sort);
    }
}
