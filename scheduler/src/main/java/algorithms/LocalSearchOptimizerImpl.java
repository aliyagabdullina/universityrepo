package algorithms;

import lesson.Lesson;
import schedule.Schedule;

import java.util.ArrayList;
import java.util.List;

public class LocalSearchOptimizerImpl implements LocalSearchOptimizer {
    private Schedule currentSchedule;
    public LocalSearchOptimizerImpl(Schedule currentSchedule) {
        this.currentSchedule = currentSchedule;
    }

    @Override
    public LSMove findBestLocalMove() {
        List<LSMove> localMoves = generateLocalMoves();
        LSMove bestMove = null;
        double bestEvaluation = Double.MAX_VALUE;

        for (LSMove move : localMoves) {
            move.makeMove(currentSchedule);
            double evaluation = currentSchedule.evaluate();
            if (evaluation < bestEvaluation) {
                bestEvaluation = evaluation;
                bestMove = move;
            }
            undoMove(move);
        }
        return bestMove;
    }

    private List<LSMove> generateLocalMoves() {
        List<LSMove> localMoves = new ArrayList<>();
        List<Lesson> lessons = currentSchedule.getAllLessons().toList();

        for (int i = 0; i < lessons.size(); i++) {
            for (int j = i + 1; j < lessons.size(); j++) {
                localMoves.add(new LSMoveImpl(i, j));
            }
        }
        return localMoves;
    }

    private void undoMove(LSMove move) {
        move.makeMove(currentSchedule);
    }
}
