package lesson;

import course.Course;
import course.CourseInProgram;
import group.Group;
import person.Teacher;
import place.Place;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class LessonRequestImpl implements LessonRequest {
    private int _nextOcupationId = 0;
    private final int _id;
    private final Group _group;
    private final CourseInProgram _courseInProgram;
    private final Set<LessonRequestOccupation> _occupations = new HashSet<>();

    public LessonRequestImpl(int id, Group group, CourseInProgram courseInProgram) {
        _id = id;
        _group = group;
        _courseInProgram = courseInProgram;
    }

    @Override
    public void addOccupation(Course course, Stream<Teacher> occupiedTeachers, Stream<Place> availablePlaces) {
        int id = _nextOcupationId;
        _nextOcupationId++;
        LessonRequestOccupation occupation = new LessonRequestOccupationImpl(this, id, course, occupiedTeachers, availablePlaces);
        _occupations.add(occupation);
    }

    @Override
    public int getOccupationsNum() {
        return _occupations.size();
    }

    @Override
    public int getId() {
        return _id;
    }

    @Override
    public Group getGroup() {
        return _group;
    }

    @Override
    public CourseInProgram getCourseInProgram() {
        return _courseInProgram;
    }


    @Override
    public Stream<LessonRequestOccupation> getOccupations() {
        return _occupations.stream();
    }

    @Override
    public LessonRequestOccupation getOccupation(String occupationId) {

        int i = Integer.parseInt(occupationId);
        return _occupations
                .stream()
                .filter(occupation -> occupation.getId() == i)
                .findAny()
                //.orElse(null);
                .orElseThrow(() -> new IllegalStateException("Occupation not presented in lesson request"));
    }
}
