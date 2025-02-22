package constraint.timeConstraint;

import time.WeeklyTimeSlot;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MandatoryTimeConstraintImpl implements MandatoryTimeConstraint {
    private final Set<WeeklyTimeSlot> _timeSlots;

    public MandatoryTimeConstraintImpl(Stream<WeeklyTimeSlot> mandatoryTimeSlots) {
        _timeSlots = mandatoryTimeSlots.collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Stream<WeeklyTimeSlot> forbiddenTimeSlots() {
        return _timeSlots.stream();
    }

    @Override
    public boolean isMandatory(WeeklyTimeSlot timeSlot) {
        return _timeSlots.contains(timeSlot);
    }
}
