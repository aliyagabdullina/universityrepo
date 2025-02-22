package model.constraint;

import model.expressions.LinearExpression;
import model.expressions.LinearExpressionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pair.Pair;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestLinearConstraintImpl {
    LinearExpression expression;
    LinearConstraint emptyConstraint;
    LinearConstraint constraint;

    @BeforeEach
    void setUp() {
        var pair1 = new Pair<>("label1", 4.0);
        var pair2 = new Pair<>("label2", 3.1);
        expression = new LinearExpressionImpl(Stream.of(pair1, pair2), 5.0);

        emptyConstraint = new LinearConstraintImpl("C1", expression, ConstraintType.EQUALS, 0.0);
        constraint = new LinearConstraintImpl("C1", expression, ConstraintType.LESS_OR_EQUAL, 10.0);
    }

    @Test
    void testGetId() {
        assertEquals("C1", constraint.getLabel());
    }

    @Test
    void testConstraintType() {
        assertEquals(ConstraintType.EQUALS, emptyConstraint.getConstraintType());
        assertEquals(ConstraintType.LESS_OR_EQUAL, constraint.getConstraintType());
    }

    @Test
    void testGetConstant() {
        assertEquals(0.0, emptyConstraint.getConstant());
        assertEquals(10.0, constraint.getConstant());
    }

    @Test
    void testGetExpression() {
        assertNotNull(constraint.getExpression());
        assertEquals(expression, constraint.getExpression());
    }
}