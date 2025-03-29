package algorithms;

import collector.SchoolDataCollector;
import group.Group;
import lesson.Lesson;
import lesson.LessonRequest;
import lesson.LessonRequestOccupation;
import lesson.LessonRequestOccupationImpl;
import person.Teacher;
import place.Place;
import schedule.Schedule;
import schedule.ScheduleImpl;
import scheduleBuilder.SchedulingInputData;
import scheduleBuilder.data.ScheduleConstraintsAccumulator;
import time.WeeklyTimeSlot;

import java.util.List;
import java.util.stream.Collectors;

public class GreedySchedulerImpl implements GreedyScheduler {
    private final SchoolDataCollector _dataCollector;
    private final List<WeeklyTimeSlot> _timeSlotSequence;
    private final SchedulingInputData _scheduleInputData;
    private final ScheduleConstraintsAccumulator _constraintAccumulator;


    public GreedySchedulerImpl(SchoolDataCollector dataCollector, List<WeeklyTimeSlot> timeSlotSequence, SchedulingInputData scheduleInputData, ScheduleConstraintsAccumulator constraintAccumulator) {
        _dataCollector = dataCollector;
        _timeSlotSequence = timeSlotSequence;
        _scheduleInputData = scheduleInputData;
        _constraintAccumulator = constraintAccumulator;
    }


    @Override
    public Schedule generateSchedule() {
        List<LessonRequest> unscheduledRequests = _scheduleInputData.getLessonRequests()
                .filter(lessonRequest -> !isScheduled(lessonRequest, _scheduleInputData))
                .toList();

        for (LessonRequest lessonRequest : unscheduledRequests) {
            assignLesson(lessonRequest, _scheduleInputData, schedule);
        }
        Schedule schedule = new ScheduleImpl(lessonStream);

        return schedule;

    }

    private boolean isScheduled(LessonRequest lessonRequest, SchedulingInputData schedulingInputData) {
        return schedulingInputData.getScheduledLessons()
                .anyMatch(lesson -> lesson.getRequest().equals(lessonRequest));
    }

    private void assignLesson(LessonRequest lessonRequest, SchedulingInputData schedulingInputData, Schedule schedule) {
        // Для урока находим доступные слоты
        List<WeeklyTimeSlot> availableSlots = getAvailableSlotsForLesson(lessonRequest, schedulingInputData);

        for (WeeklyTimeSlot slot : availableSlots) {
            if (canAssignLesson(lessonRequest, schedulingInputData, slot)) {
                // Если слот подходит, создаем и добавляем урок в расписание
                LessonRequestOccupation occupation = new LessonRequestOccupationImpl(lessonRequest, slot);
                Lesson lesson = createLesson(occupation);
                schedule.addLesson(lesson);

                // Обновляем занятые слоты
                updateOccupiedSlots(lesson, schedulingInputData);
                break; // После назначения урока переходим к следующему запросу
            }
        }
    }

    private List<WeeklyTimeSlot> getAvailableSlotsForLesson(LessonRequest lessonRequest, SchedulingInputData schedulingInputData) {
        // Возвращаем все доступные временные слоты для урока
        return schedulingInputData.getAvailableSlotsForLessonRequest(lessonRequest)
                .collect(Collectors.toList());
    }

    private boolean canAssignLesson(LessonRequest lessonRequest, SchedulingInputData schedulingInputData, WeeklyTimeSlot slot) {
        // Проверка на доступность преподавателя, группы и места для выбранного слота
        Teacher teacher = lessonRequest.getTeacher();
        Group group = lessonRequest.getGroup();
        Place place = lessonRequest.getPlace();

        return !isSlotOccupiedByTeacher(schedulingInputData, teacher, slot) &&
                !isSlotOccupiedByGroup(schedulingInputData, group, slot) &&
                !isSlotOccupiedByPlace(schedulingInputData, place, slot);
    }

    private boolean isSlotOccupiedByTeacher(SchedulingInputData schedulingInputData, Teacher teacher, WeeklyTimeSlot slot) {
        return schedulingInputData.getOccupiedTimeSlotsForTeacher(teacher)
                .anyMatch(occupiedSlot -> occupiedSlot.ifIntersects(slot));
    }

    private boolean isSlotOccupiedByGroup(SchedulingInputData schedulingInputData, Group group, WeeklyTimeSlot slot) {
        return schedulingInputData.getOccupiedTimeSlotsForGroup(group)
                .anyMatch(occupiedSlot -> occupiedSlot.ifIntersects(slot));
    }

    private boolean isSlotOccupiedByPlace(SchedulingInputData schedulingInputData, Place place, WeeklyTimeSlot slot) {
        return schedulingInputData.getOccupiedTimeSlotsForPlace(place)
                .anyMatch(occupiedSlot -> occupiedSlot.ifIntersects(slot));
    }

    private Lesson createLesson(LessonRequestOccupation occupation) {
        // Здесь создаем новый объект Lesson с использованием данных из occupation
        return new LessonImpl(
                occupation.getTimeSlot(),
                occupation.getGroup(),
                occupation.getPlace(),
                occupation.getTeacher(),
                occupation.getCourse()
        );
    }

    private void updateOccupiedSlots(Lesson lesson, SchedulingInputData schedulingInputData) {
        // Обновляем информацию о занятых слотах для преподавателя, группы и места
        schedulingInputData.scheduleLesson(lesson);
    }

}
