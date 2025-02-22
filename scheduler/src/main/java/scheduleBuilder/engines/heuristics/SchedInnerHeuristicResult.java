package scheduleBuilder.engines.heuristics;

import schedule.Schedule;
import scheduleBuilder.engines.SchedulingEngineResult;

public interface SchedInnerHeuristicResult extends SchedulingEngineResult<SchedInnerHeuristicApi> {
    Schedule getSchedule();
}
