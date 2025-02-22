package constraint;

import lesson.Lesson;

import java.util.stream.Stream;

public interface ScheduleConstraint {
    boolean ifConsistent(Stream<Lesson> schedule);
}
