package algorithms;

import group.Group;
import lesson.*;
import person.Teacher;
import place.Place;
import schedule.Schedule;
import schedule.ScheduleImpl;
import scheduleBuilder.SchedulingInputData;
import time.WeeklyTimeSlot;
import pair.Pair;

import java.time.DayOfWeek;
import java.util.*;

public class GreedySchedulerImpl implements GreedyScheduler {
    private final SchedulingInputData _scheduleInputData;


    public GreedySchedulerImpl(SchedulingInputData scheduleInputData) {
        _scheduleInputData = scheduleInputData;
    }


    @Override
    public Schedule generateSchedule() {
        List<Lesson> scheduledLessons = new ArrayList<>();

// Получаем все незапланированные занятия (для учителей и групп)
        Set<LessonRequestOccupation> unscheduledOccupations = new HashSet<>();
        _scheduleInputData.getTeachers().forEach(teacher ->
                _scheduleInputData.getUnscheduledRequestsForTeacher(teacher)
                        .map(Pair::getKey)
                        .forEach(unscheduledOccupations::add)
        );
        _scheduleInputData.getGroups().forEach(group ->
                _scheduleInputData.getUnscheduledRequestsForGroup(group)
                        .map(Pair::getKey)
                        .forEach(unscheduledOccupations::add)
        );

// Перебираем все незапланированные занятия
        for (LessonRequestOccupation occupation : unscheduledOccupations) {
            Set<WeeklyTimeSlot> availableSlots = _scheduleInputData.getLessonRequestTimeSlotsMap()
                    .getOrDefault(occupation.getLessonRequest(), Set.of());

            for (WeeklyTimeSlot timeSlot : availableSlots) {
                Optional<Teacher> availableTeacher = occupation.getOccupiedTeachers()
                        .filter(t -> _scheduleInputData.getOccupiedTimeSlotsForTeacher(t).noneMatch(tSlot -> tSlot.ifIntersects(timeSlot)))
                        .findFirst();

                if (availableTeacher.isEmpty()) continue;
                Teacher teacher = availableTeacher.get();

                Group group = occupation.getLessonRequest().getGroup();
                if (_scheduleInputData.getOccupiedTimeSlotsForGroup(group).anyMatch(t -> t.ifIntersects(timeSlot))) {
                    continue;
                }

                Map<Teacher, Integer> teacherMaxDaysMap = _scheduleInputData.getTeacherMaxDaysMap();
                Map<Group, Map<DayOfWeek, Integer>> groupMaxLoadMap = _scheduleInputData.getGroupDailyMaxLoadMap();

                DayOfWeek day = timeSlot.getDayOfWeek();
                if (teacherMaxDaysMap.getOrDefault(teacher, Integer.MAX_VALUE) <= 0) continue;
                if (groupMaxLoadMap.getOrDefault(group, Map.of()).getOrDefault(day, Integer.MAX_VALUE) <= 0) continue;

                Optional<Place> availablePlace = _scheduleInputData.getAvailablePlacesForOccupationInTimeSlot(occupation, timeSlot).findFirst();
                if (availablePlace.isEmpty()) continue;
                Place place = availablePlace.get();

                // Создаем урок и добавляем его в расписание
                Lesson lesson = new LessonImpl(timeSlot, group, place, teacher, occupation.getCourse());
                scheduledLessons.add(lesson);
                _scheduleInputData.scheduleLesson(lesson);

                // Обновляем лимиты
                teacherMaxDaysMap.put(teacher, teacherMaxDaysMap.getOrDefault(teacher, Integer.MAX_VALUE) - 1);
                groupMaxLoadMap.computeIfPresent(group, (g, loadMap) -> {
                    loadMap.put(day, loadMap.getOrDefault(day, Integer.MAX_VALUE) - 1);
                    return loadMap;
                });

                break; // Запланировали занятие, идем к следующему
            }
        }

        Schedule schedule = new ScheduleImpl(scheduledLessons.stream());
        return schedule;

    }
}