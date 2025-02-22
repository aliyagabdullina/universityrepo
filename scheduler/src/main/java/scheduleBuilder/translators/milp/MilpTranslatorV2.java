package scheduleBuilder.translators.milp;

import model.MilpModel;
import schedule.Schedule;
import scheduleBuilder.SchedulingInput;
import scheduleBuilder.SchedulingInputData;
import scheduleBuilder.SchedulingInputV2;
import solution.MilpSolution;

public interface MilpTranslatorV2 {
    MilpModel createModel(SchedulingInputV2 schedulingInput);
    Schedule createSchedule(MilpSolution solution);
}
