package algorithms;

import lesson.Lesson;
import person.Teacher;
import schedule.Schedule;
import schedule.ScheduleImpl;

import java.util.List;

public class SwapTeacherMove implements LSMove {
    private final int i;
    private final int j;

    public SwapTeacherMove(int i, int j) {
        this.i = i;
        this.j = j;
    }

    @Override
    public Schedule makeMove(Schedule schedule) {
        List<Lesson> lessons = schedule.getAllLessons().toList();
        Lesson lesson1 = lessons.get(i);
        Lesson lesson2 = lessons.get(j);

        // Меняем преподавателей местами
        Teacher tempTeacher = lesson1.getTeachers().findFirst().orElse(null);
        lesson1.getTeachers().forEach(teacher -> teacher = lesson2.getTeachers().findFirst().orElse(null));
        lesson2.getTeachers().forEach(teacher -> teacher = tempTeacher);

        // Обновляем расписание
        Schedule newSchedule = new ScheduleImpl(lessons.stream());
        return newSchedule;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SwapTeacherMove move = (SwapTeacherMove) obj;
        return i == move.i && j == move.j;
    }

    @Override
    public int hashCode() {
        return 31 * i + j;
    }
}
