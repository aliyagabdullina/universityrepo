package time;

import java.time.DayOfWeek;

public interface WeeklyTimeSlot extends Comparable<WeeklyTimeSlot>{
    DayOfWeek getDayOfWeek();
    long getStartTimeInMs();
    long getEndTimeInMs();
    boolean ifIntersects(WeeklyTimeSlot timeSlot);
    @Override
    int compareTo(WeeklyTimeSlot o);

}
