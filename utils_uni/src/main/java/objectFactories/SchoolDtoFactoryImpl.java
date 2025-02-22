package objectFactories;

import course.*;
import group.DtoGroupInput;
import group.Group;
import person.DtoTeacherInput;
import person.Student;
import person.Teacher;
import place.DtoPlace;
import place.Place;

public class SchoolDtoFactoryImpl implements SchoolDtoFactory {
    @Override
    public DtoTeacherInput createTeacherDto(Teacher teacher) {
        DtoTeacherInput dtoTeacherInput = new DtoTeacherInput();
        dtoTeacherInput.name = teacher.getName();
        return dtoTeacherInput;
    }

    @Override
    public DtoGroupInput createGroupDto(Group group) {
        DtoGroupInput dtoGroupInput = new DtoGroupInput();
        dtoGroupInput.name = group.getName();
        dtoGroupInput.students = group.getStudents().map(Student::getName).toList();
        return dtoGroupInput;
    }

    @Override
    public DtoCourseInput createCourseDto(Course course) {
        DtoCourseInput courseInput = new DtoCourseInput();
        courseInput.name  = course.getName();
        courseInput.aliasing = course.getAliasing();
        return courseInput;
    }

    @Override
    public DtoPlace createPlaceDto(Place place) {
        DtoPlace placeInput = new DtoPlace();
        placeInput.name = place.getName();
        placeInput.tagList = place.getTags().toList();
        return placeInput;
    }

    @Override
    public DtoCourseProgram createCourseProgramDto(CourseProgram courseProgram) {
        DtoCourseProgram dto = new DtoCourseProgram();
        dto.name = courseProgram.getName();
        dto.courses = courseProgram.getCoursesInProgram()
                .map(this::createDtoCourseInProgram)
                .toList();
        dto.targets = courseProgram.getDailyTargets()
                .map(this::createDtoDailyTarget)
                .toList();
        return dto;
    }

    private DtoCourseProgramDailyTarget createDtoDailyTarget(CourseProgramDailyTarget target) {
        var dto = new DtoCourseProgramDailyTarget();
        dto.targetType = target.getTargetType().toString();
        dto.dayOfWeek = target.getDayOfWeek().toString();
        dto.value = target.getValue();
        return dto;
    }

    @Override
    public DtoCourseInProgram createDtoCourseInProgram(CourseInProgram courseInProgram) {
        DtoCourseInProgram dto = new DtoCourseInProgram();
        dto.courseNames = courseInProgram.getCourses()
                .map(Course::getName)
                .toList();
        dto.maxLessonsPerDay = courseInProgram.getMaxLessonsPerDay();
        dto.complexity = courseInProgram.getComplexity();
        dto.lessonsPerWeek = courseInProgram.getLessonsPerWeek();
        dto.courseDaysPerWeek = courseInProgram.getCourseDaysPerWeek();
        return dto;
    }
}
