package course;

import java.util.stream.Stream;

public interface CourseProgram {
    Stream<CourseInProgram> getCoursesInProgram();
    void addCourse(CourseInProgram courseInProgram);
    String getName();
    void removeCourseInProgram(CourseInProgram courseInProgram);
    Stream<CourseProgramDailyTarget> getDailyTargets();
    void addDailyTarget(CourseProgramDailyTarget target);
    void removeDailyTarget(CourseProgramDailyTarget target);

    static CourseProgram createEmpty() {
        return new CourseProgramImpl("");
    }
}
