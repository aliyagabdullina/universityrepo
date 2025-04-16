package scheduleBuilder;

import collector.SchoolDataCollector;
import schedule.Schedule;
import scheduleBuilder.data.ScheduleConstraintsAccumulator;
import scheduleBuilder.data.ScheduleObjectiveAccumulator;
import scheduleBuilder.engines.settings.DtoScheduleBuilderSettings;
import time.WeeklyTimeSlot;

import java.util.List;

public interface ScheduleBuilder {

    void setScheduleInputData(SchedulingInputData inputData);
    void setTimeSlotSequence(List<WeeklyTimeSlot> timeSlotSequence);
    void setSchoolDataCollector(SchoolDataCollector dataCollector);
    void setConstraintAccumulator(ScheduleConstraintsAccumulator constraintAccumulator);
    void setObjectiveAccumulator(ScheduleObjectiveAccumulator objectiveAccumulator);
    void setSettings(DtoScheduleBuilderSettings settings);

    Schedule solve();

}
