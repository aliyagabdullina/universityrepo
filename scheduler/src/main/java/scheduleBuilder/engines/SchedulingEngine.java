package scheduleBuilder.engines;

public interface SchedulingEngine<T extends EngineApi> {
    void setInput(SchedulingEngineInput<T> engineInput);
    SchedulingEngineResult<T> run() throws RuntimeException;
}
