package input;

import collector.SchoolDataCollector;
import constraint.assignment.AssignmentCollector;
import constraint.assignment.AssignmentStatus;
import constraint.timeConstraint.*;
import course.Course;
import course.CourseImpl;
import course.CourseProgram;
import course.CourseProgramImpl;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DataLoaderDBImpl implements DataLoader {
    private DtoFileSettings _settings;
    private SchoolObjectsFactory _objFactory;

    private static final String DB_URL = "jdbc:postgresql://82.97.244.207:5432/school";
    private static final String USER = "userschool";
    private static final String PASSWORD = "passwordschool";

    Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);

    public DataLoaderDBImpl(DtoFileSettings settings) throws SQLException {
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
        File file = _settings.groupTimeSlotsFile;
        MyFileReader<DtoTimeTables> reader = new MyJsonFileReader<>(DtoTimeTables.class);
        try {
            DtoTimeTables dto = reader.readFromFile(file);
            Map<String, DtoTimeTable> groupNameTimeTableMap = dto.timeTables
                    .stream()
                    .collect(Collectors.toConcurrentMap(timeTable -> timeTable.objName, timeTable -> timeTable));

            groups
                    .forEach(gro -> {
                        DtoTimeTable dtoTimeTable = groupNameTimeTableMap.getOrDefault(gro.getName(), new DtoTimeTable());
                        TimeSlotStatus[][] availableStatuses = createAvailableStatuses(timeSlots, dtoTimeTable);
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
                        Integer[] array = dtoTimeConstraint == null ? IntStream.range(0, 7).map(i -> 10).boxed().toArray(Integer[]::new) : dtoTimeConstraint.maxDailyLoad;
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
                        Integer[] array = dtoTeacherTimeConstraint == null ? IntStream.range(0, 7).map(i -> 10).boxed().toArray(Integer[]::new) : dtoTeacherTimeConstraint.maxDailyLoad;
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
                        DtoTimeTable dtoTimeTable = teacherNameTimeTableMap.getOrDefault(teacher.getName(), new DtoTimeTable());
                        TimeSlotStatus[][] availableStatuses = createAvailableStatuses(timeSlots, dtoTimeTable);
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
            String sql = "SELECT cp.object, p.place_name " +
                    "FROM CoursePlaces cp " +
                    "INNER JOIN Places p ON cp.place_id = p.place_id";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String courseName = resultSet.getString("object");
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
            String sql = "SELECT tp.object, p.place_name " +
                    "FROM TeacherPlaces tp " +
                    "INNER JOIN Places p ON tp.place_id = p.place_id";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String teacherName = resultSet.getString("object");
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
            String sql = "SELECT gp.object, p.place_name " +
                    "FROM GroupPlaces gp " +
                    "INNER JOIN Places p ON gp.place_id = p.place_id";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String groupName = resultSet.getString("object");
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
            String sql = "SELECT g.group_name, c.name AS course_name, t.name AS teacher_name " +
                    "FROM GroupCourseTeacher gct " +
                    "INNER JOIN Groups g ON gct.group_id = g.group_id " +
                    "INNER JOIN Courses c ON gct.course_id = c.course_id " +
                    "INNER JOIN Teachers t ON gct.teacher_id = t.teacher_id";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
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
            String sql = "SELECT gp.group_id, gp.course_id, gp.program_id, g.group_name, c.name AS course_name, p.name AS program_name, g.num_of_students " +
                    "FROM GroupCourseProgram gp " +
                    "INNER JOIN Groups g ON gp.group_id = g.group_id " +
                    "INNER JOIN Courses c ON gp.course_id = c.course_id " +
                    "INNER JOIN Programs p ON gp.program_id = p.program_id";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String groupName = resultSet.getString("group_name");
                        int numStudents = resultSet.getInt("num_of_students");
                        String courseName = resultSet.getString("course_name");
                        String programName = resultSet.getString("program_name");

                        Set<Student> students = new HashSet<>(numStudents);
                        Group group = new GroupImpl(groupName, students);
                        CourseProgram courseProgram = new CourseProgramImpl(courseName+programName);

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
                    "INNER JOIN Teachers t ON ct.teacher_id = t.teacher_id";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                // Выполняем запрос и получаем результат
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
            String sql = "SELECT * FROM Places";

            // Подготавливаем запрос
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                // Выполняем запрос и получаем результат
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
            String sql = "SELECT * FROM Groups";

            // Подготавливаем запрос
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                // Выполняем запрос и получаем результат
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
            String sql = "SELECT * FROM Courses";

            // Подготавливаем запрос
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                // Выполняем запрос и получаем результат
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
            String sql = "SELECT * FROM Teachers";

            // Подготавливаем запрос
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                // Выполняем запрос и получаем результат
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
            // Создаем SQL запрос для выборки всех записей из таблицы Places
            String sql = "SELECT * FROM Programs";

            // Подготавливаем запрос
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                // Выполняем запрос и получаем результат
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Обрабатываем каждую запись из результата
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        CourseProgram group = new CourseProgramImpl(name);
                        dataCollector.addCourseProgram(group);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading programs from database", e);
        }
    }


}
