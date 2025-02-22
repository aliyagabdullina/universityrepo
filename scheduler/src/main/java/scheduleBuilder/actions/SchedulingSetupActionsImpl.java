package scheduleBuilder.actions;

import lesson.Lesson;
import lesson.LessonRequest;
import pair.Pair;
import time.WeeklyTimeSlot;

import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SchedulingSetupActionsImpl implements SchedulingSetupActions {
    private final Stack<Lesson> _lessons = new Stack<>();
    private final Stack<Pair<LessonRequest, WeeklyTimeSlot>> _forbiddenRequestSlot = new Stack<>();
    private final Stack<SchedulingActionType> _actionsSequence = new Stack<>();

    @Override
    public void addScheduleLesson(Lesson lesson) {
        _lessons.push(lesson);
        _actionsSequence.push(SchedulingActionType.SCHEDULE_LESSON);
    }

    @Override
    public void addForbidLessonRequestTimeSlot(LessonRequest lessonRequest, WeeklyTimeSlot timeSlot) {
        _forbiddenRequestSlot.push(new Pair<>(lessonRequest, timeSlot));
        _actionsSequence.push(SchedulingActionType.FORBID_LESSON_REQUEST_TIMESLOT);
    }


    @Override
    public Stream<Lesson> getScheduledLessons() {
        return _lessons.stream();
    }

    @Override
    public Stream<Pair<LessonRequest, WeeklyTimeSlot>> getForbiddenLessonRequestsTimeSlots() {
        return _forbiddenRequestSlot.stream();
    }

    @Override
    public void unscheduleIf(Predicate<Lesson> lessonPredicate) {
        int before = _lessons.size();
        _lessons.removeIf(lessonPredicate);
        if(_lessons.size() < before) {
            _actionsSequence.clear();
        }
    }

    @Override
    public void undoLastAction() {
        if(!_actionsSequence.empty()) {
            SchedulingActionType recentActionType = _actionsSequence.pop();
            switch (recentActionType) {
                case FORBID_LESSON_REQUEST_TIMESLOT -> _forbiddenRequestSlot.pop();
                case SCHEDULE_LESSON -> _lessons.pop();
            }
        }
    }
}
