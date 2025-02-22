package constraint.assignment;

import constraint.ScheduleConstraint;

import java.util.stream.Stream;

public interface MandatoryAssignmentConstraint extends ScheduleConstraint {
    Stream<Object> getObjects();
}
