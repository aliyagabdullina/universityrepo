package constraint.assignment;

import course.*;
import group.Group;
import group.GroupImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import person.Teacher;
import person.TeacherImpl;

import java.util.HashSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestAssignmentCollectorImpl {
    AssignmentCollector assignmentCollector;
    @BeforeEach
    void setUp() {
        assignmentCollector = new AssignmentCollectorImpl();
    }

    @Test
    void testGetAvailableTeachersForCourse() {
        assertEquals(0, assignmentCollector.getAvailableTeachersForCourse(new CourseImpl("A")).count());
        Teacher teacher = new TeacherImpl("Anna");
        Stream<Course> courseStream  = Stream.of("A", "B", "C")
                        .map(CourseImpl::new);

        courseStream.forEach(course ->  assignmentCollector.setAvailableTeachersForCourse(course, Stream.of(teacher)));
        assertEquals(1, assignmentCollector.getAvailableTeachersForCourse(new CourseImpl("A")).count());
    }

    @Test
    void testGetAvailableCoursesForGroup() {
        Group group = new GroupImpl("A", new HashSet<>());
        assertEquals(0, assignmentCollector.getCourseProgramForGroup(group).getCoursesInProgram().count());
        CourseProgram courseProgram = new CourseProgramImpl("myCourseProgram");
        Stream.of("A", "B", "C")
                .map(CourseImpl::new)
                .map(course -> new CourseInProgramImpl(Stream.of(course)))
                .forEach(courseProgram::addCourse);
        assignmentCollector.setCourseProgramForGroup(group, courseProgram);
        assertEquals(3, assignmentCollector.getCourseProgramForGroup(group).getCoursesInProgram().count());
    }

    @Test
    void testGetAssignmentStatus() {
        Group group = new GroupImpl("A", new HashSet<>());
        Course course = new CourseImpl("A");
        Teacher teacher = new TeacherImpl("Anna");
        var defaultStatus = assignmentCollector.getGroupCourseTeacherAssignmentStatus(group, course, teacher);
        assertEquals(AssignmentStatus.UNDEFINED, defaultStatus);
        assignmentCollector.setAssignmentStatus(group, course, teacher, AssignmentStatus.ASSIGNED);
        var currentStatus = assignmentCollector.getGroupCourseTeacherAssignmentStatus(group, course, teacher);
        assertEquals(AssignmentStatus.ASSIGNED, currentStatus);
    }

    @Test
    void getCoursePlaceAssignmentStatus() {
    }

    @Test
    void getGroupPlaceAssignmentStatus() {
    }

    @Test
    void getTeacherPlaceAssignmentStatus() {
    }
}