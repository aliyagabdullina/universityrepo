package input;

import collector.SchoolDataCollector;
import constraint.assignment.AssignmentCollector;
import constraint.assignment.AssignmentStatus;
import constraint.assignment.availablePlaces.DtoAvailablePlaces;
import constraint.assignment.courseTeachers.DtoCourseTeachers;
import constraint.assignment.courseTeachers.DtoCourseTeachersAssignments;
import constraint.assignment.groupCourseProgram.DtoGroupCourseProgram;
import constraint.assignment.groupCourseProgram.DtoGroupCourseProgramAssignments;
import constraint.assignment.groupCourseTeacher.DtoGroupCourseTeacher;
import constraint.assignment.groupCourseTeacher.DtoGroupCourseTeacherAssignments;
import constraint.timeConstraint.*;
import course.Course;
import course.CourseProgram;
import course.DtoCoursePrograms;
import course.DtoCourses;
import group.DtoGroups;
import group.Group;
import lesson.DtoLesson;
import lesson.Lesson;
import lesson.LessonImpl;
import lesson.LessonRequest;
import objectFactories.SchoolObjectsFactory;
import objectFactories.SchoolObjectsFactoryImpl;
import output.DtoFileSettings;
import person.DtoTeachers;
import person.Teacher;
import place.DtoPlaces;
import place.Place;
import schedule.DtoSchedule;
import schedule.Schedule;
import schedule.ScheduleImpl;
import scheduleBuilder.DtoScheduleSetupActions;
import scheduleBuilder.actions.SchedulingSetupActions;
import scheduleBuilder.actions.SchedulingSetupActionsImpl;
import time.DtoWeeklyTimeSlot;
import time.WeeklyTimeSlot;
import time.WeeklyTimeSlotImpl;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DataLoaderImpl implements DataLoader {

    private DtoFileSettings _settings;
    private SchoolObjectsFactory _objFactory;

    public DataLoaderImpl(DtoFileSettings settings) {
        _settings = settings;
    }

    @Override
    public void setSettings(DtoFileSettings settings) {
        _settings = settings;
    }

    @Override
    public void loadSchoolData(SchoolDataCollector dataCollector) {
        _objFactory = new SchoolObjectsFactoryImpl(dataCollector);
        loadTeachers(dataCollector);
        loadCourses(dataCollector);
        loadGroups(dataCollector);
        loadCoursePrograms(dataCollector);
        loadPlaces(dataCollector);
    }

    @Override
    public void loadAssignmentData(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector) {
        loadGroupCoursePrograms(dataCollector, assignmentCollector);
        loadCourseTeachers(dataCollector, assignmentCollector);
        loadGroupCourseTeacherAssignments(dataCollector, assignmentCollector);
        loadAvailablePlacesForTeachers(dataCollector, assignmentCollector);
        loadAvailablePlacesForCourses(dataCollector, assignmentCollector);
        loadAvailablePlacesForGroups(dataCollector, assignmentCollector);
    }

    @Override
    public void loadTimeTables(SchoolDataCollector dataCollector, WeeklyTimeSlot[][] timeSlots, TimeTablesCollector timeTablesCollector) {
        loadTeacherTimeSlots(dataCollector.getTeachers(), timeSlots, timeTablesCollector);
        loadTeachersTimeConstraints(dataCollector.getTeachers(), timeTablesCollector);
        loadGroupTimeSlots(dataCollector.getGroups(), timeSlots, timeTablesCollector);
        loadGroupsTimeConstraints(dataCollector.getGroups(), timeTablesCollector);
    }

    private void loadGroupTimeSlots(Stream<Group> groups, WeeklyTimeSlot[][] timeSlots, TimeTablesCollector timeTablesCollector) {
        File file = _settings.groupTimeSlotsFile;
        MyFileReader<DtoTimeTables> reader = new MyJsonFileReader<>(DtoTimeTables.class);
        try {
            DtoTimeTables dto = reader.readFromFile(file);
            Map<String, DtoTimeTable> groupNameTimeTableMap = dto.timeTables
                .stream()
                .collect(Collectors.toConcurrentMap(timeTable -> timeTable.objName, timeTable -> timeTable));

            groups
                .forEach(gro -> {
                    DtoTimeTable dtoTimeTable = groupNameTimeTableMap.getOrDefault(gro.getName(),new DtoTimeTable());
                    TimeSlotStatus[][] availableStatuses  = createAvailableStatuses(timeSlots, dtoTimeTable);
                    timeTablesCollector.setGroupTimeTable(gro, availableStatuses);
                });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void loadGroupsTimeConstraints(Stream<Group> groupStream, TimeTablesCollector timeTablesCollector) {
        File file = _settings.groupsTimeMetricConstraints;
        MyFileReader<DtoTimeConstraints> reader = new MyJsonFileReader<>(DtoTimeConstraints.class);
        try {
            DtoTimeConstraints dto = reader.readFromFile(file);

            Map<String, DtoObjectTimeConstraints> groupTimeConstraintMap = dto.constraints
                    .stream()
                    .collect(Collectors.toConcurrentMap(timeConstraints -> timeConstraints.name, timeTable -> timeTable));

            groupStream
                    .forEach(group -> {
                        DtoObjectTimeConstraints dtoTimeConstraint = groupTimeConstraintMap.get(group.getName());
                        int maxDays = dtoTimeConstraint == null ? DayOfWeek.values().length : dtoTimeConstraint.maxDays;
                        timeTablesCollector.setGroupNumWorkdaysUb(group, maxDays);
                        Integer[] array = dtoTimeConstraint == null ?IntStream.range(0,7).map(i -> 10).boxed().toArray(Integer[]::new) : dtoTimeConstraint.maxDailyLoad;
                        timeTablesCollector.setGroupMaxDailyLoad(group, array);
                    });
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }


    @Override
    public Schedule loadSchedule(SchoolDataCollector dataCollector, File scheduleFile) {
        MyFileReader<DtoSchedule> reader = new MyJsonFileReader<>(DtoSchedule.class);
        try {
            DtoSchedule dto = reader.readFromFile(scheduleFile);
            Stream<Lesson> lessonStream = dto.lessons
                    .stream()
                    .map(dtoLesson -> createLesson(dataCollector, dtoLesson));
            return new ScheduleImpl(lessonStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Lesson createLesson(SchoolDataCollector dataCollector, DtoLesson dtoLesson) {
        WeeklyTimeSlot timeSlot = createTimeSlot(dtoLesson.timeSlot);
        Group group = dataCollector.getGroup(dtoLesson.group);
        Course course = dataCollector.getCourse(dtoLesson.course);
        Place place = dataCollector.getPlace(dtoLesson.place);
        Teacher teacher = dataCollector.getTeacher(dtoLesson.teacher);
        return new LessonImpl(timeSlot, group, place, teacher, course);
    }


    @Override
    public SchedulingSetupActions loadSchedulingSetup(SchoolDataCollector dataCollector, File file, Map<Integer, LessonRequest> lessonRequestMap) {
        DtoScheduleSetupActions dto = loadSchedulingSetup(file);
        SchedulingSetupActions result = new SchedulingSetupActionsImpl();
        dto.scheduledLessons
                .stream()
                .map(dtoLesson -> createLesson(dataCollector, dtoLesson))
                .forEach(result::addScheduleLesson);
        int numForbiddenLessons = dto.forbiddenLessonRequestIds.size();
        for(int i=0; i< numForbiddenLessons; i++) {
            //TODO THIS IS NOT CLEAR!! LESSON REQUEST IDS CAN BE CHANGED BY ANY MODIFICATION OF PROGRAM!!!!!!!!!!!!
            int lessonRequestId = dto.forbiddenLessonRequestIds.get(i);
            LessonRequest lessonRequest = lessonRequestMap.get(lessonRequestId);
            if(lessonRequest == null) {
                throw new IllegalStateException("Scheduling setup is not valid!");
            }
            var dtoTimeSlot = dto.forbiddenLessonRequestTimeSlots.get(i);
            WeeklyTimeSlot timeSlot = createTimeSlot(dtoTimeSlot);
            result.addForbidLessonRequestTimeSlot(lessonRequest, timeSlot);
        }
        return result;
    }

    private DtoScheduleSetupActions loadSchedulingSetup(File file) {
        MyFileReader<DtoScheduleSetupActions> reader = new MyJsonFileReader<>(DtoScheduleSetupActions.class);
        try {
            return reader.readFromFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTeachersTimeConstraints(Stream<Teacher> teacherStream, TimeTablesCollector timeTablesCollector) {
        File file = _settings.teachersTimeMetricConstraints;
        MyFileReader<DtoTimeConstraints> reader = new MyJsonFileReader<>(DtoTimeConstraints.class);
        try {
            DtoTimeConstraints dto = reader.readFromFile(file);

            Map<String, DtoObjectTimeConstraints> teacherTimeConstraintMap = dto.constraints
                    .stream()
                    .collect(Collectors.toConcurrentMap(timeConstraints -> timeConstraints.name, timeTable -> timeTable));

            teacherStream
                    .forEach(teacher -> {
                        DtoObjectTimeConstraints dtoTeacherTimeConstraint = teacherTimeConstraintMap.get(teacher.getName());
                        int maxDays = dtoTeacherTimeConstraint == null ? DayOfWeek.values().length : dtoTeacherTimeConstraint.maxDays;
                        timeTablesCollector.setTeacherNumWorkdaysUb(teacher, maxDays);
                        Integer[] array = dtoTeacherTimeConstraint == null ?IntStream.range(0,7).map(i -> 10).boxed().toArray(Integer[]::new) : dtoTeacherTimeConstraint.maxDailyLoad;
                        timeTablesCollector.setTeacherMaxDailyLoad(teacher, array);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void loadTeacherTimeSlots(Stream<Teacher> teachers, final WeeklyTimeSlot[][] timeSlots, TimeTablesCollector timeTablesCollector) {
        File file = _settings.teacherTimeSlotsFile;
        MyFileReader<DtoTimeTables> reader = new MyJsonFileReader<>(DtoTimeTables.class);
        try {
            DtoTimeTables dto = reader.readFromFile(file);
            Map<String, DtoTimeTable> teacherNameTimeTableMap = dto.timeTables
                            .stream()
                                    .collect(Collectors.toConcurrentMap(timeTable -> timeTable.objName, timeTable -> timeTable));

            teachers
                    .forEach(teacher -> {
                        DtoTimeTable dtoTimeTable = teacherNameTimeTableMap.getOrDefault(teacher.getName(),new DtoTimeTable());
                        TimeSlotStatus[][] availableStatuses  = createAvailableStatuses(timeSlots, dtoTimeTable);
                        timeTablesCollector.setTeacherTimeTable(teacher, availableStatuses);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private TimeSlotStatus[][] createAvailableStatuses(WeeklyTimeSlot[][] timeSlots, DtoTimeTable dtoTimeTable) {
        Set<WeeklyTimeSlot> prohibitedSlots = createTimeSlotSet(dtoTimeTable.prohibitedSlots);
        Set<WeeklyTimeSlot> preferredSlots = createTimeSlotSet(dtoTimeTable.preferredSlots);
        Set<WeeklyTimeSlot> undesirableSlots = createTimeSlotSet(dtoTimeTable.undesirableSlots);

        int dim1 = timeSlots.length;
        int dim2 = dim1 == 0 ? 0 : timeSlots[0].length;
        TimeSlotStatus[][] result = new TimeSlotStatus[dim1][dim2];
        for (int i = 0; i < dim1; i++) {
            for (int j = 0; j < dim2; j++) {
                WeeklyTimeSlot timeSlot = timeSlots[i][j];
                if(timeSlot == null) {
                    result[i][j] = TimeSlotStatus.INDIFFERENT;
                } else {
                    if (preferredSlots.contains(timeSlot)) {
                        result[i][j] = TimeSlotStatus.PREFERRED;
                    } else if (prohibitedSlots.contains(timeSlot)) {
                        result[i][j] = TimeSlotStatus.PROHIBITED;
                    } else if (undesirableSlots.contains(timeSlot)) {
                        result[i][j] = TimeSlotStatus.UNDESIRABLE;
                    } else {
                        result[i][j] = TimeSlotStatus.INDIFFERENT;
                    }
                }
            }
        }
        return result;
    }

    private Set<WeeklyTimeSlot> createTimeSlotSet(List<DtoWeeklyTimeSlot> timeSlots) {
        return timeSlots.stream()
                .map(this::createTimeSlot)
                .collect(Collectors.toUnmodifiableSet());
    }

    private WeeklyTimeSlot createTimeSlot(DtoWeeklyTimeSlot dto) {
       return new WeeklyTimeSlotImpl(dto.dayOfWeek, dto.from, dto.to);
    }

    private void loadAvailablePlacesForCourses(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector) {
        File file = _settings.coursePlacesFile;
        MyFileReader<DtoAvailablePlaces> reader = new MyJsonFileReader<>(DtoAvailablePlaces.class);
        try {
            var dto = reader.readFromFile(file);
            if(dto != null) {
                dto.availablePlaces
                        .forEach(dtoPlacesForObject -> {
                            String courseName = dtoPlacesForObject.object;
                            Course course = dataCollector.getCourse(courseName);
                            if(course != null) {
                                List<String> placeNames = dtoPlacesForObject.places;
                                Stream<Place> placeStream = placeNames.stream()
                                        .map(dataCollector::getPlace)
                                                .filter(Objects::nonNull);
                                assignmentCollector.setAvailablePlacesForCourse(course, placeStream);
                            }
                        });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAvailablePlacesForTeachers(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector) {
        File file = _settings.teacherPlacesFile;
        MyFileReader<DtoAvailablePlaces> reader = new MyJsonFileReader<>(DtoAvailablePlaces.class);
        try {
            var dto = reader.readFromFile(file);
            if(dto != null) {
                dto.availablePlaces
                        .forEach(dtoPlacesForObject -> {
                            String teacherName = dtoPlacesForObject.object;
                            Teacher teacher = dataCollector.getTeacher(teacherName);
                            if(teacher != null) {
                                List<String> placeNames = dtoPlacesForObject.places;
                                Stream<Place> placeStream = placeNames.stream()
                                        .map(dataCollector::getPlace)
                                        .filter(Objects::nonNull);
                                ;
                                assignmentCollector.setAvailablePlacesForTeacher(teacher, placeStream);
                            }
                        });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAvailablePlacesForGroups(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector) {
        File file = _settings.groupPlacesFile;
        MyFileReader<DtoAvailablePlaces> reader = new MyJsonFileReader<>(DtoAvailablePlaces.class);
        try {
            var dto = reader.readFromFile(file);
            if(dto != null) {
                dto.availablePlaces
                        .forEach(dtoPlacesForObject -> {
                            String groupName = dtoPlacesForObject.object;
                            Group group = dataCollector.getGroup(groupName);
                            if(group != null) {
                                List<String> placeNames = dtoPlacesForObject.places;
                                Stream<Place> placeStream = placeNames.stream()
                                        .map(dataCollector::getPlace)
                                        .filter(Objects::nonNull);
                                assignmentCollector.setAvailablePlacesForGroup(group, placeStream);
                            }
                        });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void loadGroupCourseTeacherAssignments(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector) {
        File file = _settings.groupCourseTeacherFile;
        MyFileReader<DtoGroupCourseTeacherAssignments> reader = new MyJsonFileReader<>(DtoGroupCourseTeacherAssignments.class);
        try {
            var dto = reader.readFromFile(file);
            if(dto != null) {
                dto.list
                        .forEach(dtoGroupCourseTeacher -> addGroupCourseTeacherAssignments(dataCollector, assignmentCollector, dtoGroupCourseTeacher));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void addGroupCourseTeacherAssignments(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector, DtoGroupCourseTeacher dto) {
        Group group = dataCollector.getGroup(dto.groupName);
        Course course = dataCollector.getCourse(dto.courseName);
        Teacher teacher = dataCollector.getTeacher(dto.teacherName);
        if(group != null && course != null && teacher != null) {
            assignmentCollector.setAssignmentStatus(group, course, teacher, AssignmentStatus.ASSIGNED);
        }
    }

    private void loadCourseTeachers(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector) {
        File file = _settings.courseTeachersFile;
        MyFileReader<DtoCourseTeachersAssignments> reader = new MyJsonFileReader<>(DtoCourseTeachersAssignments.class);
        try {
            var dto = reader.readFromFile(file);
            if(dto != null) {
                dto.assignments
                        .forEach(dtoCourseTeachers -> addCourseTeachers(dataCollector, assignmentCollector, dtoCourseTeachers));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addCourseTeachers(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector, DtoCourseTeachers dtoCourseTeachers) {
        Course course = dataCollector.getCourse(dtoCourseTeachers.courseName);
        if(course == null) {
            return;
        }
        Stream<Teacher> teacherStream = dtoCourseTeachers.teacherNames
                .stream()
                .map(dataCollector::getTeacher)
                .filter(Objects::nonNull);
        assignmentCollector.setAvailableTeachersForCourse(course, teacherStream);
    }

    private void loadGroupCoursePrograms(final SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector) {
        File file = _settings.groupCourseProgramsFile;
        MyFileReader<DtoGroupCourseProgramAssignments> reader = new MyJsonFileReader<>(DtoGroupCourseProgramAssignments.class);
        try {
            var dto = reader.readFromFile(file);
            if(dto != null) {
                dto.groupProgram
                        .forEach(dtoGroupCourseProgram -> addGroupCourseProgram(dataCollector, assignmentCollector, dtoGroupCourseProgram));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addGroupCourseProgram(final SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector, DtoGroupCourseProgram dto) {
        Group group = dataCollector.getGroup(dto.groupName);
        CourseProgram courseProgram = dataCollector.getCourseProgram(dto.courseProgramName);
        if(group != null && courseProgram != null) {
            assignmentCollector.setCourseProgramForGroup(group, courseProgram);
        }
    }

    private void loadPlaces(SchoolDataCollector dataCollector) {
        MyFileReader<DtoPlaces> reader = new MyJsonFileReader<>(DtoPlaces.class);
        try {
            DtoPlaces dtoPlaces = reader.readFromFile(_settings.placesFile);
            if(dtoPlaces != null) {
                dtoPlaces.places
                        .stream()
                        .map(_objFactory::buildPlace)
                        .forEach(dataCollector::addPlace);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadGroups(SchoolDataCollector dataCollector) {
        MyFileReader<DtoGroups> reader = new MyJsonFileReader<>(DtoGroups.class);
        try {
            DtoGroups dtoGroups = reader.readFromFile(_settings.groupsFile);
            if(dtoGroups != null) {
                dtoGroups.groups
                        .stream()
                        .map(_objFactory::buildGroup)
                        .forEach(dataCollector::addGroup);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCoursePrograms(SchoolDataCollector dataCollector) {
        MyFileReader<DtoCoursePrograms> reader = new MyJsonFileReader<>(DtoCoursePrograms.class);
        try {
            DtoCoursePrograms dtoCoursePrograms = reader.readFromFile(_settings.courseProgramsFile);
            if(dtoCoursePrograms != null) {
                dtoCoursePrograms.programs
                        .stream()
                        .map(_objFactory::buildCourseProgram)
                        .forEach(dataCollector::addCourseProgram);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private void loadCourses(SchoolDataCollector dataCollector) {
        MyFileReader<DtoCourses> reader = new MyJsonFileReader<>(DtoCourses.class);
        try {
            DtoCourses dtoCourses = reader.readFromFile(_settings.coursesFile);
            if(dtoCourses != null) {
                dtoCourses.courses
                        .stream()
                        .map(_objFactory::buildCourse)
                        .forEach(dataCollector::addCourse);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTeachers(SchoolDataCollector dataCollector) {
        MyFileReader<DtoTeachers> teachersReader = new MyJsonFileReader<>(DtoTeachers.class);
        try {
            DtoTeachers dtoTeachers = teachersReader.readFromFile(_settings.teachersFile);
            if(dtoTeachers != null) {
                dtoTeachers.teachers
                        .stream()
                        .map(_objFactory::buildTeacher)
                        .forEach(dataCollector::addTeacher);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
