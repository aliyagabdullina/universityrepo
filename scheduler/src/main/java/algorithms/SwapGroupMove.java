package algorithms;

import group.Group;
import lesson.Lesson;
import lesson.LessonImpl;
import schedule.Schedule;
import schedule.ScheduleImpl;

import java.util.ArrayList;
import java.util.List;

public class SwapGroupMove implements LSMove {
    private final int i;
    private final int j;

    public SwapGroupMove(int i, int j) {
        this.i = i;
        this.j = j;
    }

    @Override
    public Schedule makeMove(Schedule schedule) {
        List<Lesson> lessons = schedule.getAllLessons().toList();
        Lesson lesson1 = lessons.get(i);
        Lesson lesson2 = lessons.get(j);

        // Меняем группы местами
        Group tempGroup = lesson1.getGroup();

        Lesson new1 = new LessonImpl(lesson1.getTimeSlot(), lesson2.getGroup(), lesson1.getPlace(), lesson1.getTeachers().toList().get(0), lesson1.getCourse());
        Lesson new2 = new LessonImpl(lesson2.getTimeSlot(), lesson2.getGroup(), lesson1.getPlace(), lesson2.getTeachers().toList().get(0), lesson2.getCourse());

        // Меняем местами два урока
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
        SwapGroupMove move = (SwapGroupMove) obj;
        return i == move.i && j == move.j;
    }

    @Override
    public int hashCode() {
        return 31 * i + j;
    }
}
