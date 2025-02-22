package scheduleBuilder.engines.milp.orTools;

import scheduleBuilder.engines.SchedulingEngineResult;
import solution.MilpSolution;

public interface MilpOrToolsResult extends SchedulingEngineResult<MilpOrToolsApi> {
    MilpSolution getSolution();
}
