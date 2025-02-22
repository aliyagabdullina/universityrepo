package scheduleBuilder.engines.milp.orTools;

import solution.MilpSolution;

public class MilpOrToolsResultImpl implements MilpOrToolsResult {
    private final MilpSolution _solution;

    public MilpOrToolsResultImpl(MilpSolution solution) {
        _solution = solution;
    }

    @Override
    public MilpSolution getSolution() {
        return _solution;
    }
}
