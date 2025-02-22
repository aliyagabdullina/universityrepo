package scheduleBuilder.translators.milp;

import group.Group;
import lesson.Lesson;
import lesson.LessonRequest;
import lesson.LessonRequestOccupation;
import model.MilpModel;
import pair.Pair;
import person.Teacher;
import place.Place;
import time.WeeklyTimeSlot;

import java.time.DayOfWeek;
import java.util.stream.Stream;

public interface SchoolMilpModelBuilder {


    MilpModel getMilpModel();
    Stream<Lesson> createLessonsStream(Stream<Pair<String, Double>> varValues);
    // VARIABLES
    void addTeacherDayVariable(Teacher teacher, DayOfWeek dayOfWeek);
    void addTeacherTimeslotVariable(Teacher teacher, WeeklyTimeSlot timeSlot);
    void addGroupTimeslotVariable(Group group, WeeklyTimeSlot timeSlot);
    void addLessonRequestOccupyPlacesAtTimeVariables(LessonRequest lessonRequest, WeeklyTimeSlot timeSlot);
    void addGroupDayVariables(Group group, DayOfWeek dayOfWeek);

    // MANDATORY CONSTRAINTS
    void addLessonRequestsSatisfied(LessonRequest lessonRequest);
    void addNotMoreThanOneLessonPerTeacherAtTimeSlot(Teacher teacher, WeeklyTimeSlot timeSlot);
    void addNotMoreThanOneOccupationPerPlaceAtTimeSlot(Place place, WeeklyTimeSlot timeSlot);
    void addExactlyOneLessonPerGroupAtTimeSlots(Group group, WeeklyTimeSlot timeSlot);
    void addGroupDayTimeslotVarConnections(Group group, DayOfWeek dayOfWeek);
    void addTeacherDayStartDurationTimeslotVarConnections(Teacher teacher, DayOfWeek dayOfWeek);
    void addLessonRequestDailyContinuity(LessonRequest lessonRequest, DayOfWeek dayOfWeek);
    void addTeacherDayTimeSlotVariablesRelations(Teacher teacher);
    // OPTIONAL CONSTRAINTS
    void addEachOccupationOfRequestShouldBeInTheSamePlace(LessonRequestOccupation occupation);
    void addMaxLessonsPerDay(LessonRequest lessonRequest);
    void addMaxDaysForTeacher(Teacher teacher, int maxDays);
    void addMaxDaysForLessonRequest(LessonRequest lessonRequest);

    //OBJECTIVE COMPONENTS
    void addTeacherMinDays(double coefficient);
    void addTeacherMinSumDuration(double coefficient);

    // void addTotalTeachersMinWindows(double coefficient);
    // void addTotalGroupComplexityViolation(int coefficient);
    // CUTS
    void addTeacherTotalDurationCut();
}
