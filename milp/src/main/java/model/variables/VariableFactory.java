package model.variables;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.IntFunction;

public interface VariableFactory {
    List<Variable> createBinaryVariableArray(int size, IntFunction<String> toLabel);

}
