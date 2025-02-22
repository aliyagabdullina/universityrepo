package scheduleBuilder.engines;

public interface SchedulingEngineInput<T extends EngineApi> {
    SchedulingEngineSettings<T> getSettings();
}
