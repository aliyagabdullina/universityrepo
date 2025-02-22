package scheduleServer;

import group.Group;
import person.Teacher;
import place.Place;
import schedule.Schedule;

public interface ScheduleServer {
    void readSchedule(String label, Schedule schedule);
    void selectSchedule(String label);
    Schedule getTeacherSchedule(Teacher teacher);
    Schedule getGroupSchedule(Group group);
    Schedule getPlaceSchedule(Place place);
}
