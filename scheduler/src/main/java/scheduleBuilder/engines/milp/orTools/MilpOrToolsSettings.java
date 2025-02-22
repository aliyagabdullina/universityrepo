package scheduleBuilder.engines.milp.orTools;

import com.google.ortools.linearsolver.MPSolver;
import scheduleBuilder.engines.SchedulingEngineSettings;

import java.util.OptionalLong;

public interface MilpOrToolsSettings extends SchedulingEngineSettings<MilpOrToolsApi> {
    @Override
    OptionalLong getTimeLimitMs();
    MPSolver.OptimizationProblemType getSolverType();

}
