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
            Schedule afterMove = move.makeMove(currentSchedule);
            double evaluation = afterMove.evaluate();
            if (evaluation < bestEvaluation) {
                bestEvaluation = evaluation;
                bestMove = move;
            }
            undoMove(move);
        }
        return bestMove;
    }

    private List<LSMove> generateLocalMoves() {
        List<LSMove> moves = new ArrayList<>();
        List<Lesson> lessons = currentSchedule.getAllLessons().toList();

        // 1. Поменять два урока местами
        for (int i = 0; i < lessons.size(); i++) {
            for (int j = i + 1; j < lessons.size(); j++) {
                moves.add(new SwapLessonsMove(i, j));   // обмен уроков
                moves.add(new SwapTeacherMove(i, j));  // обмен преподавателей
                moves.add(new SwapGroupMove(i, j));    // обмен групп
            }
        }
        return moves;
    }

    private void undoMove(LSMove move) {
        move.makeMove(currentSchedule);
    }
}
