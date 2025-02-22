package scheduleBuilder.translators.milp;

import model.MilpModel;
import schedule.Schedule;
import scheduleBuilder.SchedulingInput;
import solution.MilpSolution;

public interface MilpTranslator {
    MilpModel createModel(SchedulingInput schedulingInput);
    Schedule createSchedule(MilpSolution solution);
}
