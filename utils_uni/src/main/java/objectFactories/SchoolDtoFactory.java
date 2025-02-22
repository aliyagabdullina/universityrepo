package objectFactories;

import course.*;
import group.DtoGroupInput;
import group.Group;
import person.DtoTeacherInput;
import person.Teacher;
import place.DtoPlace;
import place.Place;

public interface SchoolDtoFactory {
    DtoTeacherInput createTeacherDto(Teacher teacher);

    DtoGroupInput createGroupDto(Group group);

    DtoCourseInput createCourseDto(Course course);

    DtoPlace createPlaceDto(Place place);
    DtoCourseProgram createCourseProgramDto(CourseProgram courseProgram);

    DtoCourseInProgram createDtoCourseInProgram(CourseInProgram courseInProgram);
}
