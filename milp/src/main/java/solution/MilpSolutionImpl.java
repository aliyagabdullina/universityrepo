package solution;

import pair.Pair;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MilpSolutionImpl implements MilpSolution {
    private final SolutionStatus _solutionStatus;
    private final Map<String, Double> _labelValueMap;
    private final OptionalDouble _objectiveValue;

    public MilpSolutionImpl(SolutionStatus solutionStatus, Stream<Pair<String, Double>> varLabelValueStream, OptionalDouble objective) {
        _solutionStatus = solutionStatus;
        _labelValueMap = varLabelValueStream
                .filter(pair -> !pair.getValue().equals(0.0))
                .collect(Collectors.toConcurrentMap(Pair::getKey, Pair::getValue));
        _objectiveValue = objective;
        checkState();
    }
    private void checkState() {
        if(!ifSolutionStatusAllowsVarValues()) {
            if(_objectiveValue.isPresent() || !_labelValueMap.isEmpty()) {
                throw new IllegalArgumentException("Solution status " + _solutionStatus + " doesn't allow to have variables/objective values");
            }
        }
    }

    @Override
    public double getValue(String varLabel) {
        if(ifSolutionStatusAllowsVarValues()) {
            return _labelValueMap.getOrDefault(varLabel, 0.0);
        } else  {
            throw new IllegalStateException("Solution status " + _solutionStatus + " doesn't allow to get variable values");
        }
    }

    private boolean ifSolutionStatusAllowsVarValues() {
        return switch (_solutionStatus) {
            case OPTIMAL, FEASIBLE -> true;
            default -> false;
        };
    }

    @Override
    public SolutionStatus getSolutionStatus() {
        return _solutionStatus;
    }

    @Override
    public OptionalDouble getObjective() {
        return _objectiveValue;
    }

    @Override
    public Stream<Pair<String, Double>> getNonZeroValues() {
        return _labelValueMap
                .entrySet()
                .stream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()));
    }
}
