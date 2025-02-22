package objectFactories;

import lesson.DtoLessonsRequest;
import lesson.LessonRequest;

public interface SchedulerDtoFactory {
    DtoLessonsRequest createLessonRequestDto(LessonRequest lessonRequest);
}
