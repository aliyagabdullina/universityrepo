package scheduleBuilder.engines.milp.orTools;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPSolver;
import model.MilpModelImpl;
import model.ObjectiveType;
import model.constraint.ConstraintType;
import model.constraint.LinearConstraintImpl;
import model.expressions.LinearExpression;
import model.expressions.LinearExpressionImpl;
import model.variables.Variable_BinaryImpl;
import model.variables.Variable_IntegerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pair.Pair;
import scheduleBuilder.engines.SchedulingEngineRunner;
import solution.MilpSolution;
import solution.SolutionStatus;

import java.util.OptionalInt;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


class TestMilpOrToolsRunner {
    static {
        Loader.loadNativeLibraries();
    }
    MilpOrToolsInput input;
    SchedulingEngineRunner<MilpOrToolsApi> runner;
    @BeforeEach
    void setUp() {

        initializeInput();
        runner = new MilpOrToolsRunnerImpl(input);
    }

    private void initializeInput() {
        var v1 = new Variable_IntegerImpl("lab_1", OptionalInt.of(2), OptionalInt.of(10));
        var v2 = new Variable_IntegerImpl("lab_2", OptionalInt.empty(),OptionalInt.of(10));
        var v3 = new Variable_IntegerImpl("lab_3", OptionalInt.of(2), OptionalInt.empty());
        var pair1 = new Pair<>("label1", 4.0);
        var pair2 = new Pair<>("label2", 3.1);
        LinearExpression objectiveExpression = new LinearExpressionImpl(Stream.of(pair1, pair2), 5.0);
        var constraint = new LinearConstraintImpl("C1", objectiveExpression, ConstraintType.LESS_OR_EQUAL, 10.0);
        var model = new MilpModelImpl("modelName", Stream.of(v1, v2, v3), Stream.of(constraint), ObjectiveType.MINIMIZATION, objectiveExpression);
        var settings = new MilpOrToolsSettingsImpl(MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
        input = new MilpOrToolsInputImpl(model, settings);
    }

    @Test
    void testRunNotNull() {
        assertNotNull(runner.run());
    }

    @Test
    void testRunTrivialModelMinimization() {
        var v1 = new Variable_IntegerImpl("lab_1", OptionalInt.of(2), OptionalInt.of(3));
        var v2 = new Variable_BinaryImpl("lab_2");
        var pair1 = new Pair<>("label1", 1.0);
        var pair2 = new Pair<>("label2", -1.0);
        LinearExpression objectiveExpression = new LinearExpressionImpl(Stream.of(pair1, pair2), 2);
        var model = new MilpModelImpl("modelName", Stream.of(v1, v2), Stream.empty(), ObjectiveType.MINIMIZATION, objectiveExpression);
        var settings = new MilpOrToolsSettingsImpl(MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
        settings.setTimeLimitMs(10000);
        var trivialInput = new MilpOrToolsInputImpl(model, settings);
        var trivialRunner = new MilpOrToolsRunnerImpl(trivialInput);

        var result = trivialRunner.run();
        MilpSolution solution = result.getSolution();
        assertTrue(solution.getObjective().isPresent());
        assertEquals(1.0, solution.getObjective().getAsDouble());
        assertEquals(SolutionStatus.OPTIMAL, solution.getSolutionStatus());
    }
}