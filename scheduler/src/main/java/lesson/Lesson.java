package lesson;

import course.Course;
import group.Group;
import person.Teacher;
import place.Place;
import time.WeeklyTimeSlot;

import java.util.stream.Stream;


public interface Lesson {
    WeeklyTimeSlot getTimeSlot();
    Group getGroup();
    Place getPlace();
    Stream<Teacher> getTeachers();
    Course getCourse();

    boolean conflictsWith(Lesson lesson2);
}
