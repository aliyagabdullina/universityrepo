package scheduleBuilder.data;

import scheduleMetric.ScheduleMetric;

public interface ScheduleObjectiveAccumulator {
    void addObjectiveComponent(ScheduleMetric metric, int coefficient);
}
