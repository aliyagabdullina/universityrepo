package algorithms;

import schedule.Schedule;

public interface GreedyScheduler extends Scheduler {
    @Override
    Schedule generateSchedule();
}
