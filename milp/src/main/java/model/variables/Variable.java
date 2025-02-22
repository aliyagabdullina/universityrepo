package model.variables;

import java.util.OptionalDouble;

public interface Variable {
    String getLabel();
    VariableType getType();
    OptionalDouble getUb();
    OptionalDouble getLb();
}
