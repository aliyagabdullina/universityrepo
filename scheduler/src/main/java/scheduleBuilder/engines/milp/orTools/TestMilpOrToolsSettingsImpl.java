package scheduleBuilder.engines.milp.orTools;

import com.google.ortools.linearsolver.MPSolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;

class TestMilpOrToolsSettingsImpl {
    MilpOrToolsSettings settings;

    @BeforeEach
    void setUp() {
        settings = new MilpOrToolsSettingsImpl(MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
    }

    @Test
    void testGetTimeLimitMsEmpty() {
        assertEquals(OptionalLong.empty(), settings.getTimeLimitMs());

    }

    @Test
    void testGetTimeLimitMs1000() {
        settings.setTimeLimitMs(1000);
        assertEquals(1000, settings.getTimeLimitMs().getAsLong());
    }

    @Test
    void testSetTimeLimitMsException() {
        assertThrows(IllegalArgumentException.class, () -> settings.setTimeLimitMs(-1000));
    }

    @Test
    void testGetSolverType() {
        assertNotNull(settings.getSolverType());
    }
}