package scheduleBuilder;

import group.Group;
import lesson.Lesson;
import lesson.LessonRequest;
import lesson.LessonRequestOccupation;
import pair.Pair;
import person.Teacher;
import place.Place;
import time.WeeklyTimeSlot;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public interface SchedulingInputData {
    // PERMANENT DATA
    Stream<Teacher> getTeachers();
    Stream<Group> getGroups();
    Stream<Place> getPlaces();
    Stream<LessonRequest> getLessonRequests();
    // TEMP DATA
    Stream<WeeklyTimeSlot> getAvailableSlotsForLessonRequest(LessonRequest lessonRequest);
    Stream<WeeklyTimeSlot> getOccupiedTimeSlotsForPlace(Place place);
    Stream<WeeklyTimeSlot> getOccupiedTimeSlotsForGroup(Group group);
    Stream<WeeklyTimeSlot> getOccupiedTimeSlotsForTeacher(Teacher teacher);
    // TO SCHEDULE
    Stream<Pair<LessonRequestOccupation, Integer>> getUnscheduledRequestsForTeacher(Teacher teacher);
    Stream<Pair<LessonRequestOccupation, Integer>> getUnscheduledRequestsForGroup(Group group);
    Map<Teacher, Map<DayOfWeek, Integer>> getTeacherDailyMaxLoadMap();
    Map<Group, Map<DayOfWeek, Integer>> getGroupDailyMaxLoadMap();
    Map<Teacher, Set<WeeklyTimeSlot>> getTeacherAvailableTimeSlotsMap();
    Map<Group, Set<WeeklyTimeSlot>> getGroupAvailableTimeSlotsMap();
    Map<Group, Set<WeeklyTimeSlot>> getGroupMandatoryTimeSlotsMap();
    Map<Place, Set<WeeklyTimeSlot>> getPlaceAvailableTimeSlotsMap();
    Map<Teacher, Set<DayOfWeek>> getTeacherMandatoryDaysMap();
    Map<Teacher, Integer> getTeacherMaxDaysMap();
    Map<LessonRequest, Set<WeeklyTimeSlot>> getLessonRequestTimeSlotsMap();
    // ALREADY SCHEDULED
    Stream<Lesson> getScheduledLessonsForTeacher(Teacher teacher);
    Stream<Lesson> getScheduledLessonsForGroup(Group group);
    Stream<Lesson> getScheduleLessonsForPlace(Place place);
    Stream<Lesson> getScheduledLessons();

    // MODIFY
    void scheduleLesson(Lesson lesson);
    void forbidLessonRequest(LessonRequest lessonRequest, WeeklyTimeSlot timeSlot);
    Stream<Place> getAvailablePlacesForOccupationInTimeSlot(LessonRequestOccupation lessonRequestOccupation, WeeklyTimeSlot timeSlot);

    Map<Teacher, Set<WeeklyTimeSlot>> getTeacherMandatoryTimeSlotsMap();

    Stream<Place> getAvailablePlacesForOccupation(LessonRequestOccupation occupation);
}
