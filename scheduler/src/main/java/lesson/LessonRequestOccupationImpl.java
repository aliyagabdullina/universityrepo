package lesson;

import course.Course;
import person.Teacher;
import place.Place;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LessonRequestOccupationImpl implements LessonRequestOccupation {
    private final LessonRequest _lessonRequest;
    private final int _id;
    private final Set<Teacher> _teachers;
    private final Set<Place> _places;
    private final Course _course;


    public LessonRequestOccupationImpl(LessonRequest lessonRequest, int id, Course course, Stream<Teacher> teachers, Stream<Place> places) {
        _lessonRequest = lessonRequest;
        _id = id;
        _course = course;
        _teachers = teachers.collect(Collectors.toUnmodifiableSet());
        _places = places.collect(Collectors.toUnmodifiableSet());
        if(_places.size() == 0) {
            throw new IllegalStateException("no places");
        }
        if(_teachers.size() > 1) {
            System.out.println("!" + _teachers.size() + " teachers in course " + course.getName());
        }
    }

    @Override
    public LessonRequest getLessonRequest() {
        return _lessonRequest;
    }

    @Override
    public int getId() {
        return _id;
    }

    @Override
    public Stream<Teacher> getOccupiedTeachers() {
        return _teachers.stream();
    }

    @Override
    public Course getCourse() {
        return _course;
    }

    @Override
    public Stream<Place> getCandidatePlaces() {
        return _places.stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LessonRequestOccupationImpl that = (LessonRequestOccupationImpl) o;
        return _id == that._id && Objects.equals(_lessonRequest, that._lessonRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_lessonRequest, _id);
    }
}
