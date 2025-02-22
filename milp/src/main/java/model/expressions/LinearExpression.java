package model.expressions;

import pair.Pair;

import java.util.function.Function;
import java.util.stream.Stream;

public interface LinearExpression {
    double getConstant();
    double getCoefficient(String varLabel);
    double calculate(Function<String, Double> varLabelToValue);
    Stream<Pair<String, Double>> getVarLabelCoefficientStream();
}
