package collector;

import course.DtoCourseInput;
import group.DtoGroupInput;
import person.DtoStudentInput;
import person.DtoTeacherInput;
import place.DtoPlace;

import java.util.ArrayList;
import java.util.List;

public class DtoSchoolDataCollector {
    public List<DtoTeacherInput> teachers = new ArrayList<>();
    public List<DtoCourseInput> courses = new ArrayList<>();
    public List<DtoPlace> places = new ArrayList<>();
    public List<DtoStudentInput> students = new ArrayList<>();
    public List<DtoGroupInput> groups = new ArrayList<>();

}
