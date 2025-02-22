package scheduleBuilder.engines.milp.orTools;

import scheduleBuilder.engines.SchedulingEngineResult;
import scheduleBuilder.engines.SchedulingEngineRunner;

public interface MilpOrToolsRunner extends SchedulingEngineRunner<MilpOrToolsApi> {
    @Override
    MilpOrToolsResult run();
}
