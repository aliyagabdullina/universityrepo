package scheduleBuilder.actions;

import lesson.Lesson;
import lesson.LessonRequest;
import pair.Pair;
import place.Place;
import time.WeeklyTimeSlot;

import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface SchedulingSetupActions {
    void addScheduleLesson(Lesson lesson);
    void addForbidLessonRequestTimeSlot(LessonRequest lessonRequest, WeeklyTimeSlot timeSlot);

    void unscheduleIf(Predicate<Lesson> lessonPredicate);

    void undoLastAction();

    Stream<Lesson> getScheduledLessons();
    Stream<Pair<LessonRequest, WeeklyTimeSlot>> getForbiddenLessonRequestsTimeSlots();

}
