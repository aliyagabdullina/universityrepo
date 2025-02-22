package model.constraint;

import model.expressions.LinearExpression;
import model.expressions.LinearExpressionImpl;
import pair.Pair;

import java.util.List;
import java.util.stream.Stream;

public class LinearConstraintFactoryImpl implements LinearConstraintFactory {
    @Override
    public LinearConstraint createFixVariableValue(String label, String varLabel, double value) {
        LinearExpression linearExpression = new LinearExpressionImpl( Stream.of(new Pair<>(varLabel, 0.0)), 0.0);
        return new LinearConstraintImpl(label, linearExpression , ConstraintType.EQUALS, value);
    }

    @Override
    public LinearConstraint createVarSumBoundUb(String label, Stream<String> varLabels, double ub) {

        LinearExpression linearExpression = new LinearExpressionImpl( createOneCoefficientVarStream(varLabels), 0.0);
        return new LinearConstraintImpl(label, linearExpression , ConstraintType.LESS_OR_EQUAL, ub);
    }

    @Override
    public LinearConstraint createVarSumBoundLb(String label, Stream<String> varNames, double lb) {

        LinearExpression linearExpression = new LinearExpressionImpl( createOneCoefficientVarStream(varNames), 0.0);
        return new LinearConstraintImpl(label, linearExpression , ConstraintType.MORE_OR_EQUAL, lb);
    }

    @Override
    public LinearConstraint createVarSumEquals(String label, Stream<String> varLabels, double constant) {
        LinearExpression linearExpression = new LinearExpressionImpl( createOneCoefficientVarStream(varLabels), 0.0);
        return new LinearConstraintImpl(label, linearExpression , ConstraintType.EQUALS, constant);
    }

    @Override
    public LinearConstraint createAssignmentConstraint(String label, Stream<String> varLabels) {
        LinearExpression linearExpression = new LinearExpressionImpl( createOneCoefficientVarStream(varLabels), 0.0);
        return new LinearConstraintImpl(label, linearExpression , ConstraintType.EQUALS, 1.0);
    }

    @Override
    public LinearConstraint createSelectionConstraint(String label, String selectionVar, Stream<String> varLabels) {
        LinearExpression linearExpression = new LinearExpressionImpl( Stream.concat(Stream.of(new Pair<>(selectionVar, -1.0)), createOneCoefficientVarStream(varLabels)), 0.0);
        return new LinearConstraintImpl(label, linearExpression , ConstraintType.EQUALS, 0.0);
    }

    @Override
    public LinearConstraint createBinaryBlockingConstraint(String label, String blockingVar, Stream<String> vars) {
        List<String> varList = vars.toList();
        LinearExpression linearExpression = new LinearExpressionImpl( Stream.concat(Stream.of(new Pair<>(blockingVar, -1.0 * varList.size())), createOneCoefficientVarStream(varList.stream())), 0.0);
        return new LinearConstraintImpl(label, linearExpression , ConstraintType.LESS_OR_EQUAL, 0.0);
    }

    @Override
    public LinearConstraint createVarGroupsSumEquality(String label, Stream<String> varsA, Stream<String> varsB) {
        var streamA = varsA
                .map(var -> new Pair<>(var, 1.0));
        var streamB = varsB
                .map(var -> new Pair<>(var, -1.0));
        LinearExpression linearExpression = new LinearExpressionImpl(Stream.concat(streamA,streamB), 0.0);
        return new LinearConstraintImpl(label, linearExpression , ConstraintType.EQUALS, 0.0);
    }

    private Stream<Pair<String, Double>> createOneCoefficientVarStream(Stream<String> varLabels) {
        return varLabels
                .map(varLabel -> new Pair<>(varLabel, 1.0));
    }
}
