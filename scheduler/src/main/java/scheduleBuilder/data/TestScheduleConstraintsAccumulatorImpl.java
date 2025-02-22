package scheduleBuilder.data;

import constraint.ScheduleConstraint;
import constraint.timeConstraint.ForbiddenTimeConstraintImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import person.Teacher;
import person.TeacherImpl;
import time.TimeTranslator;
import time.WeeklyTimeSlot;
import time.WeeklyTimeSlotImpl;

import java.time.DayOfWeek;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestScheduleConstraintsAccumulatorImpl {
    ScheduleConstraintsAccumulator accumulator;
    ScheduleConstraint teacherConstraint;
    @BeforeEach
    void setUp() {
        var forbiddenStart = TimeTranslator.getMsTimeByHoursAndMins(10, 30);
        var forbiddenEnd = TimeTranslator.getMsTimeByHoursAndMins(15, 30);
        WeeklyTimeSlot inconsistentTimeSlot = new WeeklyTimeSlotImpl(DayOfWeek.THURSDAY, forbiddenStart, forbiddenEnd);
        List<WeeklyTimeSlot> forbiddenTimeIntervals = List.of(inconsistentTimeSlot);
        Teacher teacher = new TeacherImpl("Anna");
        teacherConstraint = new ForbiddenTimeConstraintImpl<>(teacher, forbiddenTimeIntervals);
        accumulator = new ScheduleConstraintsAccumulatorImpl();
    }

}