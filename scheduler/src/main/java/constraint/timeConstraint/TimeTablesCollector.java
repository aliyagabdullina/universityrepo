package constraint.timeConstraint;

import group.Group;
import person.Teacher;
import place.Place;

import java.util.Map;

public interface TimeTablesCollector {
    // time tables
    Map<Teacher, TimeSlotStatus[][]> getTeacherTimeTable();
    Map<Group, TimeSlotStatus[][]> getGroupTimeTable();
    Map<Place, TimeSlotStatus[][]> getPlaceTimeTable();

    void setTeacherTimeTable(Teacher teacher, TimeSlotStatus[][] statuses);

    void setGroupTimeTable(Group group, TimeSlotStatus[][] statuses);
    void setPlaceTimeTable(Place place, TimeSlotStatus[][] statuses);

    Map<Teacher, Integer> getTeacherMaxDaysMap();
    void setTeacherNumWorkdaysUb(Teacher teacher, int maxWorkdays);
    Map<Teacher, Integer[]> getTeacherMaxDailyLoadMap();


    Map<Group, Integer[]> getGroupMaxDailyLoadMap();
    Map<Group, Integer> getGroupMaxDaysMap();

    void setTeacherMaxDailyLoad(Teacher teacher, Integer[] array);

    void setGroupNumWorkdaysUb(Group group, int maxDays);

    void setGroupMaxDailyLoad(Group group, Integer[] toArray);
}
