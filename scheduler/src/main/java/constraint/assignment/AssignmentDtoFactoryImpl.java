package constraint.assignment;

import constraint.assignment.courseTeachers.DtoCourseTeachers;
import course.Course;
import person.Teacher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AssignmentDtoFactoryImpl implements AssignmentDtoFactory {
    @Override
    public DtoCourseTeachers createDtoCourseTeachers(Course course, Stream<Teacher> teachers) {
        DtoCourseTeachers result = new DtoCourseTeachers();
        result.courseName = course.getName();
        result.teacherNames = teachers
                .map(Teacher::getName)
                .toList();

        return result;
    }
}
