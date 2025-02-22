package objectFactories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import person.Teacher;
import person.TeacherImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestSchoolDtoFactoryImpl {
    SchoolDtoFactory dtoFactory;
    @BeforeEach
    void setUp() {
        dtoFactory = new SchoolDtoFactoryImpl();
    }


    @Test
    void createTeacherDto() {
        Teacher teacher = new TeacherImpl("Anna");
        var result = dtoFactory.createTeacherDto(teacher);
        assertNotNull(result);
        assertEquals("Anna", result.name);
    }
}