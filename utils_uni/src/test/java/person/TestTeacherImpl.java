package person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestTeacherImpl {
    Teacher teacher;
    @BeforeEach
    void setUp() {
        teacher = new TeacherImpl("Anna");
    }

    @Test
    void testGetName() {
        assertEquals("Anna", teacher.getName());
    }

    @Test
    void testEqualsTrue() {
        assertEquals(teacher, new TeacherImpl("Anna"));
    }
}