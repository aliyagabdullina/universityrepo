package constraint.timeConstraint;

import time.WeeklyTimeSlot;

import java.util.stream.Stream;

public interface MandatoryTimeConstraint {
    Stream<WeeklyTimeSlot> forbiddenTimeSlots();
    boolean isMandatory(WeeklyTimeSlot timeSlot);
}
