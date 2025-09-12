package algorithms;

import java.util.List;
import java.util.Optional;

public interface LSNeighbourhood {
    Optional<LSMove> getBestMove(List<LSMove> tabuList);
}
