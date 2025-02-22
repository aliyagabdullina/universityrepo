package org.example;

import java.sql.*;

public class DataBaseInteractorImpl implements DataBaseInteractor {

    private static final String DB_URL = "jdbc:postgresql://82.97.244.207:5432/university";
    private static final String USER = "useruniversity";
    private static final String PASSWORD = "passworduniversity";

    public DataBaseInteractorImpl() {
        Connection conn = null;
        try {
            // Установка соединения с базой данных
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            //createSequence1(conn);
            // createSequence2(conn);

            // Создание таблицы для хранения информации об учителях
            createLessonsTimeTable(conn);
            createTimeslotTable(conn);

            createPlacesTable(conn);
            createTeachersTable(conn);
            createCourseTable(conn);
            createGroupsTable(conn);
            createProgramsTable(conn);
            createCourseTeacherTable(conn);
            createTeacherGroupTable(conn);
            createGroupProgramTable(conn);
            createCourseProgramTable(conn);
            createTeacherPlaceTable(conn);
            createGroupPlaceTable(conn);

            createGroupCourseProgramTable(conn);

/*
            String sql = "CREATE TABLE Roles (" +
                    "     id SERIAL PRIMARY KEY," +
                    "     name VARCHAR(255) NOT NULL" +
                    ");";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            sql = "CREATE TABLE Users (" +
                    "     id SERIAL PRIMARY KEY," +
                    "     username VARCHAR(255) NOT NULL," +
                    "     password VARCHAR(255) NOT NULL," +
                    "     role_id BIGINT," +
                    "     FOREIGN KEY (role_id) REFERENCES Roles(id)" +
                    ");";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
*/



            //TODO - delete timeslot
            //addTimeSlot(conn, 1, 1, Time.valueOf("09:00:00"), Time.valueOf("10:30:00"));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Закрытие соединения с базой данных
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createSequence1(Connection conn) {
        try {
            String sql = "CREATE SEQUENCE hibernate_sequence START 1;";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не получилось добавить последовательность", e);
        }
    }

    private void createSequence2(Connection conn) {
        try {
            String sql = "CREATE SEQUENCE from_twenty_numeration START 20;";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не получилось добавить последовательность", e);
        }
    }

    private void addTimeSlot(Connection conn, int timeSlotId, int lessonId, Time startTime, Time endTime) {
        try {
            // Вставляем запись в таблицу LessonsTime
            insertIntoLessonsTime(conn, lessonId, startTime, endTime);

            // Вставляем запись в таблицу Timeslot
            String sql = "INSERT INTO Timeslot (timeslot_id, lesson_id) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, timeSlotId);
                stmt.setInt(2, lessonId);
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении таймслота", e);
        }
    }

    // Метод для вставки записи в таблицу LessonsTime
    private void insertIntoLessonsTime(Connection conn, int lessonId, Time startTime, Time endTime) {
        String sql = "INSERT INTO LessonsTime (lesson_id, startTime, endTime) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lessonId);
            stmt.setTime(2, startTime);
            stmt.setTime(3, endTime);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении времени урока", e);
        }
    }

    private static void createGroupsTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS Groups (" +
                "    group_id INT PRIMARY KEY," +
                "    name VARCHAR(255)," +
                "    num_of_students INT," +
                "    place_id INT," +
                "    max_number_of_lessons INT," +
                "    timeslot_id INT," +
                "    FOREIGN KEY (place_id) REFERENCES Places(place_id)," +
                "    FOREIGN KEY (timeslot_id) REFERENCES Timeslot(timeslot_id)" +
                ");";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createCourseTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS Courses (" +
                "    course_id INT PRIMARY KEY," +
                "    name VARCHAR(255)," +
                "    num_of_lessons_per_week INT," +
                "    num_of_days_per_week INT," +
                "    max_lessons_per_day INT," +
                "    complexity VARCHAR(50)" +
                ");";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createPlacesTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS Places (" +
                "    place_id INT PRIMARY KEY," +
                "    name VARCHAR(255)," +
                "    capacity INT," +
                "    description TEXT" +
                ");";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createTeachersTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS Teachers (" +
                "    teacher_id INT PRIMARY KEY," +
                "    name VARCHAR(255)," +
                "    place_id INT," +
                "    max_number_of_lessons INT," +
                "    timeslot_id INT," +
                "    FOREIGN KEY (place_id) REFERENCES Places(place_id)," +
                "    FOREIGN KEY (timeslot_id) REFERENCES Timeslot(timeslot_id)" +
                ");";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createProgramsTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS Programs (" +
                "    program_id INT PRIMARY KEY," +
                "    name VARCHAR(255)," +
                "    hours INT," +
                "    complexity INT" +
                ");";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createCourseTeacherTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS CourseTeacher (" +
                "    course_id INT ," +
                "    teacher_id INT," +
                "    FOREIGN KEY (course_id) REFERENCES Courses(course_id)," +
                "    FOREIGN KEY (teacher_id) REFERENCES Teachers(teacher_id)," +
                "    PRIMARY KEY (course_id, teacher_id)" +
                ");";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTeacherGroupTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS TeacherGroup (" +
                "    teacher_id INT ," +
                "    group_id INT," +
                "    FOREIGN KEY (teacher_id) REFERENCES Teachers(teacher_id)," +
                "    FOREIGN KEY (group_id) REFERENCES Groups(group_id)," +
                "    PRIMARY KEY (teacher_id, group_id)" +
                ");";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createGroupPlaceTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS GroupPlace (" +
                "    group_id INT," +
                "    place_id INT," +
                "    FOREIGN KEY (place_id) REFERENCES Places(place_id)," +
                "    FOREIGN KEY (group_id) REFERENCES Groups(group_id)," +
                "    PRIMARY KEY (place_id, group_id)" +
                ");";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTeacherPlaceTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS TeacherPlace (" +
                "    teacher_id INT," +
                "    place_id INT," +
                "    FOREIGN KEY (place_id) REFERENCES Places(place_id)," +
                "    FOREIGN KEY (teacher_id) REFERENCES Teachers(teacher_id)," +
                "    PRIMARY KEY (place_id, teacher_id)" +
                ");";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createGroupProgramTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS GroupProgram (" +
                "    group_id INT," +
                "    program_id INT," +
                "    FOREIGN KEY (group_id) REFERENCES Groups(group_id)," +
                "    FOREIGN KEY (program_id) REFERENCES Programs(program_id)," +
                "    PRIMARY KEY (group_id, program_id)" +
                ");";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createCourseProgramTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS CourseProgram (" +
                "    course_id INT," +
                "    program_id INT," +
                "    FOREIGN KEY (course_id) REFERENCES Courses(course_id)," +
                "    FOREIGN KEY (program_id) REFERENCES Programs(program_id)," +
                "    PRIMARY KEY (course_id, program_id)" +
                ");";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createGroupCourseProgramTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS GroupCourseProgram (" +
                "    group_id INT," +
                "    course_id INT," +
                "    program_id INT," +
                "    FOREIGN KEY (group_id) REFERENCES Groups(group_id)," +
                "    FOREIGN KEY (course_id) REFERENCES Courses(course_id)," +
                "    FOREIGN KEY (program_id) REFERENCES Programs(program_id)," +
                "    PRIMARY KEY (group_id, course_id, program_id)" +
                ");";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createLessonsTimeTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS LessonsTime (" +
                "    lesson_id INT PRIMARY KEY," +
                "    startTime TIME," +
                "    endTime TIME" +
                ");";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTimeslotTable(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS Timeslot (" +
                "    timeslot_id INT PRIMARY KEY," +
                "    lesson_id INT," +
                "    FOREIGN KEY (lesson_id) REFERENCES LessonsTime(lesson_id)" +
                ");";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
