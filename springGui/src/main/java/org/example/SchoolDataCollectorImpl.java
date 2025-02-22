package org.example;

import collector.DtoSchoolDataCollector;
import collector.SchoolDataCollector;
import collector.SchoolDataStoringFormat;
import com.google.gson.Gson;
import course.Course;
import course.CourseInProgram;
import course.CourseProgram;
import course.DtoCourseInput;
import group.DtoGroupInput;
import group.Group;
import objectFactories.SchoolObjectsFactory;
import person.DtoStudentInput;
import person.DtoTeacherInput;
import person.Student;
import person.Teacher;
import place.DtoPlace;
import place.Place;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchoolDataCollectorImpl implements SchoolDataCollector {
    // teachers
    private Map<String, Teacher> _teachersMap = new ConcurrentHashMap<>();
    // students
    private Map<String, Student> _studentsMap = new ConcurrentHashMap<>();
    // groups
    private Map<String, Group> _groupsMap = new ConcurrentHashMap<>();
    // courses
    private Map<String, Course> _coursesMap = new ConcurrentHashMap<>();
    // places
    private Map<String, Place> _placesMap = new ConcurrentHashMap<>();
    private Map<String, CourseProgram> _courseProgramsMap = new ConcurrentHashMap<>();
    private final DataBaseInteractor _databaseInteractor;

    public SchoolDataCollectorImpl(DataBaseInteractor dataBaseInteractor) {
        _databaseInteractor = dataBaseInteractor;
    }



    @Override
    public void load(Path path, SchoolDataStoringFormat format, SchoolObjectsFactory factory) throws IOException {
        if(!format.ifPathIsOk(path)) {
            throw new IOException();
        }

        File file = path.toFile();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        DtoSchoolDataCollector dto = new Gson().fromJson(reader, DtoSchoolDataCollector.class);
        initializeFromDto(dto, factory);
        reader.close();
    }

    private void initializeFromDto(DtoSchoolDataCollector dto, SchoolObjectsFactory factory) {
        initializeTeachers(dto.teachers.stream(), factory);
        initializeCourses(dto.courses.stream(),factory);
        initializeStudents(dto.students.stream(), factory);
        initializeGroups(dto.groups.stream(), factory);
        initializePlaces(dto.places.stream(), factory);
    }

    private void initializeTeachers(Stream<DtoTeacherInput> teacherInputStream, SchoolObjectsFactory factory) {
        _teachersMap = teacherInputStream
                .map(factory::buildTeacher)
                .collect(Collectors.toConcurrentMap(Teacher::getName, teacher -> teacher));
    }

    private void initializeCourses(Stream<DtoCourseInput> courseInputStream, SchoolObjectsFactory factory) {
        _coursesMap = courseInputStream
                .map(factory::buildCourse)
                .collect(Collectors.toConcurrentMap(Course::getName, course -> course));
    }

    private void initializeStudents(Stream<DtoStudentInput> studentInputStream, SchoolObjectsFactory factory) {
        _studentsMap = studentInputStream
                .map(factory::buildStudent)
                .collect(Collectors.toConcurrentMap(Student::getName, student -> student));
    }

    private void initializeGroups(Stream<DtoGroupInput> groupInputStream, SchoolObjectsFactory factory) {
        _groupsMap = groupInputStream
                .map(factory::buildGroup)
                .collect(Collectors.toConcurrentMap(Group::getName, group -> group));
    }
    private void initializePlaces(Stream<DtoPlace> placeInputStream, SchoolObjectsFactory factory) {
        _placesMap = placeInputStream
                .map(factory::buildPlace)
                .collect(Collectors.toConcurrentMap(Place::getName, teacher -> teacher));
    }

    @Override
    public void addTeacher(Teacher teacher) {
        _teachersMap.put(teacher.getName(), teacher);
    }

    @Override
    public void removeTeacher(String teacherName) {
        _teachersMap.remove(teacherName);
    }

    @Override
    public Teacher getTeacher(String name) {
        return _teachersMap.get(name);
    }


    @Override
    public Stream<Teacher> getTeachers() {
        return _teachersMap.values().stream();
    }

    @Override
    public void addGroup(Group group) {
        _groupsMap.put(group.getName(), group);
    }

    @Override
    public Group getGroup(String groupName) {
        return _groupsMap.get(groupName);
    }

    @Override
    public void removeCourse(String courseName) {
        _coursesMap.remove(courseName);
        _courseProgramsMap.values()
                .stream()
                .forEach(courseProgram -> {
                    List<CourseInProgram> toRemove = courseProgram.getCoursesInProgram()
                            .filter(courseInProgram -> courseInProgram.getCourses().anyMatch(course -> course.getName().equals(courseName)))
                            .toList();
                    toRemove.forEach(courseProgram::removeCourseInProgram);
                });
    }

    @Override
    public void addCourseProgram(CourseProgram courseProgram) {
        _courseProgramsMap.put(courseProgram.getName(), courseProgram);
    }

    @Override
    public Stream<CourseProgram> getCoursePrograms() {
        return _courseProgramsMap.values().stream();
    }

    @Override
    public CourseProgram getCourseProgram(String courseProgramName) {
        return _courseProgramsMap.get(courseProgramName);
    }

    @Override
    public void removeCourseProgram(String courseProgramName) {
        _courseProgramsMap.remove(courseProgramName);
    }

    @Override
    public void removeGroup(String groupName) {
        _groupsMap.remove(groupName);
    }

    @Override
    public Stream<Group> getGroups() {
        return _groupsMap.values().stream();
    }

    @Override
    public void addStudent(Student student) {
        _studentsMap.put(student.getName(), student);
    }

    @Override
    public Student getStudent(String name) {
        return _studentsMap.get(name);
    }

    @Override
    public void addCourse(Course course) {
        _coursesMap.put(course.getName(), course);
    }

    @Override
    public Stream<Course> getCourses() {
        return _coursesMap.values().stream();
    }

    @Override
    public Course getCourse(String courseName) {
        return _coursesMap.get(courseName);
    }

    @Override
    public Stream<Student> getStudents() {
        return _studentsMap.values().stream();
    }

    @Override
    public Stream<Place> getPlaces() {
        return _placesMap.values().stream();
    }

    @Override
    public Place getPlace(String placeName) {
        return _placesMap.get(placeName);
    }

    @Override
    public void addPlace(Place place) {
        _placesMap.put(place.getName(), place);
    }

    @Override
    public void removePlace(String placeName) {
        _placesMap.remove(placeName);
    }
}
