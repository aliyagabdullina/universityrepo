package scheduleBuilder.engines.cplex;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class TestCplexSolutionReader {

    @Test
    void testExtractVarValues() {
        File file = new File("C:\\WorkData\\School scheduler\\projects\\school33\\buf\\buf_solution.sol");
        assertDoesNotThrow(() -> CplexSolutionReader.extractNonZeroVarValues(file));
        var result = CplexSolutionReader.extractNonZeroVarValues(file);
        assertNotNull(result);
    }
}