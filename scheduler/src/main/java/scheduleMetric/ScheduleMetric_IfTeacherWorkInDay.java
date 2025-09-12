package scheduleMetric;

import lesson.Lesson;
import person.Teacher;

import java.time.DayOfWeek;
import java.util.stream.Stream;

public class ScheduleMetric_IfTeacherWorkInDay implements ScheduleMetric {
    private final Teacher _teacher;
    private final DayOfWeek _dayOfWeek;

    public ScheduleMetric_IfTeacherWorkInDay(Teacher teacher, DayOfWeek dayOfWeek) {
        this._teacher = teacher;
        this._dayOfWeek = dayOfWeek;
    }

    @Override
    public int measure(Stream<Lesson> schedule) {
        return (int)schedule
                .filter(lesson -> lesson.getTeachers().anyMatch(t -> t.equals(_teacher)))
                .filter(lesson -> lesson.getTimeSlot().getDayOfWeek().equals(_dayOfWeek))
                .count();
    }
}
