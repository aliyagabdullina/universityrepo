package solution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pair.Pair;

import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestMilpSolutionImpl {
    MilpSolution feasibleSolution;
    MilpSolution infeasibleSolution;
    @BeforeEach
    void setUp() {
        feasibleSolution = new MilpSolutionImpl(SolutionStatus.FEASIBLE, Stream.of(new Pair<>("b", 10.0)), OptionalDouble.of(5.0));
        infeasibleSolution = new MilpSolutionImpl(SolutionStatus.NOT_FOUND, Stream.empty(), OptionalDouble.empty());
    }

    @Test
    void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new MilpSolutionImpl(SolutionStatus.NOT_FOUND, Stream.of(new Pair<>("b", 10.0)), OptionalDouble.empty()));
    }

    @Test
    void testGetValueInfeasibleIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> infeasibleSolution.getValue("a"));

    }

    @Test
    void testGetValueFeasible() {
        assertDoesNotThrow(() -> feasibleSolution.getValue("a"));
        assertEquals(0.0, feasibleSolution.getValue("a"));
        assertEquals(10.0, feasibleSolution.getValue("b"));
    }

    @Test
    void testGetSolutionStatus() {
        assertEquals(SolutionStatus.FEASIBLE, feasibleSolution.getSolutionStatus());
    }

    @Test
    void testGetObjective() {
        assertEquals(5.0, feasibleSolution.getObjective().getAsDouble());
    }

    @Test
    void testNonZeroGetVarLabelValuePair() {
        var stream = feasibleSolution.getNonZeroValues();
        assertEquals(1, stream.count());
    }
}