package setCovering;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestCoveringsAccumulatorImpl {
    CoveringsAccumulator<Integer> coveringsAccumulator;
    @BeforeEach
    void setUp() {
        coveringsAccumulator = new CoveringsAccumulatorImpl<>();
    }

    @Test
    void testAddCovering() {
        assertDoesNotThrow(() -> coveringsAccumulator.addCovering("a", Stream.empty()));
    }

    @Test
    void testIntersectedCoveragesGraph() {
        coveringsAccumulator.addCovering("C1",Stream.of(1, 2, 3));
        coveringsAccumulator.addCovering("C2",Stream.of(1));
        coveringsAccumulator.addCovering("C3",Stream.of(2));
        assertNotNull(coveringsAccumulator.getCoveringsIntersectionsDirectGraph());

        assertEquals(4, coveringsAccumulator.getCoveringsIntersectionsDirectGraph().getEdges().count());
    }
}