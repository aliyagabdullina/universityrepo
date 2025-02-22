package model;

import model.constraint.LinearConstraint;
import model.expressions.LinearExpression;
import model.variables.Variable;

import java.util.Optional;
import java.util.stream.Stream;

public interface MilpModel {
    String getName();
    Stream<Variable> getVariablesStream();
    Stream<LinearConstraint> getConstraintsStream();
    ObjectiveType getObjectiveType();
    Optional<LinearExpression> getObjectiveExpression();
}
