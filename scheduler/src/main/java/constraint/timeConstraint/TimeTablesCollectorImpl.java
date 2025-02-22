package constraint.timeConstraint;

import group.Group;
import person.Teacher;
import place.Place;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimeTablesCollectorImpl implements TimeTablesCollector {
    private final Map<Teacher, TimeSlotStatus[][]> _teacherTimeTable = new ConcurrentHashMap<>();
    private final Map<Group, TimeSlotStatus[][]> _groupTimeTable = new ConcurrentHashMap<>();
    private final Map<Place, TimeSlotStatus[][]> _placeTimeTable = new ConcurrentHashMap<>();

    private final Map<Teacher, Integer> _teacherMaxDaysMap = new ConcurrentHashMap<>();
    private final Map<Group, Integer> _groupMaxDaysMap = new ConcurrentHashMap<>();
    private final Map<Teacher, Integer[]> _teacherMaxDailyLoadMap = new ConcurrentHashMap<>();
    private final Map<Group, Integer[]> _groupMaxDailyLoadMap = new ConcurrentHashMap<>();

    @Override
    public Map<Teacher, TimeSlotStatus[][]> getTeacherTimeTable() {
        return _teacherTimeTable;
    }

    @Override
    public Map<Group, TimeSlotStatus[][]> getGroupTimeTable() {
        return _groupTimeTable;
    }

    @Override
    public Map<Place, TimeSlotStatus[][]> getPlaceTimeTable() {
        return _placeTimeTable;
    }

    @Override
    public void setTeacherTimeTable(Teacher teacher, TimeSlotStatus[][] statuses) {
        _teacherTimeTable.put(teacher,statuses);
    }


    @Override
    public void setTeacherNumWorkdaysUb(Teacher teacher, int maxWorkdays) {
        _teacherMaxDaysMap.put(teacher, maxWorkdays);
    }

    @Override
    public Map<Teacher, Integer[]> getTeacherMaxDailyLoadMap() {
        return _teacherMaxDailyLoadMap;
    }


    @Override
    public Map<Group, Integer[]> getGroupMaxDailyLoadMap() {
        return _groupMaxDailyLoadMap;
    }

    @Override
    public Map<Group, Integer> getGroupMaxDaysMap() {
        return _groupMaxDaysMap;
    }

    @Override
    public void setTeacherMaxDailyLoad(Teacher teacher, Integer[] array) {
        _teacherMaxDailyLoadMap.put(teacher, array);
    }

    @Override
    public void setGroupNumWorkdaysUb(Group group, int maxDays) {
        _groupMaxDaysMap.put(group, maxDays);
    }

    @Override
    public void setGroupMaxDailyLoad(Group group, Integer[] array) {
        _groupMaxDailyLoadMap.put(group, array);
    }

    @Override
    public void setPlaceTimeTable(Place place, TimeSlotStatus[][] statuses) {
        _placeTimeTable.put(place,statuses);
    }

    @Override
    public Map<Teacher, Integer> getTeacherMaxDaysMap() {
        return _teacherMaxDaysMap;
    }

    @Override
    public void setGroupTimeTable(Group group, TimeSlotStatus[][] statuses) {
        _groupTimeTable.put(group, statuses);
    }
}
