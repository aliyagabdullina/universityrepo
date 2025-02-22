package objectFactories;

import lesson.DtoLessonRequestOccupation;
import lesson.DtoLessonsRequest;
import lesson.LessonRequestOccupation;
import lesson.LessonRequest;
import person.Teacher;
import place.Place;

public class SchedulerDtoFactoryImpl implements SchedulerDtoFactory {
    private final static SchoolDtoFactory _schoolDtoFactory = new SchoolDtoFactoryImpl();
    @Override
    public DtoLessonsRequest createLessonRequestDto(LessonRequest lessonRequest) {
        DtoLessonsRequest result = new DtoLessonsRequest();
        result.id = lessonRequest.getId();
        result.groupName = lessonRequest.getGroup().getName();
        result.occupations = lessonRequest.getOccupations()
                .map(this::createLessonRequestOccupationDto)
                .toList();
                /*
        result.teacherNames = lessonsRequest.getOccupiedTeachers()
                .map(Teacher::getName)
                .collect(Collectors.toList());
        result.availablePlaces = lessonsRequest.getPlacesStream()
                .map(Place::getName)
                .collect(Collectors.toList());

                 */
        result.courseInProgram = _schoolDtoFactory.createDtoCourseInProgram(lessonRequest.getCourseInProgram());
        return result;
    }

    private DtoLessonRequestOccupation createLessonRequestOccupationDto(LessonRequestOccupation occupation) {
        var result = new DtoLessonRequestOccupation();
        result.teachers = occupation.getOccupiedTeachers()
                .map(Teacher::getName)
                .toList();
        result.places = occupation.getCandidatePlaces()
                .map(Place::getName)
                .toList();
        return result;
    }
}
