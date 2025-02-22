package scheduleBuilder.engines;

import java.util.OptionalLong;

public interface SchedulingEngineSettings<T extends EngineApi> {
    OptionalLong getTimeLimitMs();
    void setTimeLimitMs(long timeLimitMs);
}
