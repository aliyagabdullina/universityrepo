package lesson;

import course.Course;
import course.CourseInProgram;
import group.Group;
import person.Teacher;
import place.Place;

import java.util.stream.Stream;

public interface LessonRequest {
    int getId();
    Group getGroup();
    CourseInProgram getCourseInProgram();

    void addOccupation(Course course, Stream<Teacher> occupiedTeachers, Stream<Place> availablePlaces);

    int getOccupationsNum();
    Stream<LessonRequestOccupation> getOccupations();
    LessonRequestOccupation getOccupation(String occupationId);
}
