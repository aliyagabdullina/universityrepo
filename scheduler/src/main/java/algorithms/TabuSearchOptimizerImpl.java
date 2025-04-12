package algorithms;

import lesson.Lesson;
import schedule.Schedule;
import schedule.ScheduleImpl;
import scheduleBuilder.data.ScheduleConstraintsAccumulator;
import scheduleBuilder.data.ScheduleObjectiveAccumulator;

import java.util.ArrayList;
import java.util.List;

public class TabuSearchOptimizerImpl implements TabuSearchOptimizer {
    private final int _maxIterations;
    private final int _tabuTenure;
    private Schedule currentSchedule;
    private Schedule bestSchedule;
    private final ScheduleConstraintsAccumulator _constraintAccumulator;
    private final ScheduleObjectiveAccumulator _objectiveAccumulator;
    private final List<LSMove> tabuList;

    public TabuSearchOptimizerImpl(Schedule initialSchedule,
                                   ScheduleConstraintsAccumulator constraintAccumulator,
                                   ScheduleObjectiveAccumulator objectiveAccumulator,
                                   int maxIterations, int tabuTenure) {
        currentSchedule = new ScheduleImpl(initialSchedule.getAllLessons());
        bestSchedule = new ScheduleImpl(initialSchedule.getAllLessons());
        _constraintAccumulator = constraintAccumulator;
        _objectiveAccumulator = objectiveAccumulator;
        _maxIterations = maxIterations;
        _tabuTenure = tabuTenure;
        tabuList = new ArrayList<>();
    }

    @Override
    public Schedule optimize(Schedule schedule) {
        for (int i = 0; i < _maxIterations; i++) {
            LSNeighbourhood neighbourhood = new LSNeighbourhoodImpl(currentSchedule);
            LSMove bestMove = neighbourhood.getBestMove(tabuList);

            if (bestMove != null) {
                localSearch(bestMove);
                bestMove.makeMove(currentSchedule);
                if (currentSchedule.evaluate() < bestSchedule.evaluate()) {
                    bestSchedule = new ScheduleImpl(currentSchedule.getAllLessons());
                }
            }

            applyTabu(bestMove);
        }
        return bestSchedule;
    }

    private void localSearch(LSMove move) {
        // Мы проводим локальный поиск для улучшения конкретного выбранного перемещения
        // Это может включать несколько шагов в поиске соседей внутри текущего состояния расписания.
        LocalSearchOptimizer localSearch = new LocalSearchOptimizerImpl(currentSchedule);
        LSMove bestLocalMove = localSearch.findBestLocalMove();

        if (bestLocalMove != null) {
            bestLocalMove.makeMove(currentSchedule);
        }

        if (currentSchedule.evaluate() < bestSchedule.evaluate()) {
            bestSchedule = new ScheduleImpl(currentSchedule.getAllLessons());
        }
    }

    // Применение табу-операции
    private void applyTabu(LSMove move) {
        if (move != null) {
            tabuList.add(move);
            if (tabuList.size() > _tabuTenure) {
                tabuList.remove(0);
            }
        }
    }

}
