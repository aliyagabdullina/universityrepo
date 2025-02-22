package constraint.timeConstraint;

import time.WeeklyTimeSlot;

import java.time.DateTimeException;
import java.time.DayOfWeek;

public interface TimeTableRequest {
    DayOfWeek[] getDaysOfWeek();
    WeeklyTimeSlot[][] getTimeSlots();
    TimeSlotStatus[][] getStatuses();

}
