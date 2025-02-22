package scheduleBuilder.engines;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestSchedulingEngineSettingsImpl {
    SchedulingEngineSettings settings;
    @BeforeEach
    void setUp() {
        settings = new SchedulingEngineSettingsImpl();
    }

    @Test
    void testGetTimeLimitMs() {
        assertEquals(0, settings.getTimeLimitMs());
    }
}