package scheduleBuilder.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import person.Teacher;
import person.TeacherImpl;
import scheduleMetric.ScheduleMetric_IfTeacherWorkInDay;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

class TestScheduleObjectiveAccumulatorImpl {
    ScheduleObjectiveAccumulator scheduleObjectiveAccumulator;

    @BeforeEach
    void setUp() {
        scheduleObjectiveAccumulator = new ScheduleObjectiveAccumulatorImpl();
    }

    @Test
    void testAddObjectiveComponent() {
        Teacher teacher = new TeacherImpl("teacher_0");
        var mondayMetric = new ScheduleMetric_IfTeacherWorkInDay(teacher, DayOfWeek.MONDAY);
        assertDoesNotThrow(() -> scheduleObjectiveAccumulator.addObjectiveComponent(mondayMetric, 1));
    }
}