package model;

import model.constraint.ConstraintType;
import model.constraint.LinearConstraintImpl;
import model.expressions.LinearExpression;
import model.expressions.LinearExpressionImpl;
import model.variables.Variable_IntegerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pair.Pair;

import java.util.OptionalInt;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestMilpModelImpl {
    MilpModel model;

    @BeforeEach
    void setUp() {
        var v1 = new Variable_IntegerImpl("label1", OptionalInt.of(2), OptionalInt.of(10));
        var v2 = new Variable_IntegerImpl("label2", OptionalInt.empty(),OptionalInt.of(10));
        var v3 = new Variable_IntegerImpl("label3", OptionalInt.of(2), OptionalInt.empty());
        var pair1 = new Pair<>("label1", 4.0);
        var pair2 = new Pair<>("label2", 3.1);
        LinearExpression objectiveExpression = new LinearExpressionImpl(Stream.of(pair1, pair2), 5.0);
        var constraint = new LinearConstraintImpl("C1", objectiveExpression, ConstraintType.LESS_OR_EQUAL, 10.0);
        model = new MilpModelImpl("modelName", Stream.of(v1, v2, v3), Stream.of(constraint), ObjectiveType.MINIMIZATION, objectiveExpression);
    }


    @Test
    void testGetName() {
        assertEquals("modelName", model.getName());
    }

    @Test
    void testGetVariablesStream() {
        assertEquals(3, model.getVariablesStream().count());
    }

    @Test
    void testGetConstraints() {
        assertEquals(1, model.getConstraintsStream().count());
    }

    @Test
    void testGetObjectiveType() {
        assertEquals(ObjectiveType.MINIMIZATION, model.getObjectiveType());
    }

    @Test
    void getObjectiveExpression() {
        assertTrue(model.getObjectiveExpression().isPresent());
    }
}