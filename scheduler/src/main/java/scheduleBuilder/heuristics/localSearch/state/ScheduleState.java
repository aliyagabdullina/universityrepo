package scheduleBuilder.heuristics.localSearch.state;

import schedule.Schedule;
import scheduleBuilder.heuristics.localSearch.move.LocalSearchMove;

public interface ScheduleState {


    Schedule buildSchedule();
}
