package constraint.timeConstraint;

import course.CourseImpl;
import group.Group;
import group.GroupImpl;
import lesson.Lesson;
import lesson.LessonImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import person.Student;
import person.StudentImpl;
import person.Teacher;
import person.TeacherImpl;
import place.AddressImpl;
import place.BuildingImpl;
import place.Place;
import place.PlaceImpl;
import time.TimeTranslator;
import time.WeeklyTimeSlot;
import time.WeeklyTimeSlotImpl;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestForbiddenTimeConstraintImpl {
    ForbiddenTimeConstraint<Teacher> teacherConstraint;
    ForbiddenTimeConstraint<Group> groupConstraint;
    ForbiddenTimeConstraint<Place> placeConstraint;

    Lesson lessonConsistent;
    Lesson lessonInconsistent;
    @BeforeEach
    void setUp() {


        var forbiddenStart = TimeTranslator.getMsTimeByHoursAndMins(10, 30);
        var forbiddenEnd = TimeTranslator.getMsTimeByHoursAndMins(15, 30);
        WeeklyTimeSlot inconsistentTimeSlot = new WeeklyTimeSlotImpl(DayOfWeek.THURSDAY, forbiddenStart, forbiddenEnd);

        List<WeeklyTimeSlot> forbiddenTimeIntervals = List.of(inconsistentTimeSlot);
        Teacher teacher = new TeacherImpl("Anna");
        teacherConstraint = new ForbiddenTimeConstraintImpl<>(teacher, forbiddenTimeIntervals);

        Set<Student> students = Set.of(new StudentImpl("Sergey"));
        Group group = new GroupImpl( "7-1", students);
        groupConstraint = new ForbiddenTimeConstraintImpl<>(group, forbiddenTimeIntervals);

        var address = new AddressImpl("Sobinova", "22a");
        var building = new BuildingImpl("School33", address);
        var place = new PlaceImpl("cabinet_0", building);
        placeConstraint = new ForbiddenTimeConstraintImpl<>(place, forbiddenTimeIntervals);
        lessonConsistent = createConsistentLesson();
        lessonInconsistent = createInconsistentLesson();
    }

    private Lesson createConsistentLesson() {
        var start = TimeTranslator.getMsTimeByHoursAndMins(9, 15);
        var end = TimeTranslator.getMsTimeByHoursAndMins(10, 30);
        var timeSlot = new WeeklyTimeSlotImpl(DayOfWeek.THURSDAY, start, end);
        Set<Student> students = Set.of(new StudentImpl("Sergey"));
        var group = new GroupImpl( "7-1", students);

        var address = new AddressImpl("Sobinova", "22a");
        var building = new BuildingImpl("School33", address);
        var place = new PlaceImpl("cabinet_0", building);

        var teacher = new TeacherImpl("Anna");
        var course = new CourseImpl("Mechanics-1");
        return new LessonImpl(timeSlot, group, place, teacher, course);
    }

    private Lesson createInconsistentLesson() {
        var start = TimeTranslator.getMsTimeByHoursAndMins(12, 0);
        var end = TimeTranslator.getMsTimeByHoursAndMins(13, 30);
        var timeSlot = new WeeklyTimeSlotImpl(DayOfWeek.THURSDAY, start, end);
        Set<Student> students = Set.of(new StudentImpl("Sergey"));
        var group = new GroupImpl("7-1", students);

        var address = new AddressImpl("Sobinova", "22a");
        var building = new BuildingImpl("School33", address);
        var place = new PlaceImpl("cabinet_0", building);

        var teacher = new TeacherImpl("Anna");
        var course = new CourseImpl("Mechanics-1");
        return new LessonImpl(timeSlot, group, place, teacher, course);
    }

    @Test
    void testGetRelatedObject() {
        assertNotNull(teacherConstraint.getRelatedObject());
        assertEquals("Anna", teacherConstraint.getRelatedObject().getName());
    }

    @Test
    void testForbiddenTimeSlots() {
        assertNotNull(teacherConstraint.forbiddenTimeSlots());
        assertTrue(teacherConstraint.forbiddenTimeSlots().count() > 0);
    }

    @Test
    void testTeacherConstraintIfConsistentTrue() {
        assertTrue(teacherConstraint.ifConsistent(Stream.of(lessonConsistent)));
    }

    @Test
    void testTeacherConstraintIfConsistentFalse() {
        assertFalse(teacherConstraint.ifConsistent(Stream.of(lessonInconsistent)));
    }

    @Test
    void testGroupConstraintIfConsistentTrue() {
        assertTrue(groupConstraint.ifConsistent(Stream.of(lessonConsistent)));
    }

    @Test
    void testGroupConstraintIfConsistentFalse() {
        assertFalse(groupConstraint.ifConsistent(Stream.of(lessonInconsistent)));
    }

    @Test
    void testPlaceConstraintIfConsistentTrue() {
        assertTrue(placeConstraint.ifConsistent(Stream.of(lessonConsistent)));
    }

    @Test
    void testPlaceConstraintIfConsistentFalse() {
        assertFalse(placeConstraint.ifConsistent(Stream.of(lessonInconsistent)));
    }

}