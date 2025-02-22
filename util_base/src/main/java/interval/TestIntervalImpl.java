package interval;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestIntervalImpl {
    Interval<Integer> interval;

    @BeforeEach
    void setUp() {
        interval = new IntervalImpl<>(1000, 2000);
    }

    @Test
    void testConstructorException() {
        assertThrows(IllegalArgumentException.class, () ->  new IntervalImpl<>(10, -10));
    }

    @Test
    void testGetStart() {
        assertEquals(1000, interval.getStart());
    }

    @Test
    void testGetEnd() {
        assertEquals(2000, interval.getEnd());
    }

    @Test
    void testIfIntersectsInnerIntervalTrue() {
        var innerInterval = new IntervalImpl<>(1020, 1030);
        assertTrue(interval.ifIntersects(innerInterval));
    }

    @Test
    void testIfIntersectsLeftIntervalTrue() {
        var innerInterval = new IntervalImpl<>(900, 1030);
        assertTrue(interval.ifIntersects(innerInterval));
    }

    @Test
    void testIfIntersectsRightIntervalTrue() {
        var innerInterval = new IntervalImpl<>(1500, 2030);
        assertTrue(interval.ifIntersects(innerInterval));
    }

    @Test
    void testIfIntersectsWiderIntervalTrue() {
        var innerInterval = new IntervalImpl<>(900, 2030);
        assertTrue(interval.ifIntersects(innerInterval));
    }


    @Test
    void testIfIntersectsInnerOuterIntervalLeftFalse() {
        var innerInterval = new IntervalImpl<>(20, 30);
        assertFalse(interval.ifIntersects(innerInterval));
    }

    @Test
    void testIfIntersectsInnerOuterIntervalLeftBorderFalse() {
        var innerInterval = new IntervalImpl<>(20, 1000);
        assertFalse(interval.ifIntersects(innerInterval));
    }

    @Test
    void testIfIntersectsInnerOuterIntervalRightFalse() {
        var innerInterval = new IntervalImpl<>(2020, 2030);
        assertFalse(interval.ifIntersects(innerInterval));
    }

    @Test
    void testIfIntersectsInnerOuterIntervalRightBorderFalse() {
        var innerInterval = new IntervalImpl<>(2000, 2010);
        assertFalse(interval.ifIntersects(innerInterval));
    }

    @Test
    void testEqualsTrue() {
        assertEquals( new IntervalImpl<>(1000, 2000), interval);
    }
}