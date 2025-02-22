package graph;

import org.junit.jupiter.api.function.Executable;
import pair.Pair;
import triplet.Triplet;

import java.util.stream.Stream;

public interface DirectColoredGraph<V, C> {
    Stream<V> getVertices();
    Stream<Triplet<V, V, C>> getEdges();
    void addVertex(V v);
    void addEdge(Triplet<V, V, C> edge);

    Stream<Pair<V, C>> getEdgesFrom(V vertexFrom);
}
