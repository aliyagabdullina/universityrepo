package objectFactories;

import course.Course;
import course.CourseProgram;
import course.DtoCourseInput;
import course.DtoCourseProgram;
import group.DtoGroupInput;
import group.Group;
import person.DtoStudentInput;
import person.DtoTeacherInput;
import person.Student;
import person.Teacher;
import place.DtoPlace;
import place.Place;

import java.util.Optional;

public interface SchoolObjectsFactory {
    Teacher buildTeacher(DtoTeacherInput input);
    Student buildStudent(DtoStudentInput input);
    Group buildGroup(DtoGroupInput input);
    Course buildCourse(DtoCourseInput dtoCourseInput);

    Place buildPlace(DtoPlace dtoPlace);

    CourseProgram buildCourseProgram(DtoCourseProgram dtoCourseProgram);

}
