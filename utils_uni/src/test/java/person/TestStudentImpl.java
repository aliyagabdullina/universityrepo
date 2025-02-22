package person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestStudentImpl {
    Student student;

    @BeforeEach
    void setUp() {
        student = new StudentImpl("Sergey");
    }

    @Test
    void testGetName() {
        assertEquals("Sergey", student.getName());
    }
}