package constraint;

public interface OptionalConstraintsSettings {
    void setHard(OptionalSchedulingConstraint constraint);
    void setOptional(OptionalSchedulingConstraint constraint, int coefficient);
    boolean ifHard(OptionalSchedulingConstraint constraint);
}
