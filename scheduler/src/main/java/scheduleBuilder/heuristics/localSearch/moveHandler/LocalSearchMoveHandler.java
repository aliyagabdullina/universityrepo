package scheduleBuilder.heuristics.localSearch.moveHandler;

import scheduleBuilder.heuristics.localSearch.move.LocalSearchMove;
import scheduleBuilder.heuristics.localSearch.state.ScheduleState;

public interface LocalSearchMoveHandler {
    ScheduleState changeStateByMove(ScheduleState state, LocalSearchMove move);
}
