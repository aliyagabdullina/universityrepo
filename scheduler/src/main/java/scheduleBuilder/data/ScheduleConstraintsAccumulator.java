package scheduleBuilder.data;

import constraint.ScheduleConstraint;
import constraint.timeConstraint.ForbiddenTimeConstraint;
import constraint.timeConstraint.MandatoryTimeConstraint;
import group.Group;
import pair.Pair;
import person.Teacher;

import java.time.DayOfWeek;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Stream;

public interface ScheduleConstraintsAccumulator {

    void addGroupDailyMaxLoad(Group group, Stream<Pair<DayOfWeek, Integer>> stream);

    void addTeacherDailyMaxLoad(Teacher teacher, Stream<Pair<DayOfWeek, Integer>> stream);

    void addTeacherMaxDaysConstraint(Teacher teacher, int maxDays);

    void addGroupMaxDaysConstraint(Group group, int maxDays);

    void addTeacherForbiddenTimeslotsConstraint(ForbiddenTimeConstraint<Teacher> constraint);

    void addGroupForbiddenTimeslotsConstraint(ForbiddenTimeConstraint<Group> constraint);

    Optional<ForbiddenTimeConstraint<Teacher>> getForbiddenSlotsForTeacher(Teacher teacher);
    Optional<ForbiddenTimeConstraint<Group>> getForbiddenSlotsForGroup(Group group);
    Optional<MandatoryTimeConstraint> getMandatorySlotsForGroup(Group group);
    OptionalInt getNumDaysConstraintForTeacher(Teacher teacher);
    OptionalInt getNumDaysConstraintForGroup(Group group);
    Stream<Pair<DayOfWeek, Integer>> getDailyMaxLoadsForGroup(Group group);
    Stream<Pair<DayOfWeek, Integer>> getDailyMaxLoadsForTeacher(Teacher t);

    void addGroupMandatoryTimeConstraint(Group group, MandatoryTimeConstraint constraint);
}
