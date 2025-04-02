package algorithms;

import schedule.Schedule;
import scheduleBuilder.data.ScheduleConstraintsAccumulator;
import scheduleBuilder.data.ScheduleObjectiveAccumulator;

public class TabuSearchOptimizerImpl implements TabuSearchOptimizer {
    public TabuSearchOptimizerImpl(Schedule initialSchedule,
                                   ScheduleConstraintsAccumulator constraintAccumulator,
                                   ScheduleObjectiveAccumulator objectiveAccumulator,
                                   int maxIterations, int tabuTenure) {
    }

    @Override
    public Schedule optimize(Schedule schedule) {
        return null;
    }
}
