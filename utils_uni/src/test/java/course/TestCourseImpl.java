package course;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestCourseImpl {
    Course course;
    @BeforeEach
    void setUp() {
        course = new CourseImpl("Mechanics-7");
    }

    @Test
    void testAssertGetName() {
        assertEquals("Mechanics-7", course.getName());
    }
}