package model.variables;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VariableFactoryImpl implements VariableFactory {

    @Override
    public List<Variable> createBinaryVariableArray(int size, IntFunction<String> toLabel) {
        return IntStream.range(0, size)
                .mapToObj(i -> (Variable)new Variable_BinaryImpl(toLabel.apply(i)))
                .toList();
    }
}
