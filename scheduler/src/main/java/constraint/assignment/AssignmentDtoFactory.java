package constraint.assignment;

import constraint.assignment.courseTeachers.DtoCourseTeachers;
import course.Course;
import person.Teacher;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public interface AssignmentDtoFactory {
    DtoCourseTeachers createDtoCourseTeachers(Course course, Stream<Teacher> teachers);
}
