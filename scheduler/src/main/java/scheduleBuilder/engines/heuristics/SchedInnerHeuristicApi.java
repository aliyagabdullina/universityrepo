package scheduleBuilder.engines.heuristics;

import scheduleBuilder.engines.EngineApi;
import scheduleBuilder.engines.EngineApiType;

public interface SchedInnerHeuristicApi extends EngineApi {

    @Override
    default EngineApiType getType() {
        return EngineApiType.HEURISTICS;
    }
}
