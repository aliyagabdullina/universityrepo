package graph;

import pair.Pair;
import triplet.Triplet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectColoredGraphNodesImpl<V, C> implements DirectColoredGraph<V, C> {
    private final Map<V, Map<V, C>> _nodes;

    public DirectColoredGraphNodesImpl() {
        _nodes = new ConcurrentHashMap<>();
    }

    public DirectColoredGraphNodesImpl(Stream<V> verticesStream, Stream<Triplet<V,V, C>> directEdgesStream) {
        Set<V> veritices = verticesStream.collect(Collectors.toSet());
         _nodes = directEdgesStream
                 .peek(triplet -> checkTriplet(triplet, veritices))
                 .collect(Collectors.groupingBy(Triplet::getFirst,Collectors.toMap(Triplet::getSecond,Triplet::getThird)));
        veritices.forEach(v -> _nodes.putIfAbsent(v, new HashMap<>()));
    }

    private void checkTriplet(Triplet<V, V, C> triplet, final Set<V> vertices) {
        if(!vertices.contains(triplet.getFirst())) {
            throw undefinedVertexException(triplet.getFirst());
        }
        if(!vertices.contains(triplet.getSecond())) {
            throw undefinedVertexException(triplet.getSecond());
        }
    }

    private IllegalArgumentException undefinedVertexException(V v) {
        return new IllegalArgumentException("Vertex " + v.toString() + " undefined");
    }

    @Override
    public Stream<V> getVertices() {
        return _nodes.keySet().stream();
    }

    @Override
    public Stream<Triplet<V, V, C>> getEdges() {
        return _nodes.entrySet()
                .stream()
                .flatMap(entry -> {
                    V vertexFrom = entry.getKey();
                    return entry.getValue().entrySet()
                            .stream()
                            .map(vertexToEdge -> new Triplet<>(vertexFrom, vertexToEdge.getKey(), vertexToEdge.getValue()));
                });
    }

    @Override
    public void addVertex(V v) {
        _nodes.putIfAbsent(v, new HashMap<>());
    }

    @Override
    public void addEdge(Triplet<V, V, C> edge) {
        if(!_nodes.containsKey(edge.getFirst()) || !_nodes.containsKey(edge.getSecond())) {
           throw new IllegalArgumentException("Incorrect edge: " + edge);
        }
        Map<V, C> outgoingEdges = _nodes.get(edge.getFirst());
        outgoingEdges.put(edge.getSecond(), edge.getThird());
    }

    @Override
    public Stream<Pair<V, C>> getEdgesFrom(V vertexFrom) {
        if(!_nodes.containsKey(vertexFrom)) {
            throw new IllegalArgumentException("Incorrect vertex: " + vertexFrom);
        }
        return _nodes.get(vertexFrom).entrySet()
                .stream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()));
    }
}
