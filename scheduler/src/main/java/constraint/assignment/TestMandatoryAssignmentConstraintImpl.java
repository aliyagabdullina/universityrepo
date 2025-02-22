package constraint.assignment;

import group.Group;
import group.GroupImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import person.Teacher;
import person.TeacherImpl;
import schedule.DummyScheduleCreator;
import schedule.Schedule;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestMandatoryAssignmentConstraintImpl {
    MandatoryAssignmentConstraint consistentConstraint;
    MandatoryAssignmentConstraint inconsistentConstraint;
    Schedule dummyOneGroupSchedule;

    @BeforeEach
    void setUp() {
        Teacher teacherA = new TeacherImpl("Anna");
        Teacher teacherO = new TeacherImpl("Olga");
        Group group = new GroupImpl( "A", new HashSet<>());

        List<Object> listA = List.of(teacherA, group);
        consistentConstraint = new MandatoryAssignmentConstraintImpl(listA);

        List<Object> listO = List.of(teacherO, group);
        inconsistentConstraint = new MandatoryAssignmentConstraintImpl(listO);
        dummyOneGroupSchedule = DummyScheduleCreator.createOneGroupSchedule();
    }

    @Test
    void testGetObjectsNotNull() {
        assertNotNull(consistentConstraint.getObjects());
    }

    @Test
    void testGetObjectsSuccess() {
        assertTrue(consistentConstraint.getObjects().anyMatch(o -> o instanceof Teacher));
        assertTrue(consistentConstraint.getObjects().anyMatch(o -> o instanceof Group));
    }

    @Test
    void testIfConsistentTrue() {
        assertTrue(consistentConstraint.ifConsistent(dummyOneGroupSchedule.getAllLessons()));
    }


}