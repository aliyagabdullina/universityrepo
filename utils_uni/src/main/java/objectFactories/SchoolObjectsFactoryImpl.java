package objectFactories;

import collector.SchoolDataCollector;
import course.*;
import group.DtoGroupInput;
import group.Group;
import group.GroupImpl;
import person.*;
import place.DtoPlace;
import place.Place;
import place.PlaceImpl;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SchoolObjectsFactoryImpl implements SchoolObjectsFactory {
    private final SchoolDataCollector _dataCollector;

    public SchoolObjectsFactoryImpl(SchoolDataCollector dataCollector) {
        _dataCollector = dataCollector;
    }

    @Override
    public Teacher buildTeacher(DtoTeacherInput input) {
        return new TeacherImpl(input.name);
    }

    @Override
    public Student buildStudent(DtoStudentInput input) {
        return new StudentImpl(input.name);
    }

    @Override
    public Group buildGroup(DtoGroupInput input) {
        Set<Student> students = input.students
                .stream()
                .map(_dataCollector::getStudent)
                .collect(Collectors.toSet());
        return new GroupImpl(input.name, students);
    }

    @Override
    public Course buildCourse(DtoCourseInput dtoCourseInput) {
        Course result = new CourseImpl(dtoCourseInput.name);
        String name = dtoCourseInput.name;
        //dtoCourseInput.aliasing = name.contains("_") ? name.split("_")[0]: name;
        if(dtoCourseInput.aliasing != null) {
            result.setAliasing(dtoCourseInput.aliasing);
        } else {
            result.setAliasing(dtoCourseInput.name);
        }
        return result;
    }

    @Override
    public Place buildPlace(DtoPlace dtoPlace) {
        Place result =  new PlaceImpl(dtoPlace.name);
        dtoPlace.tagList.forEach(result::addTag);
        return result;
    }

    @Override
    public CourseProgram buildCourseProgram(DtoCourseProgram dtoCourseProgram) {
        CourseProgram courseProgram = new CourseProgramImpl(dtoCourseProgram.name);
        dtoCourseProgram.courses
                .stream()
                .map(this::buildCourseInProgram)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(courseProgram::addCourse);
        dtoCourseProgram.targets
                .stream()
                .map(this::buildCourseProgramDailyTarget)
                .forEach(courseProgram::addDailyTarget);
        return courseProgram;
    }

    private CourseProgramDailyTarget buildCourseProgramDailyTarget(DtoCourseProgramDailyTarget dto) {
        return new CourseProgramDailyTargetImpl(
                DailyTargetType.valueOf(dto.targetType),
                DayOfWeek.valueOf(dto.dayOfWeek),
                dto.value
        );
    }

    private Optional<CourseInProgram> buildCourseInProgram(DtoCourseInProgram dtoCourseInProgram) {
        List<Course> courses = dtoCourseInProgram.courseNames
                .stream()
                .map(_dataCollector::getCourse)
                .filter(Objects::nonNull)
                .toList();
        if(courses.isEmpty()) {
            return Optional.empty();
        }

        CourseInProgram courseInProgram = new CourseInProgramImpl(courses.stream());
        courseInProgram.setComplexity(dtoCourseInProgram.complexity);
        courseInProgram.setCourseDaysPerWeek(dtoCourseInProgram.courseDaysPerWeek);
        courseInProgram.setLessonsPerWeek(dtoCourseInProgram.lessonsPerWeek);
        courseInProgram.setMaxLessonsPerDay(dtoCourseInProgram.maxLessonsPerDay);
        return Optional.of(courseInProgram);
    }
}
