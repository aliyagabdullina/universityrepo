package course;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CourseInProgramImpl implements CourseInProgram {
    private final Set<Course> _courses;
    private int _complexity;
    private int _lessonsPerWeek;
    private int _maxLessonsPerDay;
    private int _daysPerWeek;

    public CourseInProgramImpl(Stream<Course> courses) {
        _courses = courses.collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseInProgramImpl that = (CourseInProgramImpl) o;
        return Objects.equals(_courses, that._courses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_courses);
    }

    @Override
    public Stream<Course> getCourses() {
        return _courses.stream();
    }

    @Override
    public int getComplexity() {
        return _complexity;
    }

    @Override
    public int getLessonsPerWeek() {
        return _lessonsPerWeek;
    }

    @Override
    public int getMaxLessonsPerDay() {
        return _maxLessonsPerDay;
    }

    @Override
    public int getCourseDaysPerWeek() {
        return _daysPerWeek;
    }

    @Override
    public void setComplexity(int complexity) {
        _complexity = complexity;
    }

    @Override
    public void setLessonsPerWeek(int lessonsPerWeek) {
        _lessonsPerWeek = lessonsPerWeek;
    }

    @Override
    public void setMaxLessonsPerDay(int maxLessonsPerDay) {
        _maxLessonsPerDay = maxLessonsPerDay;
    }

    @Override
    public void setCourseDaysPerWeek(int courseDaysPerWeek) {
        _daysPerWeek = courseDaysPerWeek;
    }

}
