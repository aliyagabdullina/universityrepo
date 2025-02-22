package scheduleBuilder.engines.heuristics;

import scheduleBuilder.engines.SchedulingEngineRunner;

public interface SchedulingHeuristic extends SchedulingEngineRunner<SchedInnerHeuristicApi> {
    SchedInnerHeuristicResult run();
}
