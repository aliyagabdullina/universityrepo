package algorithms;

import collector.SchoolDataCollector;
import schedule.Schedule;
import scheduleBuilder.SchedulingInputData;
import scheduleBuilder.data.ScheduleConstraintsAccumulator;
import scheduleBuilder.data.ScheduleObjectiveAccumulator;
import scheduleBuilder.engines.settings.DtoScheduleBuilderSettings;
import time.WeeklyTimeSlot;

import java.util.List;

public class GlobalScheduler implements Scheduler{
    private final SchoolDataCollector _dataCollector;
    private final List<WeeklyTimeSlot> _timeSlotSequence;
    private final SchedulingInputData _scheduleInputData;
    private final ScheduleConstraintsAccumulator _constraintAccumulator;
    private final ScheduleObjectiveAccumulator _objectiveAccumulator;
    private final DtoScheduleBuilderSettings _settings;


    public GlobalScheduler(SchoolDataCollector dataCollector, List<WeeklyTimeSlot> timeSlotSequence,
                           SchedulingInputData scheduleInputData,
                           ScheduleConstraintsAccumulator constraintAccumulator,
                           ScheduleObjectiveAccumulator objectiveAccumulator,
                           DtoScheduleBuilderSettings scheduleBuilderSettings) {
        _dataCollector = dataCollector;
        _timeSlotSequence = timeSlotSequence;
        _scheduleInputData = scheduleInputData;
        _constraintAccumulator = constraintAccumulator;
        _objectiveAccumulator = objectiveAccumulator;
        _settings = scheduleBuilderSettings;
    }

    @Override
    public Schedule generateSchedule() {
        // 1. Жадный алгоритм строит стартовое расписание
        GreedyScheduler greedyScheduler = new GreedySchedulerImpl(_scheduleInputData);
        Schedule initialSchedule = greedyScheduler.generateSchedule();

        // 2. Двухуровневый Tabu + Local Search
        TabuSearchOptimizer optimizer = new TabuSearchOptimizerImpl(
                initialSchedule, _constraintAccumulator, _objectiveAccumulator, 10, 10);
        Schedule optimizedSchedule = optimizer.optimize(initialSchedule);

        return optimizedSchedule;
    }
}
