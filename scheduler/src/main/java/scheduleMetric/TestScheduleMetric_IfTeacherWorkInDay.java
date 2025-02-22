package scheduleMetric;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import person.Teacher;
import person.TeacherImpl;
import schedule.DummyScheduleCreator;
import schedule.Schedule;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestScheduleMetric_IfTeacherWorkInDay {
    Schedule dummySchedule;
    ScheduleMetric mondayMetric;
    ScheduleMetric tuesdayMetric;

    @BeforeEach
    void setUp() {
        Teacher teacher = new TeacherImpl("teacher_0");
        dummySchedule = DummyScheduleCreator.createOneGroupSchedule();
        mondayMetric = new ScheduleMetric_IfTeacherWorkInDay(teacher, DayOfWeek.MONDAY);
        tuesdayMetric = new ScheduleMetric_IfTeacherWorkInDay( teacher, DayOfWeek.TUESDAY);
    }

    @Test
    void testMeasure1() {
        assertEquals(1, mondayMetric.measure(dummySchedule.getAllLessons()));
    }

    @Test
    void testMeasure0() {
        assertEquals(0, tuesdayMetric.measure(dummySchedule.getAllLessons()));
    }

}