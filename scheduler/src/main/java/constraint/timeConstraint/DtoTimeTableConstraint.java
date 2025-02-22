package constraint.timeConstraint;

import pair.Pair;
import time.WeeklyTimeSlot;

import java.util.List;
import java.util.stream.Stream;

public class DtoTimeTableConstraint {
    public String _constraintName;
    public Object _schedulingObject;
    public List<Pair<WeeklyTimeSlot, TimeSlotStatus>> timeSlotStatusList;

    public DtoTimeTableConstraint(String constraintName, Object schedulingObject, Stream<Pair<WeeklyTimeSlot, TimeSlotStatus>> timeSlotStatusStream) {
        _constraintName = constraintName;
        _schedulingObject = schedulingObject;
        timeSlotStatusList = timeSlotStatusStream.toList();
    }
}
