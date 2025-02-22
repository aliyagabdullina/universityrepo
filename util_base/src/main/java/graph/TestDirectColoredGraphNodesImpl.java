package graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import triplet.Triplet;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestDirectColoredGraphNodesImpl {
    DirectColoredGraph<String, Boolean> directColoredGraph;

    @BeforeEach
    void setUp() {
        var tAB = new Triplet<>("A", "B", true);
        var tAC = new Triplet<>("A", "C", true);
        directColoredGraph = new DirectColoredGraphNodesImpl<>(Stream.of("A", "B", "C"), Stream.of(tAB, tAC));
    }

    @Test
    void testConstructor() {
        var tAB = new Triplet<>("A", "B", true);
        assertThrows(IllegalArgumentException.class, () -> new DirectColoredGraphNodesImpl<>(Stream.of("B"), Stream.of(tAB)));
        assertThrows(IllegalArgumentException.class, () -> new DirectColoredGraphNodesImpl<>(Stream.of("A"), Stream.of(tAB)));
    }

    @Test
    void testGetNodesSet() {
        assertNotNull(directColoredGraph.getVertices());
        assertEquals(3, directColoredGraph.getVertices().count());
    }

    @Test
    void testGetEdgeTriplets() {
        assertNotNull(directColoredGraph.getEdges());
        assertEquals(2, directColoredGraph.getEdges().count());
    }

    @Test
    void testAddVertex() {
        assertDoesNotThrow(() -> directColoredGraph.addVertex("X"));
        assertEquals(4, directColoredGraph.getVertices().count());
    }

    @Test
    void testAddEdge() {
        assertDoesNotThrow(() -> directColoredGraph.addEdge(new Triplet<>("B", "C", false)));
        assertEquals(3, directColoredGraph.getEdges().count());
    }

    @Test
    void testAddEdgeException() {
        assertThrows(IllegalArgumentException.class, () -> directColoredGraph.addEdge(new Triplet<>("D", "C", false)));
        assertThrows(IllegalArgumentException.class, () -> directColoredGraph.addEdge(new Triplet<>("C", "D", false)));
    }

    @Test
    void testGetEdgesFromSuccess() {
        assertNotNull(directColoredGraph.getEdgesFrom("A"));
        assertEquals(2, directColoredGraph.getEdgesFrom("A").count());
        assertEquals(0, directColoredGraph.getEdgesFrom("B").count());
    }

    @Test
    void testGetEdgesFromException() {
        assertThrows(IllegalArgumentException.class, () -> directColoredGraph.getEdgesFrom("X"));
    }
}