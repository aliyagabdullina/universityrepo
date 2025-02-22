package solution;

import pair.Pair;

import java.util.OptionalDouble;
import java.util.stream.Stream;

public interface MilpSolution {
    double getValue(String varLabel);
    SolutionStatus getSolutionStatus();
    OptionalDouble getObjective();

    Stream<Pair<String, Double>> getNonZeroValues();
}
