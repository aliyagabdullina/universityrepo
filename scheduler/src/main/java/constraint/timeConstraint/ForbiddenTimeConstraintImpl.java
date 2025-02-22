package constraint.timeConstraint;

import group.Group;
import lesson.Lesson;
import person.Teacher;
import place.Place;
import time.WeeklyTimeSlot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ForbiddenTimeConstraintImpl<T> implements ForbiddenTimeConstraint<T> {
    private final T _relatedObject;
    private final Set<WeeklyTimeSlot> _forbiddenTimeIntervals;
    public ForbiddenTimeConstraintImpl(T relatedObject, List<WeeklyTimeSlot> forbiddenTimeIntervals) {
        _relatedObject = relatedObject;
        _forbiddenTimeIntervals = new HashSet<>(forbiddenTimeIntervals);
    }

    @Override
    public boolean isNotForbidden(WeeklyTimeSlot timeSlot) {
        return !_forbiddenTimeIntervals.contains(timeSlot);
    }

    @Override
    public T getRelatedObject() {
        return _relatedObject;
    }

    @Override
    public Stream<WeeklyTimeSlot> forbiddenTimeSlots() {
        return _forbiddenTimeIntervals.stream();
    }

    @Override
    public boolean ifConsistent(Stream<Lesson> schedule) {
        if(_relatedObject instanceof Teacher) {
            return ifLessonsConsistentTeacher(schedule);
        } else if (_relatedObject instanceof Group) {
            return ifLessonsConsistentGroup(schedule);
        } else if(_relatedObject instanceof Place) {
            return ifLessonsConsistentPlace(schedule);
        }
        throw new IllegalStateException();
    }

    private boolean ifLessonsConsistentPlace(Stream<Lesson> schedule) {
        return ifLessonsConsistent(schedule, lesson -> lesson.getPlace().equals(_relatedObject));
    }

    private boolean ifLessonsConsistentGroup(Stream<Lesson> schedule) {
        return ifLessonsConsistent(schedule, lesson -> lesson.getGroup().equals(_relatedObject));
    }

    private boolean ifLessonsConsistentTeacher(Stream<Lesson> schedule) {
        return ifLessonsConsistent(schedule, lesson -> lesson.getTeachers().equals(_relatedObject));
    }

    private boolean ifLessonsConsistent(Stream<Lesson> schedule, Predicate<Lesson> filterToCheck) {
        return schedule
                .filter(filterToCheck)
                .map(Lesson::getTimeSlot)
                .allMatch(weeklyTimeSlot ->
                        _forbiddenTimeIntervals
                                .stream()
                                .noneMatch(weeklyTimeSlot::ifIntersects)
                );
    }
}
