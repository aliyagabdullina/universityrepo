package lesson;

import course.Course;
import person.Teacher;
import place.Place;

import java.util.stream.Stream;

public interface LessonRequestOccupation {
    int getId();
    Course getCourse();
    Stream<Teacher> getOccupiedTeachers();
    Stream<Place> getCandidatePlaces();
    LessonRequest getLessonRequest();
}
