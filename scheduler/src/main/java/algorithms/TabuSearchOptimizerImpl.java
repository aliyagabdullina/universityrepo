package algorithms;

import group.Group;
import person.Teacher;
import schedule.Schedule;
import schedule.ScheduleImpl;
import scheduleBuilder.data.ScheduleConstraintsAccumulator;
import scheduleBuilder.data.ScheduleObjectiveAccumulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TabuSearchOptimizerImpl implements TabuSearchOptimizer {
    private final int _maxIterations;
    private final int _tabuTenure;
    private Schedule _currentSchedule;
    private Schedule _bestSchedule;
    private final ScheduleConstraintsAccumulator _constraintAccumulator;
    private final ScheduleObjectiveAccumulator _objectiveAccumulator;

    private final List<LSMove> _tabuList;
    public TabuSearchOptimizerImpl(Schedule initialSchedule,
                                   ScheduleConstraintsAccumulator constraintAccumulator,
                                   ScheduleObjectiveAccumulator objectiveAccumulator,
                                   int maxIterations, int tabuTenure) {
        _currentSchedule = new ScheduleImpl(initialSchedule.getAllLessons());
        _bestSchedule = new ScheduleImpl(initialSchedule.getAllLessons());
        _constraintAccumulator = constraintAccumulator;
        _objectiveAccumulator = objectiveAccumulator;
        _maxIterations = maxIterations;
        _tabuTenure = tabuTenure;
        _tabuList = new ArrayList<>();

    }

    @Override
    public Schedule optimize(Schedule schedule) {
        for (int i = 0; i < _maxIterations; i++) {
            LSNeighbourhood neighbourhood = new LSNeighbourhoodImpl(_currentSchedule);
            Optional<LSMove> bestMove = neighbourhood.getBestMove(_tabuList);

            if (bestMove.isPresent()) {
                bestMove.get().makeMove(_currentSchedule);
                localSearch();
                if (_currentSchedule.evaluate() < _bestSchedule.evaluate()) {
                    _bestSchedule = new ScheduleImpl(_currentSchedule.getAllLessons());
                }
                applyTabu(bestMove.get());
            }
        }
        return _bestSchedule;
    }

    private void localSearch() {

        LocalSearchOptimizer localSearch = new LocalSearchOptimizerImpl(_currentSchedule);
        Optional<LSMove> bestLocalMove = localSearch.findBestLocalMove();

        bestLocalMove.ifPresent(lsMove -> lsMove.makeMove(_currentSchedule));

        if (_currentSchedule.evaluate() < _bestSchedule.evaluate()) {
            _bestSchedule = new ScheduleImpl(_currentSchedule.getAllLessons());
        }
    }

    private void applyTabu(LSMove move) {
        _tabuList.add(move);
        if (_tabuList.size() > _tabuTenure) {
            _tabuList.remove(0);
        }
    }

}
