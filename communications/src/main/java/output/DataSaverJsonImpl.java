package output;

import collector.SchoolDataCollector;
import constraint.assignment.AssignmentDtoFactory;
import constraint.assignment.AssignmentDtoFactoryImpl;
import constraint.assignment.availablePlaces.DtoAvailablePlaces;
import constraint.assignment.availablePlaces.DtoPlacesForObject;
import constraint.assignment.courseTeachers.DtoCourseTeachersAssignments;
import constraint.assignment.groupCourseProgram.DtoGroupCourseProgram;
import constraint.assignment.groupCourseProgram.DtoGroupCourseProgramAssignments;
import constraint.assignment.groupCourseTeacher.DtoGroupCourseTeacher;
import constraint.assignment.groupCourseTeacher.DtoGroupCourseTeacherAssignments;
import constraint.timeConstraint.*;
import group.DtoGroupInput;
import group.DtoGroups;
import group.Group;
import course.*;
import lesson.DtoLesson;
import lesson.Lesson;
import lesson.LessonRequest;
import objectFactories.SchedulerDtoFactory;
import objectFactories.SchedulerDtoFactoryImpl;
import objectFactories.SchoolDtoFactory;
import objectFactories.SchoolDtoFactoryImpl;
import pair.Pair;
import person.DtoTeacherInput;
import person.DtoTeachers;
import person.Teacher;
import place.DtoPlace;
import place.DtoPlaces;
import place.Place;
import schedule.DtoSchedule;
import schedule.Schedule;
import scheduleBuilder.DtoScheduleSetupActions;
import scheduleBuilder.actions.SchedulingSetupActions;
import table.Table;
import table.TableImpl;
import time.DtoWeeklyTimeSlot;
import time.WeeklyTimeSlot;
import triplet.Triplet;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataSaverJsonImpl implements DataSaver {
    private DtoFileSettings _outputSettings;
    private SchoolDtoFactory _schoolDtoFactory = new SchoolDtoFactoryImpl();
    private SchedulerDtoFactory _schedulerDtoFactory = new SchedulerDtoFactoryImpl();

    private AssignmentDtoFactory _assignmentDtoFactory = new AssignmentDtoFactoryImpl();

    public DataSaverJsonImpl(DtoFileSettings _outputSettings) {
        this._outputSettings = _outputSettings;
    }

    @Override
    public void setSettings(DtoFileSettings settings) {
        _outputSettings = settings;
    }

    @Override
    public void saveTeachers(Stream<Teacher> teacherStream) {
        File teachersFile = _outputSettings.teachersFile;
        List<DtoTeacherInput> list = teacherStream
                .map(_schoolDtoFactory::createTeacherDto).toList();

        DtoTeachers dtoTeachers = new DtoTeachers();
        dtoTeachers.teachers = list;
        MyFileWriter<DtoTeachers> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(dtoTeachers, teachersFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveGroups(Stream<Group> groupsStream) {
        File groupsFile = _outputSettings.groupsFile;
        List<DtoGroupInput> list = groupsStream
                .map(_schoolDtoFactory::createGroupDto).toList();
        DtoGroups dtoGroups = new DtoGroups();
        dtoGroups.groups = list;
        MyFileWriter<DtoGroups> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(dtoGroups, groupsFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveCourses(Stream<Course> courseStream) {
        File coursesFile = _outputSettings.coursesFile;
        List<DtoCourseInput> list = courseStream
                .map(_schoolDtoFactory::createCourseDto).toList();
        DtoCourses dtoCourses = new DtoCourses();
        dtoCourses.courses = list;
        MyFileWriter<DtoCourses> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(dtoCourses, coursesFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void savePlaces(Stream<Place> placeStream) {
        File placesFile = _outputSettings.placesFile;
        List<DtoPlace> list = placeStream
                .map(_schoolDtoFactory::createPlaceDto).toList();
        DtoPlaces places = new DtoPlaces();
        places.places = list;

        MyFileWriter<DtoPlaces> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(places, placesFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveGroupCoursePrograms(Stream<Pair<Group, CourseProgram>> groupCourseProgramPairs) {
        File lessonRequestsFile = _outputSettings.groupCourseProgramsFile;
        List<DtoGroupCourseProgram> list = groupCourseProgramPairs
                .map(pair -> {
                    var result = new DtoGroupCourseProgram();
                    result.groupName = pair.getKey().getName();
                    result.courseProgramName = pair.getValue().getName();
                    return result;
                }).toList();
        DtoGroupCourseProgramAssignments toSave = new DtoGroupCourseProgramAssignments();
        toSave.groupProgram = list;

        MyFileWriter<DtoGroupCourseProgramAssignments> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(toSave, lessonRequestsFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveCourseTeachers(Stream<Pair<Course, Set<Teacher>>> courseTeachersPairs) {
        File courseTeachersFile = _outputSettings.courseTeachersFile;
        DtoCourseTeachersAssignments toSave = new DtoCourseTeachersAssignments();
        toSave.assignments = courseTeachersPairs
                .map(pair -> _assignmentDtoFactory.createDtoCourseTeachers(pair.getKey(), pair.getValue().stream()))
                .collect(Collectors.toList());

        MyFileWriter<DtoCourseTeachersAssignments> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(toSave, courseTeachersFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveGroupCourseTeacherAssignments(Stream<Triplet<Group, Course, Teacher>> tripletStream) {
        DtoGroupCourseTeacherAssignments dto = new DtoGroupCourseTeacherAssignments();
        dto.list = tripletStream
                .map(t -> {
                    var result = new DtoGroupCourseTeacher();
                    result.groupName = t.getFirst().getName();
                    result.courseName = t.getSecond().getName();
                    result.teacherName = t.getThird().getName();
                    return result;
                })
                .toList();

        MyFileWriter<DtoGroupCourseTeacherAssignments> fileWriter = new MyFileWriterJson<>();

        File file = _outputSettings.groupCourseTeacherFile;
        try {
            fileWriter.writeToFile(dto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveAvailablePlacesForGroups(Stream<Pair<Group, Stream<Place>>> groupPlaceStream) {
        File file = _outputSettings.groupPlacesFile;
        DtoAvailablePlaces dto = new DtoAvailablePlaces();
        dto.availablePlaces = groupPlaceStream
                .map(pair -> {
                    DtoPlacesForObject dtoPlacesForObject = new DtoPlacesForObject();
                    dtoPlacesForObject.object = pair.getKey().getName();
                    dtoPlacesForObject.places = pair.getValue()
                            .map(Place::getName)
                            .toList();
                    return dtoPlacesForObject;
                })
                .toList();

        MyFileWriter<DtoAvailablePlaces> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(dto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveAvailablePlacesForCourses(Stream<Pair<Course, Stream<Place>>> coursePlacesStream) {
        File file = _outputSettings.coursePlacesFile;
        DtoAvailablePlaces dto = new DtoAvailablePlaces();
        dto.availablePlaces = coursePlacesStream
                .map(pair -> {
                    DtoPlacesForObject dtoPlacesForObject = new DtoPlacesForObject();
                    dtoPlacesForObject.object = pair.getKey().getName();
                    dtoPlacesForObject.places = pair.getValue()
                            .map(Place::getName)
                            .toList();
                    return dtoPlacesForObject;
                })
                .toList();

        MyFileWriter<DtoAvailablePlaces> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(dto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveAvailablePlacesForTeachers(Stream<Pair<Teacher, Stream<Place>>> teacherPlaceStream) {
        File file = _outputSettings.teacherPlacesFile;
        DtoAvailablePlaces dto = new DtoAvailablePlaces();
        dto.availablePlaces = teacherPlaceStream
                .map(pair -> {
                    DtoPlacesForObject dtoPlacesForObject = new DtoPlacesForObject();
                    dtoPlacesForObject.object = pair.getKey().getName();
                    dtoPlacesForObject.places = pair.getValue()
                            .map(Place::getName)
                            .toList();
                    return dtoPlacesForObject;
                })
                .toList();

        MyFileWriter<DtoAvailablePlaces> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(dto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveTeacherTimeSlotStatuses(WeeklyTimeSlot[][] timeSlots, Stream<Pair<Teacher, TimeSlotStatus[][]>> teacherStatusesStream) {
        Table<DayOfWeek, WeeklyTimeSlot> table = new TableImpl<>(DayOfWeek.values(), timeSlots);
        File teacherTimeSlotsFile = _outputSettings.teacherTimeSlotsFile;
        DtoTimeTables dtoTimeTables = new DtoTimeTables();
        dtoTimeTables.timeTables = teacherStatusesStream
                .map(pair -> createDtoTimeTable(table, pair.getKey().getName(), pair.getValue()))
                .toList();

        MyFileWriter<DtoTimeTables> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(dtoTimeTables, teacherTimeSlotsFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void saveGroupTimeSlotStatuses(WeeklyTimeSlot[][] timeSlots, Stream<Pair<Group, TimeSlotStatus[][]>> groupStatusesStream) {
        Table<DayOfWeek, WeeklyTimeSlot> table = new TableImpl<>(DayOfWeek.values(), timeSlots);
        File groupTimeSlotsFile = _outputSettings.groupTimeSlotsFile;
        DtoTimeTables dtoTimeTables = new DtoTimeTables();
        dtoTimeTables.timeTables = groupStatusesStream
                .map(pair -> createDtoTimeTable(table, pair.getKey().getName(), pair.getValue()))
                .toList();

        MyFileWriter<DtoTimeTables> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(dtoTimeTables, groupTimeSlotsFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveTimeMetricConstraints(TimeTablesCollector collector) {
        saveTeacherTimeConstraints(collector);
        saveGroupTimeConstraints(collector);
    }

    private void saveGroupTimeConstraints(TimeTablesCollector collector) {
        Map<Group, Integer> maxDaysMap = collector.getGroupMaxDaysMap();
        Map<Group, Integer[]> maxDailyLoadMap = collector.getGroupMaxDailyLoadMap();

        DtoTimeConstraints dto = new DtoTimeConstraints();
        dto.constraints = maxDaysMap.keySet()
                .stream()
                .map(group -> {
                    DtoObjectTimeConstraints groupDto = new DtoObjectTimeConstraints();
                    groupDto.name = group.getName();
                    groupDto.maxDays = maxDaysMap.get(group);
                    groupDto.maxDailyLoad = maxDailyLoadMap.get(group);
                    return groupDto;
                })
                .toList();

        File teacherTimeMetricConstraintsFile = _outputSettings.groupsTimeMetricConstraints;

        MyFileWriter<DtoTimeConstraints> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(dto, teacherTimeMetricConstraintsFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveTeacherTimeConstraints(TimeTablesCollector collector) {
        Map<Teacher, Integer> maxDaysMap = collector.getTeacherMaxDaysMap();
        Map<Teacher, Integer[]> maxDailyLoadMap = collector.getTeacherMaxDailyLoadMap();

        DtoTimeConstraints dto = new DtoTimeConstraints();
        dto.constraints = maxDaysMap.keySet()
                .stream()
                .map(teacher -> {
                    DtoObjectTimeConstraints teacherDto = new DtoObjectTimeConstraints();
                    teacherDto.name = teacher.getName();
                    teacherDto.maxDays = maxDaysMap.get(teacher);
                    teacherDto.maxDailyLoad = maxDailyLoadMap.get(teacher);
                    return teacherDto;
                })
                .toList();

        File teacherTimeMetricConstraintsFile = _outputSettings.teachersTimeMetricConstraints;

        MyFileWriter<DtoTimeConstraints> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(dto, teacherTimeMetricConstraintsFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveSchedule(Schedule result, String scheduleName) {
        File file = _outputSettings.scheduleNameToFile.apply(scheduleName);
        DtoSchedule dtoSchedule = createScheduleDto(result);
        MyFileWriter<DtoSchedule> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(dtoSchedule, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveSchedulingSetup(SchedulingSetupActions schedulingSetupActions, String setUpName) {
        File file = _outputSettings.setupNameToFile.apply(setUpName);
        DtoScheduleSetupActions scheduleSetupActionsDto = createScheduleSetupActionsDto(schedulingSetupActions);
        MyFileWriter<DtoScheduleSetupActions> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(scheduleSetupActionsDto, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private DtoScheduleSetupActions createScheduleSetupActionsDto(SchedulingSetupActions schedulingSetupActions) {
        DtoScheduleSetupActions dto = new DtoScheduleSetupActions();
        dto.scheduledLessons = schedulingSetupActions.getScheduledLessons()
                .map(this::createLessonDto)
                .toList();
        var tmpList = schedulingSetupActions.getForbiddenLessonRequestsTimeSlots()
                .toList();
        dto.forbiddenLessonRequestIds = tmpList
                .stream()
                .sequential()
                .map(Pair::getKey)
                .map(LessonRequest::getId)
                .toList();
        dto.forbiddenLessonRequestTimeSlots = tmpList
                .stream()
                .sequential()
                .map(Pair::getValue)
                .map(this::createWeeklyTimeSlotDto)
                .toList();
        return dto;
    }

    private DtoSchedule createScheduleDto(Schedule result) {
        DtoSchedule dtoSchedule = new DtoSchedule();
        dtoSchedule.lessons = result.getAllLessons()
                .map(this::createLessonDto)
                .toList();
        return dtoSchedule;
    }

    private DtoLesson createLessonDto(Lesson lesson) {
        DtoLesson dtoLesson = new DtoLesson();
        dtoLesson.course = lesson.getCourse().getName();
        dtoLesson.group = lesson.getGroup().getName();
        dtoLesson.place = lesson.getPlace().getName();
        dtoLesson.teacher = lesson.getTeachers().toList().get(0).getName();
        dtoLesson.timeSlot  = createWeeklyTimeSlotDto(lesson.getTimeSlot());
        return dtoLesson;
    }

    private DtoTimeTable createDtoTimeTable(Table<DayOfWeek, WeeklyTimeSlot> table, String name, TimeSlotStatus[][] value) {
        DtoTimeTable result = new DtoTimeTable();
        result.objName = name;
        for (int i = 0; i < table.getDataRowsNum(); i++) {
            for (int j = 0; j < table.getColumnsNum(); j++) {
                var val = value[i][j];
                switch (val) {
                    case PREFERRED -> result.preferredSlots.add(createWeeklyTimeSlotDto(table.getValueAt(i, j)));
                    case UNDESIRABLE -> result.undesirableSlots.add(createWeeklyTimeSlotDto(table.getValueAt(i, j)));
                    case PROHIBITED -> result.prohibitedSlots.add(createWeeklyTimeSlotDto(table.getValueAt(i, j)));
                    default -> {}
                }
            }
        }
        return result;
    }

    private DtoWeeklyTimeSlot createWeeklyTimeSlotDto(WeeklyTimeSlot weeklyTimeSlot) {
        DtoWeeklyTimeSlot dtoWeeklyTimeSlot = new DtoWeeklyTimeSlot();
        dtoWeeklyTimeSlot.dayOfWeek = weeklyTimeSlot.getDayOfWeek();
        dtoWeeklyTimeSlot.from = weeklyTimeSlot.getStartTimeInMs();
        dtoWeeklyTimeSlot.to = weeklyTimeSlot.getEndTimeInMs();
        return dtoWeeklyTimeSlot;
    }

    @Override
    public void saveSchoolData(SchoolDataCollector schoolDataCollector) {
        saveCourses(schoolDataCollector.getCourses());
        saveTeachers(schoolDataCollector.getTeachers());
        saveGroups(schoolDataCollector.getGroups());
        savePlaces(schoolDataCollector.getPlaces());
        saveCoursePrograms(schoolDataCollector.getCoursePrograms());
    }

    private void saveCoursePrograms(Stream<CourseProgram> coursePrograms) {
        File courseProgramsFile = _outputSettings.courseProgramsFile;
        List<DtoCourseProgram> list = coursePrograms
                .map(_schoolDtoFactory::createCourseProgramDto).toList();
        DtoCoursePrograms dtoCoursePrograms = new DtoCoursePrograms();
        dtoCoursePrograms.programs = list;

        MyFileWriter<DtoCoursePrograms> fileWriter = new MyFileWriterJson<>();
        try {
            fileWriter.writeToFile(dtoCoursePrograms, courseProgramsFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
