package scheduleBuilder.engines.milp.orTools;

import scheduleBuilder.engines.EngineApi;
import scheduleBuilder.engines.EngineApiType;

public interface MilpOrToolsApi extends EngineApi {
    @Override
    default EngineApiType getType() {
        return EngineApiType.MILP_OR_TOOLS;
    }


}
