package lesson;

import course.Course;
import course.DtoCourseInProgram;
import group.Group;
import person.Teacher;
import place.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DtoLessonsRequest {
    public int id;
    public String groupName;
    public DtoCourseInProgram courseInProgram;
    public List<DtoLessonRequestOccupation> occupations = new ArrayList<>();

}
