package scheduleServer;

import group.Group;
import person.Teacher;
import place.Place;
import schedule.Schedule;
import schedule.ScheduleImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ScheduleServerImpl implements ScheduleServer {
    private Map<String, Schedule> _schedulesMap = new ConcurrentHashMap<>();
    private Schedule _currentSchedule = Schedule.createEmpty();
    @Override
    public void readSchedule(String label, Schedule schedule) {
        _schedulesMap.put(label, schedule);
    }

    @Override
    public void selectSchedule(String label) {
        if(!_schedulesMap.containsKey(label)) {
            throw new IllegalStateException("Schedule " + label + " is not presented");
        }
        _currentSchedule = _schedulesMap.get(label);
    }

    @Override
    public Schedule getTeacherSchedule(Teacher teacher) {
        var lessonsStream = _currentSchedule.getAllLessons()
                .filter(lesson -> lesson.getTeachers().anyMatch(teacher::equals));
        return new ScheduleImpl(lessonsStream);
    }

    @Override
    public Schedule getGroupSchedule(Group group) {
        var lessonsStream = _currentSchedule.getAllLessons()
                .filter(lesson -> lesson.getGroup().equals(group));
        return new ScheduleImpl(lessonsStream);
    }

    @Override
    public Schedule getPlaceSchedule(Place place) {
        var lessonsStream = _currentSchedule.getAllLessons()
                .filter(lesson -> lesson.getPlace().equals(place));
        return new ScheduleImpl(lessonsStream);
    }
}
