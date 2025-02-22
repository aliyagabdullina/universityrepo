package model;

import model.constraint.LinearConstraint;
import model.expressions.LinearExpression;
import model.expressions.LinearExpressionImpl;
import model.variables.Variable;
import pair.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MilpModelBuilderImpl implements MilpModelBuilder {
    private final String _modelName;
    private Map<String, Variable> _variablesMap = new ConcurrentHashMap<>();
    private Map<String, LinearConstraint> _constraintsMap = new ConcurrentHashMap<>();
    private Optional<LinearExpression> _objectiveExpression = Optional.empty();
    private ObjectiveType _objectiveType = ObjectiveType.FEASIBILITY;
    private List<Stream<Pair<String, Double>>> _objectiveExpressionBuffer = new ArrayList<>();

    public MilpModelBuilderImpl(String modelName) {
        _modelName = modelName;
    }

    @Override
    public void addVariable(Variable variable) {
        if(_variablesMap.containsKey(variable.getLabel())) {
            throw new IllegalArgumentException("Variable with id " + variable.getLabel() + " already defined in the scope");
        }
        _variablesMap.put(variable.getLabel(), variable);
    }

    @Override
    public void addConstraint(LinearConstraint constraint) {
        if(_constraintsMap.containsKey(constraint.getLabel())) {
            throw new IllegalArgumentException("Constraint with id " + constraint.getLabel() + " already defined in the scope");
        }
        _constraintsMap.put(constraint.getLabel(), constraint);
    }

    @Override
    public void setObjective(LinearExpression expression, ObjectiveType objectiveType) {
        _objectiveExpression = Optional.of(expression);
        _objectiveType = objectiveType;
    }

    @Override
    public MilpModel getModel() {

        return _objectiveExpression
                .map(expression -> new MilpModelImpl(_modelName, _variablesMap.values().stream(), _constraintsMap.values().stream(), _objectiveType, expression))
                .orElseGet(() -> {
                    if(_objectiveExpressionBuffer.size() > 0) {
                        LinearExpression expression = createExpressionByBuffer();
                        return new MilpModelImpl(_modelName, _variablesMap.values().stream(), _constraintsMap.values().stream(), _objectiveType, expression);
                    }
                   return new MilpModelImpl(_modelName, _variablesMap.values().stream(), _constraintsMap.values().stream());
                });
    }

    private LinearExpression createExpressionByBuffer() {
        var varMap = _objectiveExpressionBuffer.stream()
                .flatMap(stream -> stream)
                .collect(Collectors.groupingBy(Pair::getKey));
        // THIS IS NOT THE OPTIMAL WAY
        // IT CAN BE DONE USING COLLECTOR
        Stream<Pair<String, Double>> pairStream = varMap.entrySet()
                .stream()
                .map(entry -> {
                    double value = entry.getValue()
                            .stream()
                            .mapToDouble(Pair::getValue)
                            .sum();
                   return new Pair<>(entry.getKey(), value);
                })
                .filter(pair -> Double.compare(pair.getValue(), 0.0) != 0);
        return new LinearExpressionImpl(pairStream, 0.0);
    }

    @Override
    public void addToObjective(Stream<Pair<String, Double>> varCoefficientStream) {
        _objectiveExpressionBuffer.add(varCoefficientStream);
    }

    @Override
    public void setObjectiveType(ObjectiveType objectiveType) {
        _objectiveType = objectiveType;
    }
}
