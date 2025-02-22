package scheduleMetric;

import lesson.Lesson;
import schedule.Schedule;

import java.util.stream.Stream;

public interface ScheduleMetric {
    int measure(Stream<Lesson> schedule);
}
