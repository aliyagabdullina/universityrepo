package time;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Stream;

public interface SchoolShift {
    String getName();
    List<WeeklyTimeSlot> getTimeSlotsForDay(DayOfWeek dayOfWeek);
}
