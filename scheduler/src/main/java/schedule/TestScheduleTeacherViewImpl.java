package schedule;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TestScheduleTeacherViewImpl {
    ScheduleTeacherView schedule;
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        schedule = new ScheduleTeacherViewImpl();
    }

    @org.junit.jupiter.api.Test
    void testIfWorkdayDoesNotThrow() {
        Date date = new Date(100000);
        assertDoesNotThrow(() -> schedule.ifWorkday(date));
    }

    @org.junit.jupiter.api.Test
    void testIfWorkdayNotNull() {
        Date date = new Date(100000);
        assertFalse(schedule.ifWorkday(date));
    }
}