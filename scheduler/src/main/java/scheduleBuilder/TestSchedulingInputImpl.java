package scheduleBuilder;

import group.Group;
import lesson.LessonRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import person.Teacher;
import place.Place;
import scheduleBuilder.data.*;
import time.WeeklyTimeSlot;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestSchedulingInputImpl {
    SchedulingInput schedulingInput;
    @BeforeEach
    void setUp() {
        ScheduleBuilderDataAccumulator dataAccumulator = new ScheduleBuilderDataAccumulatorImpl();
        List<WeeklyTimeSlot> timeslotSequence = List.of();
        List<Teacher> teachers = List.of();
        List<LessonRequest> lessonRequests = List.of();
        List<Group> groups = List.of();
        List<Place> places = List.of();
        ScheduleConstraintsAccumulator constraintAccumulator = new ScheduleConstraintsAccumulatorImpl();
        ScheduleObjectiveAccumulator objectiveAccumulator = new ScheduleObjectiveAccumulatorImpl();
        schedulingInput = new SchedulingInputImpl(timeslotSequence, teachers, lessonRequests, groups, places, constraintAccumulator, objectiveAccumulator);
    }

    @Test
    void testGetTimeslotSequence() {
        assertNotNull(schedulingInput.getTimeslotSequence());
       //assertEquals(10, schedulingInput.getTimeslotSequence().size());
    }

    @Test
    void getTeachers() {
    }

    @Test
    void testGetLessonRequests() {
        assertNotNull(schedulingInput.getLessonRequests());
    }
}