package schedule;

import course.Course;
import course.CourseImpl;
import group.Group;
import group.GroupImpl;
import lesson.Lesson;
import lesson.LessonImpl;
import person.Student;
import person.StudentImpl;
import person.Teacher;
import person.TeacherImpl;
import place.*;
import time.TimeTranslator;
import time.WeeklyTimeSlot;
import time.WeeklyTimeSlotImpl;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DummyScheduleCreator {
    private static int _studentsCounter = 0;

    public static Schedule createOneGroupSchedule() {
        Map<String, Place> places = createPlaces(2);
        Map<String, Group> groups = createGroups(1);
        Map<String, Teacher> teachers = createTeachers(2);
        Map<String, Course> courses = createCourses(2);

        Group group = groups.get("0");
        //Time slot 1
        var start1 = TimeTranslator.getMsTimeByHoursAndMins(8, 30);
        var end1 = TimeTranslator.getMsTimeByHoursAndMins(9, 15);
        WeeklyTimeSlot timeSlot1 = new WeeklyTimeSlotImpl(DayOfWeek.MONDAY, start1, end1);
        Lesson lesson1 = new LessonImpl(timeSlot1, group, places.get("0"),teachers.get("teacher_0"), courses.get("course_0"));

        //Time slot 2
        var start2 = TimeTranslator.getMsTimeByHoursAndMins(9, 20);
        var end2 = TimeTranslator.getMsTimeByHoursAndMins(10, 5);
        WeeklyTimeSlot timeSlot2 = new WeeklyTimeSlotImpl(DayOfWeek.MONDAY, start2, end2);

        Lesson lesson2 = new LessonImpl(timeSlot2, group, places.get("1"),teachers.get("teacher_1"), courses.get("course_1"));

        Schedule schedule = new ScheduleImpl(Stream.of(lesson1, lesson2));
        return schedule;
    }

    private static Map<String, Course> createCourses(int numCourses) {
        return IntStream.range(0, numCourses)
                .mapToObj(id -> new CourseImpl("course_" + id))
                .collect(Collectors.toConcurrentMap(Course::getName, course -> course));
    }

    private static Map<String, Place>  createPlaces(int numPlaces) {
        Address address = new AddressImpl("Sobinova", "22a");
        Building building = new BuildingImpl("School33", address);
        return IntStream.range(0, numPlaces)
                .mapToObj(id -> new PlaceImpl("" + id, building))
                .collect(Collectors.toConcurrentMap(Place::getName, place -> place));
    }

    private static Map<String, Group> createGroups(int numGroups) {
        return IntStream.range(0, numGroups)
                .mapToObj(id -> createGroup(id, 3))
                .collect(Collectors.toConcurrentMap(Group::getName, group -> group));
    }

    private static Group createGroup(int id, int numStudents) {
        Set<Student> studentsSet = new HashSet<>();
        for(int i=0; i < numStudents; i++) {
            Student student = new StudentImpl("student_" + _studentsCounter);
            studentsSet.add(student);
            _studentsCounter++;
        }
        return new GroupImpl(id + "A", studentsSet);
    }

    private static Map<String, Teacher> createTeachers(int numTeachers) {
        return IntStream.range(0, numTeachers)
                .mapToObj(id -> new TeacherImpl("teacher_" + id))
                .collect(Collectors.toConcurrentMap(Teacher::getName, teacher -> teacher));
    }


}
