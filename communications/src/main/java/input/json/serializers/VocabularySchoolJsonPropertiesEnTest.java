package input.json.serializers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VocabularySchoolJsonPropertiesEnTest {
    VocabularySchoolJsonProperties voc;
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        voc = new VocabularySchoolJsonPropertiesEn();

    }

    @Test
    void testTeacherName() {
        assertEquals("name", voc.getLabel_objName());
    }

    @Test
    void testGroupStudents() {
        assertEquals("students", voc.getLabel_groupStudents());
    }

    @Test
    void testGetBuilding() {
        assertEquals("building", voc.getLabel_building());
    }
}