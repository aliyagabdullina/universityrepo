package constraint;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class OptionalConstraintsSettingsImpl implements OptionalConstraintsSettings {
    private Set<OptionalSchedulingConstraint> _hardConstraints = new HashSet<>();
    private Map<OptionalSchedulingConstraint, Integer> _optionalConstraintCoefficientMap = new ConcurrentHashMap<>();

    @Override
    public void setHard(OptionalSchedulingConstraint constraint) {
        _hardConstraints.add(constraint);
        _optionalConstraintCoefficientMap.remove(constraint);
    }

    @Override
    public void setOptional(OptionalSchedulingConstraint constraint, int coefficient) {
        _hardConstraints.remove(constraint);
        _optionalConstraintCoefficientMap.put(constraint, coefficient);
    }

    @Override
    public boolean ifHard(OptionalSchedulingConstraint constraint) {
        return _hardConstraints.contains(constraint);
    }
}
