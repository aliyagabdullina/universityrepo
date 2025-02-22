package scheduleBuilder.occupationSynchonizer;

import lesson.LessonRequest;
import lesson.LessonRequestOccupation;
import time.WeeklyTimeSlot;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class OccupationSynchronizerImpl implements OccupationSynchronizer {
    private final Map<LessonRequest, Set<WeeklyTimeSlot>> _lessonRequestOccupiedTimeSlots = new ConcurrentHashMap<>();

    @Override
    public void addLessonRequestSynchronization(LessonRequestOccupation occupation, WeeklyTimeSlot timeSlot) {
        LessonRequest lessonRequest = occupation.getLessonRequest();
        if(!_lessonRequestOccupiedTimeSlots.containsKey(lessonRequest)) {
           _lessonRequestOccupiedTimeSlots.put(lessonRequest, new HashSet<>());
        }
        Set<WeeklyTimeSlot> requestSet = _lessonRequestOccupiedTimeSlots.get(lessonRequest);
        requestSet.add(timeSlot);
        if(requestSet.size() > lessonRequest.getCourseInProgram().getLessonsPerWeek()) {
            throw new IllegalStateException("Too many lessons scheduled for single lesson request");
        }
    }

    @Override
    public Optional<Stream<WeeklyTimeSlot>> getAvailableTimeSlotsIfImpliedBySynchronization(LessonRequestOccupation occupation) {
        LessonRequest lessonRequest = occupation.getLessonRequest();
        Set<WeeklyTimeSlot> timeSlots = _lessonRequestOccupiedTimeSlots.get(lessonRequest);
        if(timeSlots.size() == lessonRequest.getCourseInProgram().getLessonsPerWeek()) {
            return Optional.of(timeSlots.stream());
        } else {
            return Optional.empty();
        }
    }
}
