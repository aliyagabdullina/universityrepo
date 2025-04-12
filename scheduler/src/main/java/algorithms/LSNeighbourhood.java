package algorithms;

import java.util.List;

public interface LSNeighbourhood {
    LSMove getBestMove(List<LSMove> tabuList);
}
