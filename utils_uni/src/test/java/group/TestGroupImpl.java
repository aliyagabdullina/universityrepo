package group;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import person.Student;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TestGroupImpl {
    Group group;
    Set<Student> studentsSet;
    @BeforeEach
    void setUp() {
        studentsSet = new HashSet<>();
        group = new GroupImpl("7-1", studentsSet);
    }

    @Test
    void testSetGetCurrentGradeName() {
        assertEquals("7-1", group.getName());

    }


    @Test
    void testGetSize() {
        assertEquals(0, group.size());
    }

    @Test
    void testEquals() {
        assertEquals(new GroupImpl("7-1", new HashSet<>()), group);
    }

    @Test
    void testGetStudents() {
        assertNotNull(group.getStudents());
    }
}