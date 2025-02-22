package scheduleBuilder.data;

import constraint.timeConstraint.ForbiddenTimeConstraint;
import constraint.timeConstraint.MandatoryTimeConstraint;
import group.Group;
import pair.Pair;
import person.Teacher;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScheduleConstraintsAccumulatorImpl implements ScheduleConstraintsAccumulator {
    private final Map<Teacher, ForbiddenTimeConstraint<Teacher>> _teacherForbiddenTimeConstraints = new ConcurrentHashMap<>();
    private final Map<Teacher, Integer> _teacherMaxDaysConstraint = new ConcurrentHashMap<>();
    private final Map<Group, ForbiddenTimeConstraint<Group>> _groupForbiddenTimeConstraints = new ConcurrentHashMap<>();
    private final Map<Group, MandatoryTimeConstraint> _groupMandatoryTimeConstraints = new ConcurrentHashMap<>();
    private final Map<Group, Integer> _groupMaxDaysConstraint = new ConcurrentHashMap<>();
    private final Map<Group, Map<DayOfWeek, Integer>> _groupDailyMaxLoad = new ConcurrentHashMap<>();
    private final Map<Teacher, Map<DayOfWeek, Integer>> _teacherDailyMaxLoad = new ConcurrentHashMap<>();


    @Override
    public Optional<MandatoryTimeConstraint> getMandatorySlotsForGroup(Group group) {
        MandatoryTimeConstraint constraint = _groupMandatoryTimeConstraints.get(group);
        return constraint == null ? Optional.empty() : Optional.of(constraint);
    }

    @Override
    public void addGroupDailyMaxLoad(Group group, Stream<Pair<DayOfWeek, Integer>> stream) {
        //TODO WE CAN refactor the code by creating of typical Pair stream Collector
        _groupDailyMaxLoad.put(group, stream.collect(Collectors.toConcurrentMap(Pair::getKey, Pair::getValue)));
    }

    @Override
    public void addTeacherDailyMaxLoad(Teacher teacher, Stream<Pair<DayOfWeek, Integer>> stream) {
        _teacherDailyMaxLoad.put(teacher, stream.collect(Collectors.toConcurrentMap(Pair::getKey, Pair::getValue)));
    }
    @Override
    public void addTeacherMaxDaysConstraint(Teacher teacher, int maxDays) {
        _teacherMaxDaysConstraint.put(teacher, maxDays);
    }

    @Override
    public void addGroupMaxDaysConstraint(Group group, int maxDays) {
        _groupMaxDaysConstraint.put(group, maxDays);
    }

    @Override
    public void addTeacherForbiddenTimeslotsConstraint(ForbiddenTimeConstraint<Teacher> constraint) {
        _teacherForbiddenTimeConstraints.put(constraint.getRelatedObject(), constraint);
    }

    @Override
    public void addGroupForbiddenTimeslotsConstraint(ForbiddenTimeConstraint<Group> constraint) {
        _groupForbiddenTimeConstraints.put(constraint.getRelatedObject(), constraint);
    }

    @Override
    public Optional<ForbiddenTimeConstraint<Teacher>> getForbiddenSlotsForTeacher(Teacher teacher) {
        ForbiddenTimeConstraint<Teacher> result = _teacherForbiddenTimeConstraints.get(teacher);
        return result == null ? Optional.empty() : Optional.of(result);
    }

    @Override
    public Optional<ForbiddenTimeConstraint<Group>> getForbiddenSlotsForGroup(Group group) {
        ForbiddenTimeConstraint<Group> result = _groupForbiddenTimeConstraints.get(group);
        return result == null ? Optional.empty() : Optional.of(result);
    }

    @Override
    public OptionalInt getNumDaysConstraintForTeacher(Teacher teacher) {
        Integer result = _teacherMaxDaysConstraint.get(teacher);
        return result == null ? OptionalInt.empty() : OptionalInt.of(result);
    }

    @Override
    public OptionalInt getNumDaysConstraintForGroup(Group group) {
        Integer result = _groupMaxDaysConstraint.get(group);
        return result == null ? OptionalInt.empty() : OptionalInt.of(result);
    }

    @Override
    public Stream<Pair<DayOfWeek, Integer>> getDailyMaxLoadsForGroup(Group group) {
        return _groupDailyMaxLoad.get(group).entrySet()
                .stream()
                .map(Pair::new);
    }

    @Override
    public Stream<Pair<DayOfWeek, Integer>> getDailyMaxLoadsForTeacher(Teacher teacher) {
        return _teacherDailyMaxLoad.get(teacher).entrySet()
                .stream()
                .map(Pair::new);
    }

    @Override
    public void addGroupMandatoryTimeConstraint(Group group, MandatoryTimeConstraint constraint) {
        _groupMandatoryTimeConstraints.put(group, constraint);
    }
}
