package scheduleBuilder;

import constraint.timeConstraint.ForbiddenTimeConstraint;
import constraint.timeConstraint.MandatoryTimeConstraint;
import group.Group;
import lesson.LessonRequest;
import pair.Pair;
import person.Teacher;
import place.Place;
import time.WeeklyTimeSlot;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


public interface SchedulingInput {
    List<WeeklyTimeSlot> getTimeslotSequence();
    List<Teacher> getTeachers();
    List<LessonRequest> getLessonRequests();

    Optional<ForbiddenTimeConstraint<Teacher>> getTeacherForbiddenTimeSlots(Teacher teacher);

    int getMaxDaysForTeacher(Teacher teacher);

    List<Group> getGroups();
    Optional<ForbiddenTimeConstraint<Group>> getGroupForbiddenTimeSlots(Group group);

    Optional<MandatoryTimeConstraint> getMandatorySlotsForGroup(Group group);

    int getMaxDaysForGroup(Group group);
    List<Place> getPlaces();

    Stream<Pair<DayOfWeek, Integer>> getDailyMaxLoadsForGroup(Group group);

    Stream<Pair<DayOfWeek, Integer>> getDailyMaxLoadsForTeacher(Teacher t);
}
