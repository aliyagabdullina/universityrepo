package constraint.assignment;

import course.Course;
import course.CourseProgram;
import group.Group;
import pair.Pair;
import person.Teacher;
import place.Place;
import triplet.Triplet;

import java.util.Set;
import java.util.stream.Stream;

public interface AssignmentCollector {
    // PLACES
    void setAvailablePlacesForCourse(Course course, Stream<Place> placeStream);
    void setAvailablePlacesForGroup(Group group, Stream<Place> placeStream);
    void setAvailablePlacesForTeacher(Teacher teacher, Stream<Place> placeStream);
    Stream<Place> getAvailablePlacesForCourse(Course course);
    Stream<Place> getAvailablePlacesForGroup(Group group);
    Stream<Place> getAvailablePlacesForTeacher(Teacher teacher);


    void setAvailableTeachersForCourse(Course course, Stream<Teacher> teacherStream);
    Stream<Pair<Course, Set<Teacher>>> getCourseTeachersStream();
    void setCourseProgramForGroup(Group group, CourseProgram courseProgram);
    void removeGroupCourseProgramAssignment(Group group, CourseProgram courseProgram);

    Stream<Pair<Group, CourseProgram>> getGroupCourseProgramStream();
    void setAssignmentStatus(Group group, Course course, Teacher teacher, AssignmentStatus assignmentStatus);
    Stream<Teacher> getAvailableTeachersForCourse(Course course);
    CourseProgram getCourseProgramForGroup(Group group);
    AssignmentStatus getGroupCourseTeacherAssignmentStatus(Group group, Course course, Teacher teacher);

    Stream<Teacher> getAssignedTeachers(Group group, Course c);

    Stream<Triplet<Group, Course, Teacher>> getGroupCourseAssignedTeachers();

    Stream<Place> getAvailablePlaces(Group group, Course course, Stream<Teacher> teachers);


    void addTeacherForCourse(Course course, Teacher teacher);

    void addAvailablePlaceForGroup(Group group, Place place);

    void addAvailablePlaceForTeacher(Teacher teacher, Place place);

    void addAvailablePlaceForCourse(Course course, Place place);

    void addCourseProgramForGroup(Group group, CourseProgram courseProgram);
}
