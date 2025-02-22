package model.constraint;

import model.expressions.LinearExpression;

public class LinearConstraintImpl implements LinearConstraint {
    private final String _label;
    private final LinearExpression _linearExpression;
    private final ConstraintType _constraintType;
    private double _constant;

    public LinearConstraintImpl(String label, LinearExpression linearExpression, ConstraintType constraintType, double constant) {
        _label = label;
        _linearExpression = linearExpression;
        _constraintType = constraintType;
        _constant = constant;
    }

    @Override
    public String getLabel() {
        return _label;
    }

    @Override
    public ConstraintType getConstraintType() {
        return _constraintType;
    }

    @Override
    public double getConstant() {
        return _constant - _linearExpression.getConstant();
    }

    @Override
    public LinearExpression getExpression() {
        return _linearExpression;
    }
}
