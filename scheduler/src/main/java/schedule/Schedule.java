package schedule;

import lesson.Lesson;

import java.util.stream.Stream;

public interface Schedule {
    Stream<Lesson> getAllLessons();
    static Schedule createEmpty() {
        return new ScheduleImpl(Stream.empty());
    }

    double evaluate();
}
