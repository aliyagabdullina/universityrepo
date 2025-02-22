package output;

import course.CourseImpl;
import group.GroupImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import person.TeacherImpl;
import place.PlaceImpl;

import java.io.File;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TestDataSaverJsonImpl {
    DtoFileSettings settings;
    DataSaver dataSaver;
    @BeforeEach
    void setUp() {
        initSettings();
        dataSaver = new DataSaverJsonImpl(settings);
    }

    private void initSettings() {
        settings = new DtoFileSettings();
        settings.dataDirectory = new File("C:\\Users\\miptr\\IdeaProjects\\SchoolManagerV2\\main\\src\\output\\testResources");
        settings.teachersFile = new File("C:\\Users\\miptr\\IdeaProjects\\SchoolManagerV2\\main\\src\\output\\testResources\\teachers.json");
        settings.groupsFile = new File("C:\\Users\\miptr\\IdeaProjects\\SchoolManagerV2\\main\\src\\output\\testResources\\groups.json");
        settings.coursesFile = new File("C:\\Users\\miptr\\IdeaProjects\\SchoolManagerV2\\main\\src\\output\\testResources\\courses.json");
        settings.placesFile = new File("C:\\Users\\miptr\\IdeaProjects\\SchoolManagerV2\\main\\src\\output\\testResources\\places.json");
        settings.courseProgramsFile = new File("C:\\Users\\miptr\\IdeaProjects\\SchoolManagerV2\\main\\src\\output\\testResources\\coursePrograms.json");
    }

    @Test
    void testSaveTeachers() {
        assertDoesNotThrow(() -> dataSaver.saveTeachers(Stream.of(new TeacherImpl("Anna"))));
    }

    @Test
    void testSaveGroups() {
        assertDoesNotThrow(() -> dataSaver.saveGroups(Stream.of(new GroupImpl("1A", Set.of()))));
    }

    @Test
    void testSaveCourses() {
        assertDoesNotThrow(() -> dataSaver.saveCourses(Stream.of(new CourseImpl("Math_1"))));
    }

    @Test
    void testSavePlaces() {
        assertDoesNotThrow(() -> dataSaver.savePlaces(Stream.of(new PlaceImpl("Cabinet_1"))));}

}