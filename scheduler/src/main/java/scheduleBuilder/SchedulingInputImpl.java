package scheduleBuilder;

import constraint.timeConstraint.ForbiddenTimeConstraint;
import constraint.timeConstraint.MandatoryTimeConstraint;
import group.Group;
import lesson.LessonRequest;
import pair.Pair;
import person.Teacher;
import place.Place;
import scheduleBuilder.data.ScheduleConstraintsAccumulator;
import scheduleBuilder.data.ScheduleObjectiveAccumulator;
import time.WeeklyTimeSlot;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SchedulingInputImpl implements SchedulingInput {
    private final List<WeeklyTimeSlot> _timeslotSequence;
    private final List<Teacher> _teachers;
    private final List<LessonRequest> _lessonRequests;
    private final List<Group> _groups;
    private final List<Place> _places;
    private final ScheduleConstraintsAccumulator _constraintsAccumulator;
    private final ScheduleObjectiveAccumulator _objectiveAccumulator;

    public SchedulingInputImpl(List<WeeklyTimeSlot> timeslotSequence, List<Teacher> teachers, List<LessonRequest> lessonRequests, List<Group> groups, List<Place> places, ScheduleConstraintsAccumulator constraintsAccumulator, ScheduleObjectiveAccumulator objectiveAccumulator) {
        _timeslotSequence = timeslotSequence;
        _teachers = teachers;

        _lessonRequests = lessonRequests;
        _groups = groups;
        _places = places;
        _constraintsAccumulator = constraintsAccumulator;
        _objectiveAccumulator = objectiveAccumulator;
    }

    @Override
    public List<WeeklyTimeSlot> getTimeslotSequence() {
        return _timeslotSequence;
    }

    @Override
    public List<Teacher> getTeachers() {
        return _teachers;
    }

    @Override
    public List<LessonRequest> getLessonRequests() {
        return _lessonRequests;
    }

    @Override
    public Optional<ForbiddenTimeConstraint<Teacher>> getTeacherForbiddenTimeSlots(Teacher teacher) {
        return _constraintsAccumulator.getForbiddenSlotsForTeacher(teacher);
    }

    @Override
    public int getMaxDaysForTeacher(Teacher teacher) {
        return _constraintsAccumulator.getNumDaysConstraintForTeacher(teacher).orElse(DayOfWeek.values().length);
    }

    @Override
    public List<Group> getGroups() {
        return _groups;
    }

    @Override
    public List<Place> getPlaces() {
        return _places;
    }


    @Override
    public Optional<ForbiddenTimeConstraint<Group>> getGroupForbiddenTimeSlots(Group group) {
        return _constraintsAccumulator.getForbiddenSlotsForGroup(group);
    }

    @Override
    public Optional<MandatoryTimeConstraint> getMandatorySlotsForGroup(Group group) {
        return _constraintsAccumulator.getMandatorySlotsForGroup(group);
    }

    @Override
    public int getMaxDaysForGroup(Group group) {
        return _constraintsAccumulator.getNumDaysConstraintForGroup(group).orElse(DayOfWeek.values().length);
    }

    @Override
    public Stream<Pair<DayOfWeek, Integer>> getDailyMaxLoadsForGroup(Group group) {
        return _constraintsAccumulator.getDailyMaxLoadsForGroup(group);
    }

    @Override
    public Stream<Pair<DayOfWeek, Integer>> getDailyMaxLoadsForTeacher(Teacher teacher) {
        return _constraintsAccumulator.getDailyMaxLoadsForTeacher(teacher);
    }
}
