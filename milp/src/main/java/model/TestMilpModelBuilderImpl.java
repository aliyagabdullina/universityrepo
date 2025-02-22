package model;

import model.constraint.ConstraintType;
import model.constraint.LinearConstraintImpl;
import model.expressions.LinearExpressionImpl;
import model.variables.Variable_BinaryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pair.Pair;

import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestMilpModelBuilderImpl {
    MilpModelBuilder milpModelBuilder;
    @BeforeEach
    void setUp() {
        milpModelBuilder = new MilpModelBuilderImpl("testModel");
    }

    @Test
    void testAddVariableNoException() {
        assertDoesNotThrow(() -> milpModelBuilder.addVariable(new Variable_BinaryImpl("varName")));
    }

    @Test
    void testAddVariableSameVarIdException() {
        milpModelBuilder.addVariable(new Variable_BinaryImpl("varName"));
        assertThrows(IllegalArgumentException.class, () -> milpModelBuilder.addVariable(new Variable_BinaryImpl("varName")));
    }

    @Test
    void testAddVariableSuccess() {
        milpModelBuilder.addVariable(new Variable_BinaryImpl("varName"));
        MilpModel model = milpModelBuilder.getModel();
        boolean variablePresented = model.getVariablesStream()
                .anyMatch(variable -> Objects.equals(variable.getLabel(), "varName"));
        assertTrue(variablePresented);
    }

    @Test
    void testAddConstraintNoException() {
        var pair1 = new Pair<>("label1", 4.0);
        var pair2 = new Pair<>("label2", 3.1);
        var expression = new LinearExpressionImpl(Stream.of(pair1, pair2), 5.0);
        var constraint = new LinearConstraintImpl("C1", expression, ConstraintType.LESS_OR_EQUAL, 10.0);

        assertDoesNotThrow(() -> milpModelBuilder.addConstraint(constraint));
    }

    @Test
    void testAddConstraintSameIdException() {
        var pair1 = new Pair<>("label1", 4.0);
        var pair2 = new Pair<>("label2", 3.1);
        var expression = new LinearExpressionImpl(Stream.of(pair1, pair2), 5.0);
        var constraint = new LinearConstraintImpl("C1", expression, ConstraintType.LESS_OR_EQUAL, 10.0);

        milpModelBuilder.addConstraint(constraint);
        assertThrows(IllegalArgumentException.class, () -> milpModelBuilder.addConstraint(constraint));
    }

    @Test
    void testAddConstraintSuccess() {
        var pair1 = new Pair<>("label1", 4.0);
        var pair2 = new Pair<>("label2", 3.1);
        var expression = new LinearExpressionImpl(Stream.of(pair1, pair2), 5.0);
        var constraint = new LinearConstraintImpl("C0", expression, ConstraintType.LESS_OR_EQUAL, 10.0);

        milpModelBuilder.addConstraint(constraint);
        MilpModel model = milpModelBuilder.getModel();
        boolean constraintPresented = model.getConstraintsStream()
                .anyMatch(constr -> Objects.equals(constr.getLabel(), "C0"));
        assertTrue(constraintPresented);
    }

    @Test
    void testSetObjectiveNoException() {
        var pair1 = new Pair<>("label1", 4.0);
        var pair2 = new Pair<>("label2", 3.1);
        var expression = new LinearExpressionImpl(Stream.of(pair1, pair2), 5.0);

        assertDoesNotThrow(() -> milpModelBuilder.setObjective(expression, ObjectiveType.MINIMIZATION));
    }

    @Test
    void testSetObjectiveSuccess() {
        var pair1 = new Pair<>("label1", 4.0);
        var pair2 = new Pair<>("label2", 3.1);
        var expression = new LinearExpressionImpl(Stream.of(pair1, pair2), 5.0);

        assertDoesNotThrow(() -> milpModelBuilder.setObjective(expression, ObjectiveType.MINIMIZATION));
        MilpModel model = milpModelBuilder.getModel();
        assertTrue(model.getObjectiveExpression().isPresent());
    }

    @Test
    void testGetModelNotNull() {
        assertNotNull(milpModelBuilder.getModel());
    }

    @Test
    void testGetModelName() {
        assertEquals("testModel", milpModelBuilder.getModel().getName());
    }
}