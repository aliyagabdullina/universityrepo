package scheduleBuilder;

import lesson.DtoLesson;
import time.DtoWeeklyTimeSlot;

import java.util.ArrayList;
import java.util.List;

public class DtoScheduleSetupActions {
    public List<DtoLesson> scheduledLessons = new ArrayList<>();
    public List<Integer> forbiddenLessonRequestIds = new ArrayList<>();
    public List<DtoWeeklyTimeSlot> forbiddenLessonRequestTimeSlots = new ArrayList<>();
}
