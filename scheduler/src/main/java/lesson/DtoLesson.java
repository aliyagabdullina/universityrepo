package lesson;

import course.Course;
import group.Group;
import person.Teacher;
import place.Place;
import time.DtoWeeklyTimeSlot;
import time.WeeklyTimeSlot;

import java.time.DayOfWeek;
import java.util.List;

public class DtoLesson {
    public String group;
    public String course;
    public String teacher;
    public String place;
    public DtoWeeklyTimeSlot timeSlot;
}
