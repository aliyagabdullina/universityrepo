package programSettings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VocabularyFilesTest {
    VocabularyFiles voc;

    @BeforeEach
    void setUp() {
        voc =  new VocabularyFilesImpl();
    }

    @Test
    void testGetDataFolderName() {
        assertEquals("data", voc.getDataFolderName());
    }

    @Test
    void testGetTeachersFileName() {
        assertEquals("teachers.json", voc.getTeachersFileName());
    }

    @Test
    void testGetCoursesFileName() {
        assertEquals("courses.json", voc.getCoursesFileName());
    }

    @Test
    void testGetGroupsFileName() {
        assertEquals("groups.json", voc.getGroupsFileName());
    }

    @Test
    void testGetPlacesFileName() {
        assertEquals("places.json", voc.getPlacesFileName());
    }
}