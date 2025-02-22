package course;

import java.util.stream.Stream;

public interface CourseInProgram {
    Stream<Course> getCourses();

    int getComplexity();
    int getLessonsPerWeek();
    int getMaxLessonsPerDay();
    int getCourseDaysPerWeek();

    void setComplexity(int complexity);
    void setLessonsPerWeek(int lessonsPerWeek);
    void setMaxLessonsPerDay(int maxLessonsPerDay);
    void setCourseDaysPerWeek(int courseDaysPerWeek);
}
