package model.expressions;

import pair.Pair;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LinearExpressionImpl implements LinearExpression {
    private final Map<String, Double> _varLabelCoefficientMap;
    private double _const;

    public LinearExpressionImpl(Stream<Pair<String, Double>> varCoefficientStream, double aConst) {
        _varLabelCoefficientMap = createVarIdCoefficientMap(varCoefficientStream);
        _const = aConst;
    }

    private Map<String, Double> createVarIdCoefficientMap(Stream<Pair<String, Double>> varCoefficientStream) {
        return varCoefficientStream
                .collect(Collectors.toConcurrentMap(Pair::getKey, Pair::getValue));
    }

    @Override
    public double getConstant() {
        return _const;
    }

    @Override
    public double getCoefficient(String varLabel) {
        return _varLabelCoefficientMap.getOrDefault(varLabel, 0.0);
    }

    @Override
    public double calculate(Function<String, Double> varLabelToValue) {
        double varCoeffSum =  _varLabelCoefficientMap.entrySet()
                .stream()
                .mapToDouble(entry -> {
                    String varLabel = entry.getKey();
                    double coefficient = entry.getValue();
                    return coefficient * varLabelToValue.apply(varLabel);
                })
                .sum();
        return varCoeffSum + _const;
    }

    @Override
    public Stream<Pair<String, Double>> getVarLabelCoefficientStream() {
        return _varLabelCoefficientMap.entrySet()
                .stream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()));
    }
}
