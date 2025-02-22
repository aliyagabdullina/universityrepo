package course;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class CourseProgramImpl implements CourseProgram {
    private final String _name;
    private final Set<CourseInProgram> _courses = new HashSet<>();
    private final Set<CourseProgramDailyTarget> _dailyTargets = new HashSet<>();

    public CourseProgramImpl(String name) {
        _name = name;
    }

    @Override
    public Stream<CourseInProgram> getCoursesInProgram() {
        return _courses.stream();
    }
    @Override
    public void addCourse(CourseInProgram courseInProgram) {
        _courses.add(courseInProgram);
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void removeCourseInProgram(CourseInProgram courseInProgram) {
        _courses.remove(courseInProgram);
    }

    @Override
    public Stream<CourseProgramDailyTarget> getDailyTargets() {
        return _dailyTargets.stream();
    }

    @Override
    public void addDailyTarget(CourseProgramDailyTarget target) {
        _dailyTargets.add(target);
    }

    @Override
    public void removeDailyTarget(CourseProgramDailyTarget target) {
        _dailyTargets.remove(target);
    }


    @Override
    public String toString() {
        return _name;
    }
}
