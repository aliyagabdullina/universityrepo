package course;

import java.time.DayOfWeek;

public interface CourseProgramDailyTarget {
    DailyTargetType getTargetType();
    DayOfWeek getDayOfWeek();
    int getValue();
}
