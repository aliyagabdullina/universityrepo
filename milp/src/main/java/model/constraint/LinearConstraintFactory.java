package model.constraint;

import java.util.stream.Stream;

public interface LinearConstraintFactory {
    LinearConstraint createFixVariableValue(String label, String varLabel, double value);
    LinearConstraint createVarSumBoundUb(String label, Stream<String> varLabels, double ub);
    LinearConstraint createVarSumBoundLb(String nextConstraintLabel, Stream<String> varNames, double v);
    LinearConstraint createVarSumEquals(String label, Stream<String> varLabels, double constant);
    LinearConstraint createAssignmentConstraint(String label, Stream<String> varLabels);
    LinearConstraint createSelectionConstraint(String label, String selectionVar, Stream<String> vars);
    LinearConstraint createBinaryBlockingConstraint(String label, String blockingVar, Stream<String> vars);

    LinearConstraint createVarGroupsSumEquality(String label, Stream<String> varsA, Stream<String> varsB);


}
