package schedule;

import lesson.Lesson;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScheduleImpl implements Schedule {
    private final List<Lesson> _lessonList;

    public ScheduleImpl(Stream<Lesson> lessonStream) {
        _lessonList = lessonStream.toList();
    }

    @Override
    public Stream<Lesson> getAllLessons() {
        return _lessonList.stream();
    }
}
