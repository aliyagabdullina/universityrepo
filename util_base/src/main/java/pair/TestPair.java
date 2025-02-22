package pair;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestPair {
    Pair<Integer, Double> testPair;

    @BeforeEach
    void setUp() {
        testPair = new Pair<>(1, 10.0);
    }

    @Test
    void testGetKey() {
        assertEquals(1, testPair.getKey());
    }


    @Test
    void testGetValue() {
        assertEquals(10.0, testPair.getValue());
    }

}