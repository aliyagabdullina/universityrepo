package org.example.controllers;


import algorithms.GlobalScheduler;
import collector.SchoolDataCollector;
import constraint.OptionalConstraintsSettings;
import constraint.OptionalConstraintsSettingsImpl;
import constraint.OptionalSchedulingConstraint;
import constraint.assignment.AssignmentCollector;
import constraint.assignment.AssignmentCollectorImpl;
import constraint.timeConstraint.*;
import group.Group;
import input.*;
import lesson.Lesson;
import lesson.LessonRequest;
import lesson.LessonRequestsBuilder;
import lesson.LessonRequestsBuilderImpl;
import lombok.RequiredArgsConstructor;
import org.example.DataBaseInteractor;
import org.example.DataBaseInteractorImpl;
import org.example.SchoolDataCollectorImpl;
import org.example.data.ScheduleDto;
import org.example.data.ScheduleEntry;
import org.example.repositories.GroupsRepository;
import org.example.repositories.PlacesRepository;
import org.example.repositories.TeacherRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import output.DtoFileSettings;
import pair.Pair;
import person.Student;
import person.Teacher;
import place.Place;
import programSettings.VocabularyFiles;
import programSettings.VocabularyFilesImpl;
import schedule.Schedule;
import scheduleBuilder.ScheduleBuilder;
import scheduleBuilder.ScheduleBuilderImpl;
import scheduleBuilder.SchedulingInputData;
import scheduleBuilder.SchedulingInputDataImpl;
import scheduleBuilder.data.ScheduleConstraintsAccumulator;
import scheduleBuilder.data.ScheduleConstraintsAccumulatorImpl;
import scheduleBuilder.data.ScheduleObjectiveAccumulator;
import scheduleBuilder.data.ScheduleObjectiveAccumulatorImpl;
import scheduleBuilder.engines.settings.DtoScheduleBuilderSettings;
import table.Table;
import table.TableImpl;
import time.WeeklyTimeSlot;
import time.WeeklyTimeSlotImpl;

import java.io.File;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@Controller
@RequiredArgsConstructor
public class ScheduleController {
    private static final VocabularyFiles _vocabularyFiles = new VocabularyFilesImpl();
    private final File root = new File("/Users/aliya/Documents/Курсач/School Scheduling/school10-11");

    private AssignmentCollector _assignmentCollector;
    private SchoolDataCollector _dataCollector;
    private TimeTablesCollector _timeTablesCollector;
    private WeeklyTimeSlot[][] _timeSlots;
    private DtoScheduleBuilderSettings _scheduleBuilderSettings;
    private SchedulingInputData _schedulingInputData;
    private DtoFileSettings _fileSettings;
    private DataLoader _dataLoader;

    private final PlacesRepository placeRepository;
    private final GroupsRepository groupsRepository;
    private final TeacherRepository teacherRepository;

    private int universityId = 1;


    private Schedule _result;

    @GetMapping("/schedule")
    public String schedule(Model model) {
        _fileSettings = initializeSettings(root);
        _dataLoader = new DataLoaderImpl(_fileSettings);
        _dataCollector = loadData();
        _timeSlots = loadTimeSlots();
        _timeTablesCollector = loadCollector();
        _assignmentCollector = loadAssignmentsCollector();
        _scheduleBuilderSettings = new DtoScheduleBuilderSettings();

        ScheduleBuilder scheduleBuilder = new ScheduleBuilderImpl();
        scheduleBuilder.setSchoolDataCollector(_dataCollector);

        List<WeeklyTimeSlot> timeSlotSequence = createTimeSlotSequence(_timeSlots);
        scheduleBuilder.setTimeSlotSequence(timeSlotSequence);

        SchedulingInputData scheduleInputData = _schedulingInputData != null ? _schedulingInputData
                : createSchedulingInputData();
        scheduleBuilder.setScheduleInputData(scheduleInputData);

        ScheduleConstraintsAccumulator constraintAccumulator = initializeScheduleConstraints();
        scheduleBuilder.setConstraintAccumulator(constraintAccumulator);

        ScheduleObjectiveAccumulator objectiveAccumulator = new ScheduleObjectiveAccumulatorImpl();
        scheduleBuilder.setObjectiveAccumulator(objectiveAccumulator);

        scheduleBuilder.setSettings(_scheduleBuilderSettings);

        _result = scheduleBuilder.solve();
        System.out.println(_result);
        System.out.println(_result.getAllLessons().map(Lesson::getTimeSlot).map(WeeklyTimeSlot::getDayOfWeek).toList());
        ScheduleDto sch = new ScheduleDto(_result);
        model.addAttribute("schedule", sch);
        return "schedule";
    }

    @GetMapping("/api/schedule")
    @ResponseBody
    public Map<String, Object> getSchedule() throws SQLException {
        //_fileSettings = initializeSettings(root);
        _dataLoader = new DataLoaderDBImpl(universityId);
        _dataCollector = loadDataDB();

        List<Teacher> teachers = _dataCollector.getTeachers()
                .toList();
        List<Student> students = _dataCollector.getStudents()
                .toList();
        List<Group> groups = _dataCollector.getGroups()
                .toList();
        List<Place> places = _dataCollector.getPlaces()
                .toList();

        Map<String, Object> data = new HashMap<>();
        data.put("teachers", teachers);
        data.put("students", students);
        data.put("groups", groups);
        data.put("places", places);

        Map<String, List<ScheduleEntry>> groupScheduleMap = getGroupScheduleMap();
        data.put("groupScheduleMap", groupScheduleMap);
        Map<String, List<ScheduleEntry>> teacherScheduleMap = getTeacherScheduleMap();
        data.put("teacherScheduleMap", teacherScheduleMap);
        //Map<String, List<ScheduleEntry>> studentScheduleMap = getStudentScheduleMap();
        //data.put("studentScheduleMap", studentScheduleMap);
        Map<String, List<ScheduleEntry>> placeScheduleMap = getPlaceScheduleMap();
        data.put("placeScheduleMap", placeScheduleMap);

        return data;
    }


    @GetMapping("/schedule2")
    public String schedule2() throws SQLException {
        _dataLoader = new DataLoaderDBImpl(universityId);
        _dataCollector = loadDataDB();
        _timeSlots = loadTimeSlots();
        _timeTablesCollector = loadCollector();
        _assignmentCollector = loadAssignmentsCollector();
        _scheduleBuilderSettings = new DtoScheduleBuilderSettings();

        ScheduleBuilder scheduleBuilder = new ScheduleBuilderImpl();
        scheduleBuilder.setSchoolDataCollector(_dataCollector);

        List<WeeklyTimeSlot> timeSlotSequence = createTimeSlotSequence(_timeSlots);
        scheduleBuilder.setTimeSlotSequence(timeSlotSequence);

        SchedulingInputData scheduleInputData = _schedulingInputData != null ? _schedulingInputData
                : createSchedulingInputData();
        scheduleBuilder.setScheduleInputData(scheduleInputData);

        ScheduleConstraintsAccumulator constraintAccumulator = initializeScheduleConstraints();
        scheduleBuilder.setConstraintAccumulator(constraintAccumulator);

        ScheduleObjectiveAccumulator objectiveAccumulator = new ScheduleObjectiveAccumulatorImpl();
        scheduleBuilder.setObjectiveAccumulator(objectiveAccumulator);

        scheduleBuilder.setSettings(_scheduleBuilderSettings);

        GlobalScheduler globalScheduler = new GlobalScheduler( _dataCollector, timeSlotSequence,
                scheduleInputData, constraintAccumulator, objectiveAccumulator, _scheduleBuilderSettings);
        _result = globalScheduler.generateSchedule();

        return "schedule";
    }

    private String getTeacherNames(Lesson lesson) {
        return lesson.getTeachers().map(Teacher::getName).collect(Collectors.joining(", "));
    }
    private Map<String, List<ScheduleEntry>> getGroupScheduleMap() {
        // Собираем список всех уроков
        List<Lesson> lessons = _result.getAllLessons().toList();

        // Группируем уроки по группам (group)
        Map<String, List<ScheduleEntry>> groupScheduleMap = lessons.stream()
                .collect(Collectors.groupingBy(
                        lesson -> lesson.getGroup().getName(), // Группируем по имени группы
                        Collectors.mapping(lesson -> new ScheduleEntry(
                                lesson.getCourse().getName(),
                                getTeacherNames(lesson),
                                lesson.getPlace().getName(),
                                lesson.getTimeSlot().getDayOfWeek().name(),
                                getLessonNumberForTimeSlot(lesson.getTimeSlot())
                        ), Collectors.toList())
                ));
        return groupScheduleMap;
    }
    private Map<String, List<ScheduleEntry>> getTeacherScheduleMap() {
        // Собираем список всех уроков
        List<Lesson> lessons = _result.getAllLessons().toList();
        // Группируем уроки по учителям
        Map<String, List<ScheduleEntry>> teacherScheduleMap = new HashMap<>();

        for (Lesson lesson : lessons) {
            // Для каждого урока проходим по всем учителям
            for (Teacher teacher : lesson.getTeachers().toList()) {
                // Создаём ScheduleEntry для этого урока
                ScheduleEntry entry = new ScheduleEntry(
                        lesson.getCourse().getName(),
                        getTeacherNames(lesson), // Список учителей
                        lesson.getPlace().getName(),
                        lesson.getTimeSlot().getDayOfWeek().name(),
                        getLessonNumberForTimeSlot(lesson.getTimeSlot())
                );

                // Если этого учителя ещё нет в мапе, создаём пустой список
                teacherScheduleMap.computeIfAbsent(teacher.getName(), k -> new ArrayList<>()).add(entry);
            }
        }
        return teacherScheduleMap;
    }

    private Map<String, List<ScheduleEntry>> getPlaceScheduleMap() {
        // Собираем список всех уроков
        List<Lesson> lessons = _result.getAllLessons().toList();

        // Группируем уроки по местам (кабинетам)
        Map<String, List<ScheduleEntry>> placeScheduleMap = lessons.stream()
                .collect(Collectors.groupingBy(
                        lesson -> lesson.getPlace().getName(), // Группируем по имени кабинета
                        Collectors.mapping(lesson -> new ScheduleEntry(
                                lesson.getCourse().getName(),
                                getTeacherNames(lesson),
                                lesson.getPlace().getName(),
                                lesson.getTimeSlot().getDayOfWeek().name(),
                                getLessonNumberForTimeSlot(lesson.getTimeSlot())
                        ), Collectors.toList())
                ));
        return placeScheduleMap;
    }

    private Lesson getLessonForTeacher(Teacher teacher, List<Lesson> lessons) {
        return lessons.stream()
                .filter(lesson -> lesson.getTeachers().anyMatch(t -> t.equals(teacher)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Lesson not found for teacher: " + teacher.getName()));
    }


    private int getLessonNumberForTimeSlot(WeeklyTimeSlot timeSlot) {
        DayOfWeek dayOfWeek = timeSlot.getDayOfWeek();

        for (int lesson = 0; lesson < _timeSlots.length; lesson++) {
            if (_timeSlots[lesson][castDayToInt(dayOfWeek)].equals(timeSlot)) {
                return lesson + 1;
            }
        }

        throw new IllegalArgumentException("Таймслот не найден для дня недели " + dayOfWeek);
    }

    @GetMapping("/schedule/place")
    public String schedulePlace(Model model) {
        Stream<Lesson> lessons = _result.getAllLessons();

        String place = "17";
        Map<DayOfWeek, List<Lesson>> lessonsByDay = groupLessonsByDayOfWeekPlace(place, lessons);

        String[][] array = new String[8][7];
        for (int i = 0; i < 8; i++) {
            array[i][0] = String.valueOf(i + 1);
        }
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            List<Lesson> lessonsForDay = lessonsByDay.getOrDefault(dayOfWeek, Collections.emptyList());
            lessonsForDay.forEach(lesson -> {
                array[(int) (lesson.getTimeSlot().getStartTimeInMs() / 3600000) - 8][castDayToInt(dayOfWeek) + 1] = lesson.getCourse().toString() + "\n" + lesson.getTeachers().toList().get(0).toString();
            });
        }


        model.addAttribute("array", array);
        model.addAttribute("groups", groupsRepository.findAll());
        model.addAttribute("places", placeRepository.findAll());
        model.addAttribute("teachers", teacherRepository.findAll());
        return "schedule-place";
    }

    @GetMapping("/schedule/group/{groupName}")
    public String scheduleGroup(@PathVariable String groupName, Model model) {
        Stream<Lesson> lessons = _result.getAllLessons();

        Map<DayOfWeek, List<Lesson>> lessonsByDay = groupLessonsByDayOfWeekGroup(groupName, lessons);

        String[][] array = new String[8][7];
        for (int i = 0; i < 8; i++) {
            array[i][0] = String.valueOf(i + 1);
        }
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            List<Lesson> lessonsForDay = lessonsByDay.getOrDefault(dayOfWeek, Collections.emptyList());
            lessonsForDay.forEach(lesson -> {
                array[(int) (lesson.getTimeSlot().getStartTimeInMs() / 3600000) - 8][castDayToInt(dayOfWeek) + 1] = lesson.getPlace().toString() + "\n" + lesson.getCourse().toString();
            });
        }


        model.addAttribute("array", array);
        model.addAttribute("groupName", groupName);
        model.addAttribute("groups", groupsRepository.findAll());
        model.addAttribute("places", placeRepository.findAll());
        model.addAttribute("teachers", teacherRepository.findAll());
        return "schedule-group";
    }

    @GetMapping("/schedule/teacher")
    public String scheduleTeacher(Model model) {
        Stream<Lesson> lessons = _result.getAllLessons();

        String teacher = "Федосеева Эльвира Евгеньевна";
        Map<DayOfWeek, List<Lesson>> lessonsByDay = groupLessonsByDayOfWeekTeacher(teacher, lessons);

        String[][] array = new String[8][7];
        for (int i = 0; i < 8; i++) {
            array[i][0] = String.valueOf(i + 1);
        }
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            List<Lesson> lessonsForDay = lessonsByDay.getOrDefault(dayOfWeek, Collections.emptyList());
            lessonsForDay.forEach(lesson -> {
                array[(int) (lesson.getTimeSlot().getStartTimeInMs() / 3600000) - 8][castDayToInt(dayOfWeek) + 1] = lesson.getGroup().toString() + "\n" + lesson.getPlace().toString() + "\n" + lesson.getCourse().toString();
            });
        }


        model.addAttribute("array", array);
        model.addAttribute("groups", groupsRepository.findAll());
        model.addAttribute("places", placeRepository.findAll());
        model.addAttribute("teachers", teacherRepository.findAll());
        return "schedule-teacher";
    }

    private int castDayToInt(DayOfWeek dayOfWeek) {
        if (dayOfWeek.equals(DayOfWeek.MONDAY)) {

            return 0;
        }
        if (dayOfWeek.equals(DayOfWeek.TUESDAY)) {
            return 1;
        }
        if (dayOfWeek.equals(DayOfWeek.WEDNESDAY)) {
            return 2;
        }
        if (dayOfWeek.equals(DayOfWeek.THURSDAY)) {
            return 3;
        }
        if (dayOfWeek.equals(DayOfWeek.FRIDAY)) {
            return 4;
        }
        if (dayOfWeek.equals(DayOfWeek.SATURDAY)) {
            return 5;
        }
        return 6;
    }

    public static Map<DayOfWeek, List<Lesson>> groupLessonsByDayOfWeekTeacher(String teacher, Stream<Lesson> lessons) {
        return lessons
                .filter(lesson -> lesson.getTeachers().anyMatch(t -> t.getName().equals(teacher)))
                .collect(Collectors.groupingBy(lesson -> lesson.getTimeSlot().getDayOfWeek()));
    }

    public static Map<DayOfWeek, List<Lesson>> groupLessonsByDayOfWeekPlace(String place, Stream<Lesson> lessons) {
        return lessons
                .filter(lesson -> lesson.getPlace().getName().equals(place))
                .collect(Collectors.groupingBy(lesson -> lesson.getTimeSlot().getDayOfWeek()));
    }

    public static Map<DayOfWeek, List<Lesson>> groupLessonsByDayOfWeekGroup(String group, Stream<Lesson> lessons) {
        return lessons
                .filter(lesson -> lesson.getGroup().getName().equals(group))
                .collect(Collectors.groupingBy(lesson -> lesson.getTimeSlot().getDayOfWeek()));
    }

    private WeeklyTimeSlot[][] loadTimeSlots() {
        MyFileReader<Table<String, String>> reader = new MyFileReaderCsv();
        //Table<String, String> slots = reader.readFromFile(_fileSettings.timeSlotsFile);

        // Дни недели
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

        // Время
        String[][] timeSlots = {
                {"8:30 9:10", "8:30 9:10", "8:30 9:10", "8:30 9:10", "8:30 9:10", "8:30 9:10", "8:30 9:10"},
                {"9:20 10:00", "9:20 10:00", "9:20 10:00", "9:20 10:00", "9:20 10:00", "9:20 10:00", "9:20 10:00"},
                {"10:15 10:55", "10:15 10:55", "10:15 10:55", "10:15 10:55", "10:15 10:55", "10:15 10:55", "10:15 10:55"},
                {"11:10 11:50", "11:10 11:50", "11:10 11:50", "11:10 11:50", "11:10 11:50", "11:10 11:50", "11:10 11:50"},
                {"12:00 12:40", "12:00 12:40", "12:00 12:40", "12:00 12:40", "12:00 12:40", "12:00 12:40", "12:00 12:40"},
                {"12:50 13:30", "12:50 13:30", "12:50 13:30", "12:50 13:30", "12:50 13:30", "12:50 13:30", "12:50 13:30"},
                {"13:50 14:30", "13:50 14:30", "13:50 14:30", "13:50 14:30", "13:50 14:30", "13:50 14:30", "13:50 14:30"},
                {"14:40 15:20", "14:40 15:20", "14:40 15:20", "14:40 15:20", "14:40 15:20", "14:40 15:20", "14:40 15:20"},
                {"15:35 16:15", "15:35 16:15", "15:35 16:15", "15:35 16:15", "15:35 16:15", "15:35 16:15", "15:35 16:15"},
                {"16:30 17:10", "16:30 17:10", "16:30 17:10", "16:30 17:10", "16:30 17:10", "16:30 17:10", "16:30 17:10"},
                {"17:20 18:00", "17:20 18:00", "17:20 18:00", "17:20 18:00", "17:20 18:00", "17:20 18:00", "17:20 18:00"},
                {"18:05 18:45", "18:05 18:45", "18:05 18:45", "18:05 18:45", "18:05 18:45", "18:05 18:45", "18:05 18:45"}
        };

        Table<String, String> slots = new TableImpl<>(days, timeSlots);

        Table<DayOfWeek, WeeklyTimeSlot> result = createTimeSlotTable(slots);
        return result.getData();
    }

    private Table<DayOfWeek, WeeklyTimeSlot> createTimeSlotTable(Table<String, String> slots) {
        String[][] data = slots.getData();
        WeeklyTimeSlot[][] result = new WeeklyTimeSlot[slots.getDataRowsNum()][slots.getColumnsNum()];
        DayOfWeek[] daysOfWeek = DayOfWeek.values();
        for (int j = 0; j < slots.getColumnsNum(); j++) {
            DayOfWeek dayOfWeek = daysOfWeek[j];
            for (int i = 0; i < slots.getDataRowsNum(); i++) {
                result[i][j] = createTimeSlot(dayOfWeek, data[i][j]);
            }
        }
        return new TableImpl<>(daysOfWeek, result);
    }

    private WeeklyTimeSlot createTimeSlot(DayOfWeek dayOfWeek, String timeSlot) {
        if (timeSlot == null || timeSlot.isBlank() || timeSlot.equals("NA")) {
            return null;
        }
        String[] tokens = timeSlot.split("\\s+");
        long startMs = parseToMs(tokens[0]);
        long endMs = parseToMs(tokens[1]);
        return new WeeklyTimeSlotImpl(dayOfWeek, startMs, endMs);
    }

    private long parseToMs(String hh_mm) {
        String[] tokens = hh_mm.split("[-:]");
        int h = Integer.parseInt(tokens[0].trim());
        int m = Integer.parseInt(tokens[1].trim());
        return h * 3600000L + m * 60000L;
    }

    private AssignmentCollector loadAssignmentsCollector() {
        AssignmentCollector collector = new AssignmentCollectorImpl();
        _dataLoader.loadAssignmentData(_dataCollector, collector);
        return collector;
    }

    private TimeTablesCollector loadCollector() {
        TimeTablesCollector collector = new TimeTablesCollectorImpl();
        _dataLoader.loadTimeTables(_dataCollector, _timeSlots, collector);
        // load collector from files
        return collector;
    }

    private SchoolDataCollector loadData() {
        DataBaseInteractor dataBaseInteractor = new DataBaseInteractorImpl();
        SchoolDataCollector schoolDataCollector = new SchoolDataCollectorImpl(dataBaseInteractor);
        DataLoader dataLoader = new DataLoaderImpl(_fileSettings);
        dataLoader.loadSchoolData(schoolDataCollector);
        return schoolDataCollector;
    }

    private SchoolDataCollector loadDataDB() throws SQLException {
        DataBaseInteractor dataBaseInteractor = new DataBaseInteractorImpl();
        SchoolDataCollector schoolDataCollector = new SchoolDataCollectorImpl(dataBaseInteractor);
        DataLoader dataLoader = new DataLoaderDBImpl(universityId);
        dataLoader.loadSchoolData(schoolDataCollector);
        return schoolDataCollector;
    }

    private ScheduleConstraintsAccumulator initializeScheduleConstraints() {
        var result = new ScheduleConstraintsAccumulatorImpl();
        // add forbidden time slots constraints
        addTeachersForbiddenTimeSlotContraints(result);
        addTeacherMaxDaysConstraints(result);
        addTeacherDailyMaxLoadConstraints(result);

        addGroupsForbiddenTimeSlotContraints(result);
        addGroupsMandatoryTimeSlotConstraints(result);
        addGroupMaxDaysConstraints(result);
        addGroupDailyMaxLoadConstraints(result);

        return result;
    }

    private void addTeacherDailyMaxLoadConstraints(ScheduleConstraintsAccumulatorImpl result) {
        _timeTablesCollector.getTeacherMaxDailyLoadMap().forEach((teacher, arr) -> {
            var stream = IntStream.range(0, arr.length)
                    .boxed()
                    .map(i -> new Pair<>(DayOfWeek.values()[i], arr[i]));
            result.addTeacherDailyMaxLoad(teacher, stream);
        });
    }

    private void addGroupDailyMaxLoadConstraints(ScheduleConstraintsAccumulatorImpl result) {
        _timeTablesCollector.getGroupMaxDailyLoadMap().forEach((group, arr) -> {
            var stream = IntStream.range(0, arr.length)
                    .boxed()
                    .map(i -> new Pair<>(DayOfWeek.values()[i], arr[i]));
            result.addGroupDailyMaxLoad(group, stream);
        });
    }

    private void addGroupMaxDaysConstraints(ScheduleConstraintsAccumulatorImpl result) {
        _timeTablesCollector.getGroupMaxDaysMap().forEach(result::addGroupMaxDaysConstraint);
    }

    private void addGroupsMandatoryTimeSlotConstraints(ScheduleConstraintsAccumulatorImpl result) {
        _timeTablesCollector.getGroupTimeTable().entrySet()
                .stream()
                .map(entry -> createMandatoryTimeConstraint(entry.getValue()).map(tc -> new Pair<>(entry.getKey(), tc)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(pair -> result.addGroupMandatoryTimeConstraint(pair.getKey(), pair.getValue()));
    }

    private void addTeacherMaxDaysConstraints(ScheduleConstraintsAccumulatorImpl result) {
        _timeTablesCollector.getTeacherMaxDaysMap().forEach(result::addTeacherMaxDaysConstraint);
    }

    private void addTeachersForbiddenTimeSlotContraints(ScheduleConstraintsAccumulatorImpl result) {
        _timeTablesCollector.getTeacherTimeTable().entrySet()
                .stream()
                .map(entry -> createForbiddenTimeSlotConstraints(entry.getKey(), entry.getValue()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(result::addTeacherForbiddenTimeslotsConstraint);
    }

    private void addGroupsForbiddenTimeSlotContraints(ScheduleConstraintsAccumulatorImpl result) {
        _timeTablesCollector.getGroupTimeTable().entrySet()
                .stream()
                .map(entry -> createForbiddenTimeSlotConstraints(entry.getKey(), entry.getValue()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(result::addGroupForbiddenTimeslotsConstraint);
    }

    private <T> Optional<ForbiddenTimeConstraint<T>> createForbiddenTimeSlotConstraints(T obj, TimeSlotStatus[][] timeSlotStatuses) {

        int numRows = _timeSlots.length;
        int numCols = _timeSlots.length == 0 ? 0 : _timeSlots[0].length;
        List<WeeklyTimeSlot> forbiddenTs = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (timeSlotStatuses[i][j].equals(TimeSlotStatus.PROHIBITED)) {
                    forbiddenTs.add(_timeSlots[i][j]);
                }
            }
        }
        return forbiddenTs.isEmpty() ? Optional.empty() :
                Optional.of(new ForbiddenTimeConstraintImpl<>(obj, forbiddenTs));
    }

    private Optional<MandatoryTimeConstraint> createMandatoryTimeConstraint(TimeSlotStatus[][] timeSlotStatuses) {
        int numRows = _timeSlots.length;
        int numCols = _timeSlots.length == 0 ? 0 : _timeSlots[0].length;
        List<WeeklyTimeSlot> mandatoryTs = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (timeSlotStatuses[i][j].equals(TimeSlotStatus.PREFERRED)) {
                    mandatoryTs.add(_timeSlots[i][j]);
                }
            }
        }
        return mandatoryTs.isEmpty() ? Optional.empty() :
                Optional.of(new MandatoryTimeConstraintImpl(mandatoryTs.stream()));
    }

    private SchedulingInputData createSchedulingInputData() {
        OptionalConstraintsSettings optionalConstraintsSettings = createOptionalConstraintsSettings();
        List<WeeklyTimeSlot> timeSlotSequence = createTimeSlotSequence(_timeSlots);
        List<Teacher> teachers = _dataCollector.getTeachers().toList();
        List<Group> groups = _dataCollector.getGroups().toList();
        List<Place> places = _dataCollector.getPlaces().toList();
        List<LessonRequest> lessonRequests = createLessonRequests().toList();
        ScheduleConstraintsAccumulator constraintAccumulator = initializeScheduleConstraints();


        SchedulingInputData schedulingInputData = new SchedulingInputDataImpl(
                optionalConstraintsSettings,
                timeSlotSequence,
                teachers,
                lessonRequests,
                groups,
                places,
                constraintAccumulator
        );
        return schedulingInputData;
    }

    private Stream<LessonRequest> createLessonRequests() {
        LessonRequestsBuilder lrBuilder = new LessonRequestsBuilderImpl(_dataCollector, _assignmentCollector);
        return lrBuilder.getFeasibleLessonRequestsStream();
    }

    private OptionalConstraintsSettings createOptionalConstraintsSettings() {
        OptionalConstraintsSettings result = new OptionalConstraintsSettingsImpl();
        result.setHard(OptionalSchedulingConstraint.MAX_DAILY_LOAD_GROUP);
        result.setHard(OptionalSchedulingConstraint.MAX_DAYS_PER_TEACHER);
        result.setHard(OptionalSchedulingConstraint.LESSON_SAME_PLACES);
        return result;
    }

    private List<WeeklyTimeSlot> createTimeSlotSequence(WeeklyTimeSlot[][] timeSlots) {
        return Arrays.stream(timeSlots)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .sorted(WeeklyTimeSlot::compareTo)
                .toList();
    }

    private DtoFileSettings initializeSettings(File root) {
        DtoFileSettings settings = new DtoFileSettings();
        settings.dataDirectory = new File(root.getAbsolutePath() + "//" + _vocabularyFiles.getDataFolderName());
        String dataDirPath = settings.dataDirectory.getAbsolutePath();
        settings.teachersFile = new File(dataDirPath + "//" + _vocabularyFiles.getTeachersFileName());
        settings.coursesFile = new File(dataDirPath + "//" + _vocabularyFiles.getCoursesFileName());
        settings.groupsFile = new File(dataDirPath + "//" + _vocabularyFiles.getGroupsFileName());
        settings.placesFile = new File(dataDirPath + "//" + _vocabularyFiles.getPlacesFileName());
        settings.courseProgramsFile = new File(dataDirPath + "//" + _vocabularyFiles.getCourseProgramFileName());

        settings.schedulerDirectory = new File(root.getAbsolutePath() + "//" + _vocabularyFiles.getSchedulerFolderName());
        String schedDirPath = settings.schedulerDirectory.getAbsolutePath();
        settings.timeSlotsFile = new File(schedDirPath + "//" + _vocabularyFiles.getTimeSlotsFileName());
        settings.lessonRequestsFile = new File(schedDirPath + "//" + _vocabularyFiles.getLessonRequestsFileName());
        settings.groupCourseProgramsFile = new File(schedDirPath + "//" + _vocabularyFiles.getGroupCourseProgramsFileName());
        settings.courseTeachersFile = new File(schedDirPath + "//" + _vocabularyFiles.getCourseTeachersFileName());
        settings.groupCourseTeacherFile = new File(schedDirPath + "//" + _vocabularyFiles.getGroupCourseTeacherFileName());
        settings.coursePlacesFile = new File(schedDirPath + "//" + _vocabularyFiles.getCoursePlacesFileName());
        settings.groupPlacesFile = new File(schedDirPath + "//" + _vocabularyFiles.getGroupPlacesFileName());
        settings.teacherPlacesFile = new File(schedDirPath + "//" + _vocabularyFiles.getTeacherPlacesFileName());
        settings.teacherTimeSlotsFile = new File(schedDirPath + "//" + _vocabularyFiles.getTeacherTimeslotsFileName());
        settings.placeTimeSlotsFile = new File(schedDirPath + "//" + _vocabularyFiles.getPlaceTimeslotsFileName());
        settings.groupTimeSlotsFile = new File(schedDirPath + "//" + _vocabularyFiles.getGroupsTimeslotsFileName());
        settings.teachersTimeMetricConstraints = new File(schedDirPath + "//" + _vocabularyFiles.getTeacherTimeMetricConstraintsFileName());
        settings.groupsTimeMetricConstraints = new File(schedDirPath + "//" + _vocabularyFiles.getGroupTimeMetricConstraintsFileName());

        settings.schedulesDirectory = new File(root.getAbsolutePath() + "//" + _vocabularyFiles.getSchedulesFolderName());
        settings.setupsDirectory = new File(root.getAbsolutePath() + "//" + _vocabularyFiles.getSetupsFolderName());
        settings.scriptsDirectory = new File(root.getAbsolutePath() + "//" + _vocabularyFiles.getScriptsDirectoryName());
        settings.bufferDirectory = new File(root.getAbsolutePath() + "//" + _vocabularyFiles.getBufferDirectoryName());
        return settings;
    }

    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }
}
