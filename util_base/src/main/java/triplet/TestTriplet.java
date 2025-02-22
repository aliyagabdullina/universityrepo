package triplet;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

class TestTriplet {

    @Test
    void testContent() {
        Triplet<Integer, String, Double> triplet = new Triplet<>(3, "A", 10.2);
        assertEquals(Optional.of(3), triplet.getFirst());
        assertEquals("A", triplet.getSecond());
        assertEquals(Optional.of(10.2), triplet.getThird());
    }
}