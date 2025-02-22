package scheduleBuilder;

import group.Group;
import lesson.LessonRequest;
import lesson.LessonRequestOccupation;
import person.Teacher;
import place.Place;
import time.WeeklyTimeSlot;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface SchedulingInputV2 {
    void setTeachersDailyMaxLoadConstraint(Map<Teacher, Map<DayOfWeek, Integer>> teacherDailyMaxLoad);
    void setGroupDailyMaxLoadConstraint(Map<Group, Map<DayOfWeek, Integer>> groupDailyMaxLoad);
    void setTeacherAvailableTimeSlots(Map<Teacher, Set<WeeklyTimeSlot>> teacherAvailableTimeSlots);
    void setGroupAvailableTimeSlots(Map<Group, Set<WeeklyTimeSlot>> groupAvailableTimeSlots);
    void setGroupMandatoryTimeSlots(Map<Group, Set<WeeklyTimeSlot>> groupMandatoryTimeSlots);

    void setTeacherMandatoryTimeSlots(Map<Teacher, Set<WeeklyTimeSlot>> teacherMandatoryTimeSlots);

    void setPlaceAvailableTimeSlots(Map<Place, Set<WeeklyTimeSlot>> placeAvailableTimeSlots);
    void setTeacherMandatoryDays(Map<Teacher, Set<DayOfWeek>> teacherMandatoryDays);
    void setTeacherMaxDaysMap(Map<Teacher, Integer> teacherMaxDaysMap);
    void setLessonRequestAvailableTimeSlots(Map<LessonRequest, Set<WeeklyTimeSlot>> lessonRequestTimeSlotsMap);

    String getTimeSlotId(WeeklyTimeSlot timeSlot);
    String getTeacherId(Teacher teacher);
    String getGroupId(Group group);
    String getPlaceId(Place place);

    Stream<LessonRequest> getLessonRequestsForTeacher(Teacher teacher);

    List<WeeklyTimeSlot> getTimeslotSequence();
    List<WeeklyTimeSlot> getTimeslotsSequenceForDay(DayOfWeek dayOfWeek);

    List<LessonRequest> getLessonRequests();
    List<Teacher> getTeachers();
    List<Group> getGroups();
    List<Place> getPlaces();
    Stream<DayOfWeek> getDays();

    // TIME CONSTRAINTS
    Stream<WeeklyTimeSlot> getAvailableSlotsForTeacher(Teacher teacher);
    Stream<WeeklyTimeSlot> getAvailableSlotsForGroup(Group group);
    Stream<WeeklyTimeSlot> getAvailableSlotsForLessonRequest(LessonRequest lessonRequest);

    Stream<Place> getAvailablePlacesForTeacher(Teacher teacher);
    Stream<LessonRequest> getLessonRequestsForGroup(Group group);
    Stream<LessonRequest> getLessonRequestsForPlace(Place place);

    boolean ifTimeSlotAvailableForLessonRequest(LessonRequest lessonRequest, WeeklyTimeSlot timeSlot);
    boolean ifTimeSlotAvailableForGroup(Group group, WeeklyTimeSlot timeSlot);
    boolean ifTimeSlotAvailableForTeacher(Teacher teacher, WeeklyTimeSlot timeSlot);

    boolean ifTimeSlotMandatoryForTeacher(Teacher teacher, WeeklyTimeSlot timeSlot);
    boolean ifTimeSlotMandatoryForGroup(Group group, WeeklyTimeSlot timeSlot);
    boolean ifDayMandatoryForTeacher(Teacher teacher, DayOfWeek day);

    boolean ifDayAvailableForTeacher(Teacher teacher, DayOfWeek day);
    boolean ifDayAvailableForGroup(Group group, DayOfWeek dow);

    // CONSTRAINTS WHICH MAYBE SOFT
    int getMaxDaysForTeacher(Teacher teacher);
    int getTeacherInDayStartTimeslotLb(Teacher teacher, DayOfWeek dayOfWeek);
    int getTeacherInDayStartTimeslotUb(Teacher teacher, DayOfWeek dayOfWeek);
    int getTeacherDailyMinTimeslotLoad(Teacher teacher, DayOfWeek dayOfWeek);
    int getTeacherDailyMaxTimeslotLoad(Teacher teacher, DayOfWeek dayOfWeek);

    int getGroupInDayStartLb(Group group, DayOfWeek dayOfWeek);
    int getGroupInDayStartUb(Group group, DayOfWeek dayOfWeek);
    int getGroupDailyMinLoad(Group group, DayOfWeek dayOfWeek);
    int getGroupDailyMaxLoad(Group group, DayOfWeek dayOfWeek);

    // THIS MAYBE SOFT CONSTRAINT
    Stream<DayOfWeek> getAvailableDaysForTeacher(Teacher teacher);
    // THIS IS HARD CONSTRAINT
    Stream<DayOfWeek> getAvailableDaysForGroup(Group group);
    // TODO UNDERSTAND WHAT IS IT
    int getMaxOccupationsInSlot();

    LessonRequest getLessonRequest(String lessonId);
    Place getPlace(String placeId);
    WeeklyTimeSlot getTimeSlot(String timeSlotId);

    // LOOKS LIKE HARD CONSTRAINTS
    Stream<DayOfWeek> getAvailableDaysForLessonRequest(LessonRequest lessonRequest);
    int getLessonRequestDayStartTimeSlotLb(LessonRequest lessonRequest, DayOfWeek dayOfWeek);
    int getLessonRequestLatestTimeSlot(LessonRequest lessonRequest, DayOfWeek dayOfWeek);

    //TODO WHY DO WE NEED THIS METHOD? WE ALREADY HAVE INFO IN OCCUPATION
    Stream<Place> getAvailablePlacesForOccupation(LessonRequestOccupation occupation);
    Stream<Place> getAvailablePlacesForOccupationAtTimeSlot(LessonRequestOccupation occupation, WeeklyTimeSlot timeSlot);
    Optional<Place> occupationScheduledInPlace(LessonRequestOccupation occupation, WeeklyTimeSlot timeSlot);


    boolean ifDayMandatoryForLessonRequest(LessonRequest lessonRequest, DayOfWeek day);


    int getTotalTeacherLessonsLoad(Teacher teacher);
    @Deprecated
    boolean ifDayAvailableForLessonRequest(LessonRequest lessonRequest, DayOfWeek dayOfWeek);
}
