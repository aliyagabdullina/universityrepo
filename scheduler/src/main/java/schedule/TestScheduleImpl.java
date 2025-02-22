package schedule;

import lesson.Lesson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestScheduleImpl {
    Schedule schedule;

    @BeforeEach
    void setUp() {
        schedule = DummyScheduleCreator.createOneGroupSchedule();
    }

    @Test
    void testGetAllLessons() {
        assertNotNull(schedule.getAllLessons());
        assertTrue(schedule.getAllLessons().findAny().isPresent());
    }


}