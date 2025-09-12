package algorithms;

import lesson.Lesson;
import schedule.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LSNeighbourhoodImpl implements LSNeighbourhood {
    private final Schedule schedule;

    public LSNeighbourhoodImpl(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public Optional<LSMove> getBestMove(List<LSMove> tabuList) {
        List<LSMove> possibleMoves = generatePossibleMoves();
        LSMove bestMove = null;
        double bestEvaluation = Double.MAX_VALUE;

        for (LSMove move : possibleMoves) {
            if (tabuList.contains(move)) continue;

            move.makeMove(schedule);
            double evaluation = schedule.evaluate(); // Оценка

            if (evaluation < bestEvaluation) {
                bestEvaluation = evaluation;
                bestMove = move;
            }

            undoMove(move);
        }
        return bestMove == null ? Optional.empty() : Optional.of(bestMove);
    }

    private List<LSMove> generatePossibleMoves() {
        List<LSMove> moves = new ArrayList<>();
        List<Lesson> lessons = schedule.getAllLessons().toList();

        for (int i = 0; i < lessons.size(); i++) {
            for (int j = i + 1; j < lessons.size(); j++) {
                moves.add(new LSMoveImpl(i, j));
            }
        }
        return moves;
    }

    private void undoMove(LSMove move) {
        move.makeMove(schedule); // Просто снова применяем тот же самый метод для отмены изменений - работает в
        //случае move = перестановка, иначе другая логика
    }

}
