package scheduleBuilder.occupationSynchonizer;

import lesson.LessonRequest;
import lesson.LessonRequestOccupation;
import time.WeeklyTimeSlot;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface OccupationSynchronizer {
    void addLessonRequestSynchronization(LessonRequestOccupation occupation, WeeklyTimeSlot timeSlot);
    Optional<Stream<WeeklyTimeSlot>> getAvailableTimeSlotsIfImpliedBySynchronization(LessonRequestOccupation lessonRequestOccupation);
}
