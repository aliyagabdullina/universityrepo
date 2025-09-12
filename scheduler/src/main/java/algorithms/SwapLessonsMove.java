package algorithms;

import lesson.Lesson;
import lesson.LessonImpl;
import schedule.Schedule;
import schedule.ScheduleImpl;
import time.WeeklyTimeSlot;

import java.util.ArrayList;
import java.util.List;

public class SwapLessonsMove implements LSMove {
    private final int i; // Индекс первого урока
    private final int j; // Индекс второго урока

    public SwapLessonsMove(int i, int j) {
        this.i = i;
        this.j = j;
    }

    @Override
    public Schedule makeMove(Schedule schedule) {
        List<Lesson> lessons = schedule.getAllLessons().toList();
        Lesson lesson1 = lessons.get(i);
        Lesson lesson2 = lessons.get(j);

        WeeklyTimeSlot ts1 = lesson1.getTimeSlot();
        WeeklyTimeSlot ts2 = lesson2.getTimeSlot();
        Lesson new1 = new LessonImpl(ts2, lesson1.getGroup(), lesson1.getPlace(), lesson1.getTeachers().toList().get(0), lesson1.getCourse());
        Lesson new2 = new LessonImpl(ts1, lesson2.getGroup(), lesson2.getPlace(), lesson2.getTeachers().toList().get(0), lesson2.getCourse());

        List<Lesson> newLessons = new ArrayList<>(lessons);

        // Меняем местами два урока
        newLessons.set(i, new2);
        newLessons.set(j, new1);

        // Обновляем расписание
        Schedule newSchedule = new ScheduleImpl(newLessons.stream());
        return newSchedule;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SwapLessonsMove move = (SwapLessonsMove) obj;
        return i == move.i && j == move.j;
    }

    @Override
    public int hashCode() {
        return 31 * i + j;
    }
}

