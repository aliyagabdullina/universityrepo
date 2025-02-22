package scheduleBuilder.engines;

public interface SchedulingEngineRunner<T extends EngineApi> {
    SchedulingEngineResult<T> run();
}
