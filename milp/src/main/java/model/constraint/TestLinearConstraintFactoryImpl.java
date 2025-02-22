package model.constraint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestLinearConstraintFactoryImpl {
    LinearConstraintFactory constraintFactory;

    @BeforeEach
    void setUp() {
        constraintFactory = new LinearConstraintFactoryImpl();
    }


    @Test
    void testCreateFixVariableValue() {
        var constraint = constraintFactory.createFixVariableValue("C1", "var1", 12.0);
        assertNotNull(constraint);
        assertEquals(ConstraintType.EQUALS, constraint.getConstraintType());
        assertEquals(12.0, constraint.getConstant());
    }

    @Test
    void testCreateVarSumBoundUb() {
        var constraint = constraintFactory.createVarSumBoundUb("C1", Stream.of("var1", "var2"), 10.0);
        assertNotNull(constraint);
        assertEquals(ConstraintType.LESS_OR_EQUAL, constraint.getConstraintType());
    }

    @Test
    void testCreateAssignment() {
        var constraint = constraintFactory.createAssignmentConstraint("C1", Stream.of("var1", "var2"));
        assertNotNull(constraint);
        assertEquals(ConstraintType.EQUALS, constraint.getConstraintType());
    }

    @Test
    void testCreateBlocking() {
        var constraint = constraintFactory.createSelectionConstraint("C1", "var1", Stream.of("var2", "var3"));
        assertNotNull(constraint);
    }
}