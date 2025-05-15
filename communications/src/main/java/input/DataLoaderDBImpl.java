package input;

import collector.SchoolDataCollector;
import constraint.assignment.AssignmentCollector;
import constraint.assignment.AssignmentStatus;
import constraint.timeConstraint.*;
import course.*;
import group.Group;
import group.GroupImpl;
import lesson.DtoLesson;
import lesson.Lesson;
import lesson.LessonImpl;
import lesson.LessonRequest;
import objectFactories.SchoolObjectsFactory;
import objectFactories.SchoolObjectsFactoryImpl;
import output.DtoFileSettings;
import person.Student;
import person.Teacher;
import person.TeacherImpl;
import place.Place;
import place.PlaceImpl;
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
import java.sql.*;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DataLoaderDBImpl implements DataLoader {

    private SchoolObjectsFactory _objFactory;

    private static final String DB_URL = "jdbc:postgresql://82.97.244.207:5432/school";
    private static final String USER = "userschool";
    private static final String PASSWORD = "passwordschool";
    private int university_id;

    final Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);

    public DataLoaderDBImpl(int university_id) throws SQLException {
        this.university_id = university_id;
    }

    @Override
    public void setSettings(DtoFileSettings settings) {

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

    @Override
    public SchedulingSetupActions loadSchedulingSetup(SchoolDataCollector dataCollector, File file, Map<Integer, LessonRequest> lessonRequestMap) {
        DtoScheduleSetupActions dto = loadSchedulingSetup(file);
        SchedulingSetupActions result = new SchedulingSetupActionsImpl();
        dto.scheduledLessons
                .stream()
                .map(dtoLesson -> createLesson(dataCollector, dtoLesson))
                .forEach(result::addScheduleLesson);
        int numForbiddenLessons = dto.forbiddenLessonRequestIds.size();
        for (int i = 0; i < numForbiddenLessons; i++) {
            //TODO THIS IS NOT CLEAR!! LESSON REQUEST IDS CAN BE CHANGED BY ANY MODIFICATION OF PROGRAM!!!!!!!!!!!!
            int lessonRequestId = dto.forbiddenLessonRequestIds.get(i);
            LessonRequest lessonRequest = lessonRequestMap.get(lessonRequestId);
            if (lessonRequest == null) {
                throw new IllegalStateException("Scheduling setup is not valid!");
            }
            var dtoTimeSlot = dto.forbiddenLessonRequestTimeSlots.get(i);
            WeeklyTimeSlot timeSlot = createTimeSlot(dtoTimeSlot);
            result.addForbidLessonRequestTimeSlot(lessonRequest, timeSlot);
        }
        return result;
    }


    private void loadGroupTimeSlots(Stream<Group> groups, WeeklyTimeSlot[][] timeSlots, TimeTablesCollector timeTablesCollector) {
        String sql = "SELECT g.name, ts.timeslot_id, t.day_of_week, t.status " +
                "FROM groups g " +
                "JOIN groupTimeslot ts ON g.group_id = ts.group_id " +
                "JOIN timeslot t ON ts.timeslot_id = t.timeslot_id " +
                "WHERE g.university_id = ?"; // Запрос для получения всех таймслотов для групп

        try (PreparedStatement statement = conn.prepareStatement(sql)){
             statement.setInt(1, university_id); // Устанавливаем university_id
             try(ResultSet resultSet = statement.executeQuery()) {

                 Map<String, List<TimeSlotStatus[]>> groupTimeSlotMap = new HashMap<>();

                 while (resultSet.next()) {
                     String groupName = resultSet.getString("name");
                     int timeslotId = resultSet.getInt("timeslot_id");
                     int dayOfWeek = resultSet.getInt("day_of_week");
                     String statusStr = resultSet.getString("status");

                     TimeSlotStatus status = TimeSlotStatus.valueOf(statusStr);

                     TimeSlotStatus[] timeslotStatusArray = new TimeSlotStatus[7]; // 7 дней недели
                     timeslotStatusArray[dayOfWeek - 1] = status; // Устанавливаем статус для конкретного дня недели

                     groupTimeSlotMap.computeIfAbsent(groupName, k -> new ArrayList<>()).add(timeslotStatusArray);
                 }

                 groups.forEach(group -> {
                     List<TimeSlotStatus[]> groupTimeSlots = groupTimeSlotMap.get(group.getName());

                     if (groupTimeSlots != null) {
                         TimeSlotStatus[][] availableStatuses = new TimeSlotStatus[7][groupTimeSlots.size()];
                         for (int i = 0; i < groupTimeSlots.size(); i++) {
                             availableStatuses[i] = groupTimeSlots.get(i);
                         }
                         timeTablesCollector.setGroupTimeTable(group, availableStatuses);
                     }
                 });
             }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading group time slots from database", e);
        }
    }



    private void loadGroupsTimeConstraints(Stream<Group> groupStream, TimeTablesCollector timeTablesCollector) {
        String sql = "SELECT g.name, g.max_number_of_lessons FROM groups g WHERE g.university_id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setInt(1, university_id); // Устанавливаем university_id
            try (ResultSet resultSet = statement.executeQuery()) {

                Map<String, Integer[]> groupMaxDailyLoadMap = new HashMap<>();
                Map<String, Integer> groupMaxDaysMap = new HashMap<>();

                while (resultSet.next()) {
                    String groupName = resultSet.getString("name");
                    int maxNumberOfLessons = resultSet.getInt("max_number_of_lessons");

                    Integer[] maxDailyLoad = new Integer[7];
                    Arrays.fill(maxDailyLoad, maxNumberOfLessons); // Заполняем массив значениями maxLessonsNum

                    // Записываем данные в карты
                    groupMaxDailyLoadMap.put(groupName, maxDailyLoad);
                    groupMaxDaysMap.put(groupName, 7); // Устанавливаем максимальное количество рабочих дней как 7
                }

                groupStream.forEach(group -> {
                    Integer[] maxDailyLoad = groupMaxDailyLoadMap.get(group.getName());
                    int maxDays = groupMaxDaysMap.getOrDefault(group.getName(), 7);

                    timeTablesCollector.setGroupNumWorkdaysUb(group, maxDays);
                    if (maxDailyLoad != null) {
                        timeTablesCollector.setGroupMaxDailyLoad(group, maxDailyLoad);
                    } else {
                        Integer[] defaultLoad = new Integer[7];
                        Arrays.fill(defaultLoad, 10); // 10 уроков на каждый день по умолчанию
                        timeTablesCollector.setGroupMaxDailyLoad(group, defaultLoad);
                    }
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading groups' time constraints from database", e);
        }
    }

    // Метод для получения статусов для нескольких timeslot_id
    private TimeSlotStatus[][] getTimeSlotStatusForGroupTimeslots(List<Integer> timeslotIds) {
        String sql = "SELECT day_of_week, status, timeslot_id FROM timeslot WHERE timeslot_id IN (?)";
        TimeSlotStatus[][] statusArray = new TimeSlotStatus[7][timeslotIds.size()]; // 7 дней недели, количество слотов в день зависит от количества timeslot_id

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            // Формируем строку для запроса с несколькими timeslot_id
            String timeslotIdsString = timeslotIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            statement.setString(1, timeslotIdsString);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int dayOfWeek = resultSet.getInt("day_of_week");
                    String statusStr = resultSet.getString("status");
                    int timeslotId = resultSet.getInt("timeslot_id");

                    // Преобразуем строку в TimeSlotStatus
                    TimeSlotStatus status = TimeSlotStatus.valueOf(statusStr);

                    // Добавляем статус в соответствующий день недели
                    int index = timeslotIds.indexOf(timeslotId); // Получаем индекс timeslot_id
                    statusArray[dayOfWeek - 1][index] = status; // day_of_week 1..7, но индексы массива с 0
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading timeslot status from database", e);
        }

        return statusArray;
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


    private DtoScheduleSetupActions loadSchedulingSetup(File file) {
        MyFileReader<DtoScheduleSetupActions> reader = new MyJsonFileReader<>(DtoScheduleSetupActions.class);
        try {
            return reader.readFromFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTeachersTimeConstraints(Stream<Teacher> teacherStream, TimeTablesCollector timeTablesCollector) {
        String sql = "SELECT t.name, t.max_number_of_lessons FROM teachers t WHERE t.university_id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setInt(1, university_id); // Устанавливаем university_id
            try (ResultSet resultSet = statement.executeQuery()) {

                // Храним максимальное количество уроков и их распределение по дням недели для каждого преподавателя
                Map<String, Integer[]> teacherMaxDailyLoadMap = new HashMap<>();
                Map<String, Integer> teacherMaxDaysMap = new HashMap<>();

                // Загружаем все данные для преподавателей
                while (resultSet.next()) {
                    String teacherName = resultSet.getString("name");
                    int maxLessonsNum = resultSet.getInt("max_number_of_lessons");

                    Integer[] maxDailyLoad = new Integer[7];
                    Arrays.fill(maxDailyLoad, maxLessonsNum); // Заполняем массив значениями maxLessonsNum

                    // Записываем данные в карты
                    teacherMaxDailyLoadMap.put(teacherName, maxDailyLoad);
                    teacherMaxDaysMap.put(teacherName, 7); // Устанавливаем максимальное количество рабочих дней как 7
                }

                // Применяем ограничения для каждого преподавателя
                teacherStream.forEach(teacher -> {
                    // Извлекаем данные для преподавателя из карт
                    Integer[] maxDailyLoad = teacherMaxDailyLoadMap.get(teacher.getName());
                    int maxDays = teacherMaxDaysMap.getOrDefault(teacher.getName(), 7);

                    timeTablesCollector.setTeacherNumWorkdaysUb(teacher, maxDays);
                    if (maxDailyLoad != null) {
                        timeTablesCollector.setTeacherMaxDailyLoad(teacher, maxDailyLoad);
                    } else {
                        Integer[] defaultLoad = new Integer[7];
                        Arrays.fill(defaultLoad, 10); // 10 уроков на каждый день по умолчанию
                        timeTablesCollector.setTeacherMaxDailyLoad(teacher, defaultLoad);
                    }
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading teachers' time constraints from database", e);
        }
    }




    private void loadTeacherTimeSlots(Stream<Teacher> teachers, final WeeklyTimeSlot[][] timeSlots, TimeTablesCollector timeTablesCollector) {
        String sql = "SELECT t.name, ts.timeslot_id, s.day_of_week, s.status " +
                "FROM teachers t " +
                "JOIN teacherTimeslot ts ON t.teacher_id = ts.teacher_id " +
                "JOIN timeslot s ON ts.timeslot_id = s.timeslot_id " +
                "WHERE t.university_id = ?"; // Запрос для получения всех таймслотов для преподавателей

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, university_id); // Устанавливаем university_id
            try (ResultSet resultSet = statement.executeQuery()) {

                Map<String, List<TimeSlotStatus[]>> teacherTimeSlotMap = new HashMap<>();

                while (resultSet.next()) {
                    String teacherName = resultSet.getString("name");
                    int timeslotId = resultSet.getInt("timeslot_id");
                    int dayOfWeek = resultSet.getInt("day_of_week");
                    String statusStr = resultSet.getString("status");

                    TimeSlotStatus status = TimeSlotStatus.valueOf(statusStr);

                    TimeSlotStatus[] timeslotStatusArray = new TimeSlotStatus[7]; // 7 дней недели
                    timeslotStatusArray[dayOfWeek - 1] = status; // Устанавливаем статус для конкретного дня недели

                    teacherTimeSlotMap.computeIfAbsent(teacherName, k -> new ArrayList<>()).add(timeslotStatusArray);
                }

                teachers.forEach(teacher -> {
                    List<TimeSlotStatus[]> teacherTimeSlots = teacherTimeSlotMap.get(teacher.getName());

                    // Если таймслоты найдены, передаем их в TimeTablesCollector
                    if (teacherTimeSlots != null) {
                        TimeSlotStatus[][] availableStatuses = new TimeSlotStatus[7][teacherTimeSlots.size()];
                        for (int i = 0; i < teacherTimeSlots.size(); i++) {
                            availableStatuses[i] = teacherTimeSlots.get(i);
                        }
                        timeTablesCollector.setTeacherTimeTable(teacher, availableStatuses);
                    }
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading teacher time slots from database", e);
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
                if (timeSlot == null) {
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
        try {
            String sql = "SELECT cp.course_id, c.name AS course_name, p.name AS place_name " +
                    "FROM CoursePlace cp " +
                    "INNER JOIN Courses c ON cp.course_id = c.course_id " + // Присоединяем таблицу Courses для получения course_name
                    "INNER JOIN Places p ON cp.place_id = p.place_id " +
                    "WHERE c.university_id = ? " +
                    "AND p.university_id = ?";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, university_id);
                statement.setInt(2, university_id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String courseName = resultSet.getString("course_name");
                        String placeName = resultSet.getString("place_name");

                        Course course = dataCollector.getCourse(courseName);
                        Place place = dataCollector.getPlace(placeName);
                        if (course != null && place != null) {
                            assignmentCollector.addAvailablePlaceForCourse(course, place);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading available places for courses from database", e);
        }
    }


    private void loadAvailablePlacesForTeachers(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector) {
        try {
            String sql = "SELECT tp.teacher_id, t.name AS teacher_name, p.name AS place_name " +
                    "FROM TeacherPlace tp " +
                    "INNER JOIN Teachers t ON tp.teacher_id = t.teacher_id " +
                    "INNER JOIN Places p ON tp.place_id = p.place_id " +
                    "WHERE t.university_id = ? " +
                    "AND p.university_id = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, university_id);
                statement.setInt(2, university_id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String teacherName = resultSet.getString("teacher_name");
                        String placeName = resultSet.getString("place_name");

                        Teacher teacher = dataCollector.getTeacher(teacherName);
                        Place place = dataCollector.getPlace(placeName);
                        if (teacher != null && place != null) {
                            assignmentCollector.addAvailablePlaceForTeacher(teacher, place);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading available places for teachers from database", e);
        }
    }


    private void loadAvailablePlacesForGroups(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector) {
        try {
            String sql = "SELECT gp.group_id, g.name AS group_name, p.name AS place_name " +
                    "FROM GroupPlace gp " +
                    "INNER JOIN Groups g ON gp.group_id = g.group_id " + // Присоединяем таблицу Groups для получения group_name
                    "INNER JOIN Places p ON gp.place_id = p.place_id " +
                    "WHERE g.university_id = ? " +
                    "AND p.university_id = ?";


            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, university_id);
                statement.setInt(2, university_id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String groupName = resultSet.getString("group_name");
                        String placeName = resultSet.getString("place_name");

                        Group group = dataCollector.getGroup(groupName);
                        Place place = dataCollector.getPlace(placeName);
                        if (group != null && place != null) {
                            assignmentCollector.addAvailablePlaceForGroup(group, place);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading available places for groups from database", e);
        }
    }


    private void loadGroupCourseTeacherAssignments(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector) {
        try {
            String sql = "SELECT g.name AS group_name, c.name AS course_name, t.name AS teacher_name " +
                    "FROM GroupCourseTeacher gct " +
                    "INNER JOIN Groups g ON gct.group_id = g.group_id " +
                    "INNER JOIN Courses c ON gct.course_id = c.course_id " +
                    "INNER JOIN Teachers t ON gct.teacher_id = t.teacher_id " +
                    "WHERE g.university_id = ? "+
                    "AND c.university_id = ? "+
                    "AND t.university_id = ?";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, university_id);
                statement.setInt(2, university_id);
                statement.setInt(3, university_id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String groupName = resultSet.getString("group_name");
                        String courseName = resultSet.getString("course_name");
                        String teacherName = resultSet.getString("teacher_name");

                        Group group = dataCollector.getGroup(groupName);
                        Course course = dataCollector.getCourse(courseName);
                        Teacher teacher = dataCollector.getTeacher(teacherName);
                        if (group != null && course != null && teacher != null) {
                            assignmentCollector.setAssignmentStatus(group, course, teacher, AssignmentStatus.ASSIGNED);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading group course teacher assignments from database", e);
        }
    }

    private void loadGroupCoursePrograms(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector) {
        try {
            String sql = "SELECT gp.group_id, gp.course_id, gp.program_id, g.name AS group_name, c.name AS course_name, p.name AS program_name, g.num_of_students " +
                    "FROM GroupCourseProgram gp " +
                    "INNER JOIN Groups g ON gp.group_id = g.group_id " +
                    "INNER JOIN Courses c ON gp.course_id = c.course_id " +
                    "INNER JOIN Programs p ON gp.program_id = p.program_id " +
                    "WHERE g.university_id = ? " +
                    "AND c.university_id = ? " +
                    "AND p.university_id = ?";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, university_id);
                statement.setInt(2, university_id);
                statement.setInt(3, university_id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String groupName = resultSet.getString("group_name");
                        int numStudents = resultSet.getInt("num_of_students");
                        String courseName = resultSet.getString("course_name");
                        String programName = resultSet.getString("program_name");

                        Set<Student> students = new HashSet<>(numStudents);
                        Group group = new GroupImpl(groupName, students);
                        Course course = dataCollector.getCourse(courseName);

                        CourseProgram courseProgram = dataCollector.getCourseProgram(programName);
                        assignmentCollector.setCourseProgramForGroup(group, courseProgram);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading group course programs from database", e);
        }
    }


    private void loadCourseTeachers(SchoolDataCollector dataCollector, AssignmentCollector assignmentCollector) {
        try {
            String sql = "SELECT ct.course_id, ct.teacher_id, c.name AS course_name, t.name AS teacher_name " +
                    "FROM CourseTeacher ct " +
                    "INNER JOIN Courses c ON ct.course_id = c.course_id " +
                    "INNER JOIN Teachers t ON ct.teacher_id = t.teacher_id " +
                    "WHERE c.university_id = ? " +
                    "AND t.university_id = ?";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, university_id);
                statement.setInt(2, university_id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Обрабатываем каждую запись из результата
                    while (resultSet.next()) {
                        String courseName = resultSet.getString("course_name");
                        String teacherName = resultSet.getString("teacher_name");

                        Course course = new CourseImpl(courseName);
                        Teacher teacher = new TeacherImpl(teacherName);

                        assignmentCollector.addTeacherForCourse(course, teacher);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding course teachers from database", e);
        }
    }


    private void loadPlaces(SchoolDataCollector dataCollector) {
        try {
            // Создаем SQL запрос для выборки всех записей из таблицы Places
            String sql = "SELECT * FROM Places WHERE university_id = ?";

            // Подготавливаем запрос
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, university_id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Обрабатываем каждую запись из результата
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        Place place = new PlaceImpl(name);
                        dataCollector.addPlace(place);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading places from database", e);
        }
    }

    private void loadGroups(SchoolDataCollector dataCollector) {
        try {
            // Создаем SQL запрос для выборки всех записей из таблицы Places
            String sql = "SELECT * FROM Groups WHERE university_id = ?";

            // Подготавливаем запрос
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, university_id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Обрабатываем каждую запись из результата
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        int numOfStudents = resultSet.getInt("num_of_students");
                        Set<Student> students = new HashSet<>(numOfStudents);
                        Group group = new GroupImpl(name, students);
                        dataCollector.addGroup(group);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading groups from database", e);
        }
    }


    private void loadCourses(SchoolDataCollector dataCollector) {
        try {
            // Создаем SQL запрос для выборки всех записей из таблицы Places
            String sql = "SELECT * FROM Courses WHERE university_id = ?";

            // Подготавливаем запрос
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, university_id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Обрабатываем каждую запись из результата
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        Course course = new CourseImpl(name);
                        dataCollector.addCourse(course);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading courses from database", e);
        }
    }

    private void loadTeachers(SchoolDataCollector dataCollector) {
        try {
            // Создаем SQL запрос для выборки всех записей из таблицы Places
            String sql = "SELECT * FROM Teachers WHERE university_id = ?";

            // Подготавливаем запрос
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, university_id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Обрабатываем каждую запись из результата
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");

                        Teacher teacher = new TeacherImpl(name);
                        dataCollector.addTeacher(teacher);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading teachers from database", e);
        }
    }

    private void loadCoursePrograms(SchoolDataCollector dataCollector) {
        try {
            String sql =
                    """
                    SELECT c.course_id, c.name as courseName, c.num_of_lessons_per_week, c.num_of_days_per_week, c.max_lessons_per_day, p.program_id, p.name as programName
                    FROM Courses c
                    INNER JOIN CourseProgram cp ON c.course_id = cp.course_id
                    INNER JOIN Programs p ON cp.program_id = p.program_id
                    WHERE p.university_id = ?
                    """;

            // Подготавливаем запрос
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, university_id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Обрабатываем каждую запись из результата
                    while (resultSet.next()) {
                        String courseName = resultSet.getString("courseName");
                        String programName = resultSet.getString("programName");
                        int num_of_lessons_per_week = resultSet.getInt("num_of_lessons_per_week");
                        int num_of_days_per_week = resultSet.getInt("num_of_days_per_week");
                        int max_lessons_per_day = resultSet.getInt("max_lessons_per_day");
                        String prCourseName = programName;
                        // Создаем объект CourseProgramImpl и добавляем его в коллектор
                        CourseProgram courseProgram = dataCollector.getCourseProgram(prCourseName) == null ? new CourseProgramImpl(prCourseName) : dataCollector.getCourseProgram(prCourseName);
                        Course course = new CourseImpl(courseName);
                        CourseInProgram courseInProgram = new CourseInProgramImpl(Stream.of(course));
                        courseInProgram.setComplexity(10);
                        courseInProgram.setMaxLessonsPerDay(max_lessons_per_day);
                        courseInProgram.setCourseDaysPerWeek(num_of_days_per_week);
                        courseInProgram.setLessonsPerWeek(num_of_lessons_per_week);

                        courseProgram.addCourse(courseInProgram);
                        dataCollector.addCourseProgram(courseProgram);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading programs from database", e);
        }
    }


}
