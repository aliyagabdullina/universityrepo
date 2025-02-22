package objectFactories;

import course.DtoCourseInput;
import group.DtoGroupInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import person.DtoStudentInput;
import person.DtoTeacherInput;
import place.DtoPlace;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestSchoolObjectsFactoryImpl {
    SchoolObjectsFactory factory;
    @BeforeEach
    void setUp() {
        //SchoolDataCollector dataCollector = new SchoolDataCollectorImpl();
        //factory = new SchoolObjectsFactoryImpl(dataCollector);
    }

    @Test
    void testBuildTeacher() {
        var dtoTeacher = new DtoTeacherInput();
        dtoTeacher.name = "Anna";
        assertNotNull(factory.buildTeacher(dtoTeacher));
    }

    @Test
    void testBuildStudent() {
        var dto = new DtoStudentInput();
        dto.name = "Anna";
        assertNotNull(factory.buildStudent(dto));
    }

    @Test
    void testBuildGroup() {
        var dto = new DtoGroupInput();
        dto.name = "11-1";
        assertNotNull(factory.buildGroup(dto));
    }

    @Test
    void testBuildCourse() {
        var dto = new DtoCourseInput();
        dto.name = "A";
        assertNotNull(factory.buildCourse(dto));
    }

    @Test
    void testBuildPlace() {
        var dto = new DtoPlace();
        dto.name = "A";
        assertNotNull(factory.buildPlace(dto));
    }
}