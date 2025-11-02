package graph.common;

import java.util.Objects;

public class Edge {

    public final int to;
    public final int weight;

    public Edge(int to, int weight) {
        this.to = to;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return String.format("(->%d, w:%d)", to, weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return to == edge.to && weight == edge.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, weight);
    }

}

