package setCovering;

import graph.DirectColoredGraph;
import graph.DirectColoredGraphNodesImpl;
import pair.Pair;
import triplet.Triplet;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoveringsAccumulatorImpl<T> implements CoveringsAccumulator<T> {
    private final Map<String, Set<T>> _coveringsMap = new ConcurrentHashMap<>();

    @Override
    public void addCovering(String label, Stream<T> covering) {
        _coveringsMap.put(label, covering.collect(Collectors.toUnmodifiableSet()));
    }

    @Override
    public DirectColoredGraph<String, Set<T>> getCoveringsIntersectionsDirectGraph() {
        Stream<String> verticesStream = _coveringsMap.keySet().stream();
        Stream<Triplet<String,String, Set<T>>> directEdgesStream = createDirectEdgesStream();
        DirectColoredGraph<String, Set<T>> result = new DirectColoredGraphNodesImpl<>(verticesStream, directEdgesStream);
        return result;
    }

    private  Stream<Triplet<String,String, Set<T>>> createDirectEdgesStream() {
        return _coveringsMap.entrySet()
                .stream()
                .flatMap(coveringFromEntry -> {
                    String fromLabel = coveringFromEntry.getKey();
                    Set<T> fromSet = coveringFromEntry.getValue();
                    Stream<Pair<String, Set<T>>> toPairStream = createIntersectionPairsWith(fromLabel, fromSet);
                    return toPairStream
                            .map(pair -> new Triplet<>(fromLabel, pair.getKey(), pair.getValue()));
                });
    }

    private Stream<Pair<String, Set<T>>> createIntersectionPairsWith(String fromLabel, Set<T> fromSet) {
       return  _coveringsMap
                .entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(fromLabel))
                .map(entry -> {
                    String toLabel = entry.getKey();
                    Set<T> toSet = entry.getValue();
                    Set<T> intersection = toSet
                            .stream()
                            .filter(fromSet::contains)
                            .collect(Collectors.toUnmodifiableSet());
                    return new Pair<>(toLabel, intersection);
                })
                .filter(pair -> !pair.getValue().isEmpty());
    }


}
