package model;

import model.constraint.LinearConstraint;
import model.expressions.LinearExpression;
import model.variables.Variable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MilpModelImpl implements MilpModel {
    private final String _name;
    private final List<Variable> _variables;
    private final List<LinearConstraint> _constraints;
    private final ObjectiveType _objectiveType;
    private final Optional<LinearExpression> _objectiveExpression;

    public MilpModelImpl(String name, Stream<Variable> variableStream, Stream<LinearConstraint> constraintStream, ObjectiveType objectiveType, LinearExpression objectiveExpression) {
        _name = name;
        _variables = variableStream.toList();
        _objectiveType = objectiveType;
        _constraints = constraintStream.toList();
        _objectiveExpression = Optional.of(objectiveExpression);
    }

    public MilpModelImpl(String name, Stream<Variable> variableStream, Stream<LinearConstraint> constraintStream) {
        this._name = name;
        this._variables = variableStream.toList();
        _constraints = constraintStream.toList();
        _objectiveType = ObjectiveType.FEASIBILITY;
        _objectiveExpression = Optional.empty();
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public Stream<Variable> getVariablesStream() {
        return _variables.stream();
    }

    @Override
    public Stream<LinearConstraint> getConstraintsStream() {
        return _constraints.stream();
    }

    @Override
    public ObjectiveType getObjectiveType() {
        return _objectiveType;
    }

    @Override
    public Optional<LinearExpression> getObjectiveExpression() {
        return _objectiveExpression;
    }
}
