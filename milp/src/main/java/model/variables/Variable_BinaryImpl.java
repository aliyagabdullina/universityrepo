package model.variables;

import java.util.OptionalDouble;

public class Variable_BinaryImpl implements Variable {
    private final String _label;
    private short _lb;
    private short _ub;

    public Variable_BinaryImpl(String label) {
        _label = label;
        _lb = 0;
        _ub = 1;
    }


    @Override
    public String getLabel() {
        return _label;
    }

    @Override
    public VariableType getType() {
        return VariableType.BINARY;
    }

    @Override
    public OptionalDouble getUb() {
        return OptionalDouble.of(_ub);
    }

    @Override
    public OptionalDouble getLb() {
        return  OptionalDouble.of(_lb);
    }
}
