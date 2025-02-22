package setCovering;

import graph.DirectColoredGraph;

import java.util.Set;
import java.util.stream.Stream;

public interface CoveringsAccumulator<T> {
    void addCovering(String label, Stream<T> covering);
    DirectColoredGraph<String, Set<T>> getCoveringsIntersectionsDirectGraph();
}
