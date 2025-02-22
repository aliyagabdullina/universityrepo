package constraint.timeConstraint;

import constraint.ScheduleConstraint;
import time.WeeklyTimeSlot;

import java.util.stream.Stream;

public interface ForbiddenTimeConstraint<T> extends ScheduleConstraint {
    T getRelatedObject();
    Stream<WeeklyTimeSlot> forbiddenTimeSlots();
    boolean isNotForbidden(WeeklyTimeSlot timeSlot);
}
