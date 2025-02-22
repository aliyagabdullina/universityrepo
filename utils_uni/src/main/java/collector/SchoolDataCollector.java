package collector;

import course.Course;
import course.CourseProgram;
import group.Group;
import objectFactories.SchoolObjectsFactory;
import person.Student;
import person.Teacher;
import place.Place;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface SchoolDataCollector {
    void load(Path toPath, SchoolDataStoringFormat json, SchoolObjectsFactory factory) throws IOException;
    // teachers
    void addTeacher(Teacher teacher);
    void removeTeacher(String teacherName);
    Teacher getTeacher(String name);
    Stream<Teacher> getTeachers();

    // groups
    void addGroup(Group group);
    Group getGroup(String groupName);


    void removeGroup(String groupName);

    Stream<Group> getGroups();
    Stream<Student> getStudents();

    void addStudent(Student student);

    Student getStudent(String s);


    void addCourse(Course course);
    Stream<Course> getCourses();

    Course getCourse(String courseName);
    void removeCourse(String courseName);

    void addCourseProgram(CourseProgram course);
    Stream<CourseProgram> getCoursePrograms();

    CourseProgram getCourseProgram(String courseProgramName);
    void removeCourseProgram(String courseProgramName);

    Stream<Place> getPlaces();

    Place getPlace(String placeName);

    void addPlace(Place place);

    void removePlace(String placeName);


}
