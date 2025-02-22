package model.variables;

import java.util.OptionalDouble;
import java.util.OptionalInt;

public class Variable_IntegerImpl implements Variable {
    private final String _label;
    private OptionalInt _lb;
    private OptionalInt _ub;
    public Variable_IntegerImpl(String label, OptionalInt lb, OptionalInt ub) {
        _label = label;
        _lb = lb;
        _ub = ub;
        checkBounds(lb, ub);
    }

    void checkBounds(OptionalInt lb, OptionalInt ub) {
        if(lb.isPresent() && ub.isPresent() && (lb.getAsInt() > ub.getAsInt())) {
            throw new IllegalArgumentException("Incorrect int variable domain: " + lb + " " + ub);
        }
    }

    @Override
    public String getLabel() {
        return _label;
    }

    @Override
    public VariableType getType() {
        return VariableType.INTEGER;
    }

    @Override
    public OptionalDouble getUb() {
        return _ub.isPresent() ? OptionalDouble.of(_ub.getAsInt()) : OptionalDouble.empty();
    }

    @Override
    public OptionalDouble getLb() {
        return _lb.isPresent() ? OptionalDouble.of(_lb.getAsInt()) : OptionalDouble.empty();
    }
}
