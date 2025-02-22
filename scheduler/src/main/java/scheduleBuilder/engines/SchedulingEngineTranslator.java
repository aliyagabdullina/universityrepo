package scheduleBuilder.engines;

import schedule.Schedule;
import scheduleBuilder.SchedulingInput;

public interface SchedulingEngineTranslator<T extends EngineApi> {
    SchedulingEngineInput<T> translateInput(SchedulingInput input);
    Schedule translateResult(SchedulingEngineResult<T> result);
}
