package scheduleBuilder.engines.milp.orTools;

import com.google.ortools.linearsolver.MPSolver;

import java.util.OptionalLong;

public class MilpOrToolsSettingsImpl implements MilpOrToolsSettings {
    private final MPSolver.OptimizationProblemType _solverType;
    private OptionalLong _timeLimitInMs = OptionalLong.empty();

    public MilpOrToolsSettingsImpl(MPSolver.OptimizationProblemType solverType) {
        _solverType = solverType;
    }

    @Override
    public OptionalLong getTimeLimitMs() {
        return _timeLimitInMs;
    }

    @Override
    public void setTimeLimitMs(long timeLimitMs) {
        if(timeLimitMs < 0) {
            throw new IllegalArgumentException("Incorrect time limit: " + timeLimitMs);
        }
        _timeLimitInMs = OptionalLong.of(timeLimitMs);
    }

    @Override
    public MPSolver.OptimizationProblemType getSolverType() {
        return null;
    }
}
