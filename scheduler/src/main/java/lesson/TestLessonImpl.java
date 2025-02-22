package lesson;

import course.CourseImpl;
import group.Group;
import group.GroupImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import person.TeacherImpl;
import place.AddressImpl;
import place.BuildingImpl;
import place.PlaceImpl;
import person.Student;
import person.StudentImpl;
import time.TimeTranslator;
import time.WeeklyTimeSlotImpl;

import java.time.DayOfWeek;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestLessonImpl {
    Lesson lesson;
    @BeforeEach
    void setUp() {
        var start = TimeTranslator.getMsTimeByHoursAndMins(9, 15);
        var end = TimeTranslator.getMsTimeByHoursAndMins(10, 30);
        var timeSlot = new WeeklyTimeSlotImpl(DayOfWeek.THURSDAY, start, end);
        Set<Student> students = Set.of(new StudentImpl("Sergey"));
        var group = new GroupImpl("7-1", students);

        var address = new AddressImpl("Sobinova", "22a");
        var building = new BuildingImpl("School33", address);
        var place = new PlaceImpl("cabinet_0", building);

        var teacher = new TeacherImpl("Anna");
        var course = new CourseImpl("Mechanics-1");
        lesson = new LessonImpl(timeSlot, group, place, teacher, course);
    }

    @Test
    void testGetTimeSlotNoException() {
        assertDoesNotThrow(lesson::getTimeSlot);
    }

    @Test
    void testGetTimeSlotNotNull() {
        assertNotNull(lesson.getTimeSlot());
    }

    @Test
    void testGetGroupNotNull() {
        assertNotNull(lesson.getGroup());
    }

    @Test
    void testGetGroup() {
        Set<Student> students = Set.of(new StudentImpl("Sergey"));
        Group group = new GroupImpl("7-1", students);
        assertEquals(group, lesson.getGroup());
    }

    @Test
    void testGetPlace() {
        assertNotNull(lesson.getPlace());
        assertEquals("School33", lesson.getPlace().getBuilding().getName());
    }

    @Test
    void testGetTeacher() {
        assertNotNull(lesson.getTeachers());
        assertEquals("Anna", lesson.getTeachers().findAny().get().getName());
    }

    @Test
    void testGetCourse() {
        assertNotNull(lesson.getCourse());
        //assertEquals("Mechanics-1", lesson.getCourse().getName());
    }
}