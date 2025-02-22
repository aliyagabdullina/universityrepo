package time;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

class TestWeeklyTimeSlotImpl {
    WeeklyTimeSlot weeklyTimeSlot;
    long start;
    long end;
    @BeforeEach
    void setUp() {
        start = TimeTranslator.getMsTimeByHoursAndMins(9, 15);
        end = TimeTranslator.getMsTimeByHoursAndMins(10, 30);
        weeklyTimeSlot = new WeeklyTimeSlotImpl(DayOfWeek.THURSDAY, start, end);
    }


    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new WeeklyTimeSlotImpl(DayOfWeek.THURSDAY, end, start));
    }

    @Test
    void testGetDayOfWeek() {
        assertEquals(DayOfWeek.THURSDAY, weeklyTimeSlot.getDayOfWeek());
    }

    @Test
    void testGetStartTimeInMs() {
        assertEquals(start, weeklyTimeSlot.getStartTimeInMs());
    }

    @Test
    void testGetEndTimeInMs() {
        assertEquals(end, weeklyTimeSlot.getEndTimeInMs());
    }

    @Test
    void testCompareToWeekDayLess() {
        var earlierTimeSlot = new WeeklyTimeSlotImpl(DayOfWeek.WEDNESDAY, start + 1, end+1);
        assertTrue(earlierTimeSlot.compareTo(weeklyTimeSlot) < 0);
    }

    @Test
    void testCompareToWeekDayMore() {
        var laterTimeSlot = new WeeklyTimeSlotImpl(DayOfWeek.FRIDAY, start - 1, end-1);
        assertTrue(laterTimeSlot.compareTo(weeklyTimeSlot) > 0);
    }

    @Test
    void testCompareToEquals() {
        var earlierTimeSlot = new WeeklyTimeSlotImpl(DayOfWeek.THURSDAY, start, end);
        assertEquals(0, earlierTimeSlot.compareTo(weeklyTimeSlot));
    }

    @Test
    void testCompareToLessTime() {
        var earlierTimeSlot1 = new WeeklyTimeSlotImpl(DayOfWeek.THURSDAY, start-1, end);
        var earlierTimeSlot2 = new WeeklyTimeSlotImpl(DayOfWeek.THURSDAY, start, end-1);
        assertTrue(earlierTimeSlot1.compareTo(weeklyTimeSlot) < 0);
        assertTrue(earlierTimeSlot2.compareTo(weeklyTimeSlot) < 0);
    }




    @Test
    void testCompareToMoreTime() {
        var earlierTimeSlot1 = new WeeklyTimeSlotImpl(DayOfWeek.THURSDAY, start-1, end);
        var earlierTimeSlot2 = new WeeklyTimeSlotImpl(DayOfWeek.THURSDAY, start, end-1);
        assertTrue(weeklyTimeSlot.compareTo(earlierTimeSlot1) > 0);
        assertTrue(weeklyTimeSlot.compareTo(earlierTimeSlot2) > 0);
    }

    @Test
    void ifIntersectsLeftTrue() {
        var earlierTimeSlot1 = new WeeklyTimeSlotImpl(DayOfWeek.THURSDAY, start-1, end-10);
        assertTrue(weeklyTimeSlot.ifIntersects(earlierTimeSlot1));
    }

    @Test
    void ifIntersectsLeftFalse() {
        var earlierTimeSlot1 = new WeeklyTimeSlotImpl(DayOfWeek.THURSDAY, start-10, start-1);
        assertFalse(weeklyTimeSlot.ifIntersects(earlierTimeSlot1));
    }
}