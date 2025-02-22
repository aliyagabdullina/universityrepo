package scheduleBuilder.engines.milp.orTools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import solution.MilpSolutionImpl;
import solution.SolutionStatus;

import java.util.OptionalDouble;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TestMilpOrToolsResultImpl {
    MilpOrToolsResult result;

    @BeforeEach
    void setUp() {
        var infeasibleSolution = new MilpSolutionImpl(SolutionStatus.NOT_FOUND, Stream.empty(), OptionalDouble.empty());
        result = new MilpOrToolsResultImpl(infeasibleSolution);
    }

    @Test
    void testGetSolution() {
        assertNotNull(result.getSolution());
    }
}