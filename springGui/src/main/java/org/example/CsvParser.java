package org.example;

import org.example.data.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {

    public static List<CourseData> parseCourses(MultipartFile file) throws IOException {
        List<CourseData> courses = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            // Читаем заголовок
            String headerLine = reader.readLine();
            String[] headerFields = headerLine.split("[;,]");

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("[;,]");

                CourseData courseData = new CourseData();
                // Заполняем courseData
                courseData.setName(fields[0]);
                courseData.setNumOfLessonsPerWeek(Integer.parseInt(fields[1]));
                courseData.setNumOfDaysPerWeek(Integer.parseInt(fields[2]));
                courseData.setMaxLessonsPerDay(Integer.parseInt(fields[3]));
                courseData.setComplexity(fields[4]);

                courses.add(courseData);
            }
        }

        return courses;
    }

    public static List<GroupData> parseGroups(MultipartFile file) throws IOException {
        List<GroupData> groups = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            // Читаем заголовок
            String headerLine = reader.readLine();
            String[] headerFields = headerLine.split("[;,]");

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("[;,]");

                GroupData groupData = new GroupData();
                // Заполняем courseData
                groupData.setName(fields[0]);
                groupData.setNumOfStudents(Integer.parseInt(fields[1]));
                //groupData.setPlaceId(Integer.parseInt(fields[2]));
                groupData.setMaxNumberOfLessons(Integer.parseInt(fields[3]));

                groups.add(groupData);
            }
        }

        return groups;
    }

    public static List<PlaceData> parsePlaces(MultipartFile file) throws IOException {
        List<PlaceData> places = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            // Читаем заголовок
            String headerLine = reader.readLine();
            String[] headerFields = headerLine.split("[;,]");

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("[;,]");

                PlaceData placeData = new PlaceData();
                // Заполняем courseData
                placeData.setName(fields[0]);
                placeData.setCapacity(Integer.parseInt(fields[1]));
                placeData.setDescription(fields[2]);

                places.add(placeData);
            }
        }

        return places;
    }


    public static List<ProgramData> parsePrograms(MultipartFile file) throws IOException {
        List<ProgramData> programs = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            // Читаем заголовок
            String headerLine = reader.readLine();
            String[] headerFields = headerLine.split("[;,]");

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("[;,]");

                ProgramData programData = new ProgramData();
                // Заполняем courseData
                programData.setName(fields[0]);
                programData.setHours(Integer.parseInt(fields[1]));
                programData.setComplexity(Integer.parseInt(fields[2]));

                programs.add(programData);
            }
        }

        return programs;
    }


    public static List<TeacherData> parseTeachers(MultipartFile file) throws IOException {
        List<TeacherData> teachers = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            // Читаем заголовок
            String headerLine = reader.readLine();
            String[] headerFields = headerLine.split("[;,]");

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("[;,]");

                TeacherData teacherData = new TeacherData();
                // Заполняем courseData
                teacherData.setName(fields[0]);
                //teacherData.setPlaceId(Integer.parseInt(fields[1]));
                teacherData.setMaxNumberOfLessons(Integer.parseInt(fields[2]));

                teachers.add(teacherData);
            }
        }

        return teachers;
    }


}