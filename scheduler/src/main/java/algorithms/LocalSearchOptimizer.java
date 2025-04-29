package algorithms;

import schedule.Schedule;

import java.util.Optional;

public interface LocalSearchOptimizer {
    Optional<LSMove> findBestLocalMove();
}
