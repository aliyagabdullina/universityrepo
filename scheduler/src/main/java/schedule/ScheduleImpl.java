package schedule;

import lesson.Lesson;
import person.Teacher;
import time.WeeklyTimeSlot;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
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

    @Override
    public double evaluate() {
        int conflicts = 0;
        int preferences = 0;
        for (int i = 0; i < _lessonList.size(); i++) {
            Lesson lesson1 = _lessonList.get(i);
            for (int j = i + 1; j < _lessonList.size(); j++) {
                Lesson lesson2 = _lessonList.get(j);
                if (lesson1.conflictsWith(lesson2)) {
                    conflicts++;
                }
            }
        }

        // учителя хотят, чтобы их уроки шли подряд
        for (Teacher teacher : getAllTeachers()) {
            List<Lesson> teacherLessons = getTeacherLessons(teacher);
            teacherLessons.sort(Comparator.comparing(Lesson::getTimeSlot));

            // Длину рабочего дня добавляем штраф
            preferences += timeMeasure(teacherLessons.get(teacherLessons.size()-1).getTimeSlot(), teacherLessons.get(0).getTimeSlot());
        }
        return 3*conflicts + preferences;
    }
    private List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        for (Lesson lesson : _lessonList) {
            lesson.getTeachers().forEach(teachers::add);
        }
        return teachers;
    }
    private List<Lesson> getTeacherLessons(Teacher teacher) {
        List<Lesson> teacherLessons = new ArrayList<>();
        for (Lesson lesson : _lessonList) {
            lesson.getTeachers().forEach(t -> {
                if (t.equals(teacher)) {
                    teacherLessons.add(lesson);
                }
            });
        }
        return teacherLessons;
    }
    public int timeMeasure(WeeklyTimeSlot t1, WeeklyTimeSlot t2) {
        return t2.getDayOfWeek() == t1.getDayOfWeek() ? (int) Math.abs((t1.getEndTimeInMs() - t2.getStartTimeInMs())) : 0;
    }

}
