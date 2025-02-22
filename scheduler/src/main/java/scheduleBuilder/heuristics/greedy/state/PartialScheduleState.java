package scheduleBuilder.heuristics.greedy.state;

import lesson.Lesson;

public interface PartialScheduleState {
    void addLesson(Lesson lesson);
}
