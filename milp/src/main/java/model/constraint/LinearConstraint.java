package model.constraint;

import model.expressions.LinearExpression;

public interface LinearConstraint {
    String getLabel();
    ConstraintType getConstraintType();
    double getConstant();
    LinearExpression getExpression();
}
