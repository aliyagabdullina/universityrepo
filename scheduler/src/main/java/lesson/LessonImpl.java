package lesson;

import course.Course;
import group.Group;
import person.Teacher;
import place.Place;
import time.WeeklyTimeSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LessonImpl implements Lesson {
    private final WeeklyTimeSlot _timeslot;
    private final Group _group;
    private final Place _place;
    private final List<Teacher> _teachers;
    private final Course _course;

    public LessonImpl(WeeklyTimeSlot timeSlot, Group group, Place place, Teacher teacher, Course course) {
        _timeslot = timeSlot;
        _group = group;
        _place = place;
        _teachers = new ArrayList<>();
        _teachers.add(teacher);
        _course = course;
    }

    @Override
    public WeeklyTimeSlot getTimeSlot() {
        return _timeslot;
    }

    @Override
    public Group getGroup() {
        return _group;
    }

    @Override
    public Place getPlace() {
        return _place;
    }

    @Override
    public Stream<Teacher> getTeachers() {
        return _teachers.stream();
    }

    @Override
    public Course getCourse() {
        return _course;
    }
}
