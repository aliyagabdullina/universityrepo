package model;

import model.constraint.LinearConstraint;
import model.expressions.LinearExpression;
import model.variables.Variable;
import pair.Pair;

import java.util.stream.Stream;

public interface MilpModelBuilder {
    void addVariable(Variable variable);
    void addConstraint(LinearConstraint constraint);
    void setObjective(LinearExpression expression, ObjectiveType objectiveType);
    MilpModel getModel();

    void addToObjective(Stream<Pair<String, Double>> varCoefficientStream);
    void setObjectiveType(ObjectiveType objectiveType);
}
