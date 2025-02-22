package time;


import interval.Interval;
import interval.IntervalImpl;

import java.time.DayOfWeek;
import java.util.Objects;

public class WeeklyTimeSlotImpl implements WeeklyTimeSlot {

    private final DayOfWeek _weekDay;
    private final Interval<Long> _intervalInMs;

    public WeeklyTimeSlotImpl(DayOfWeek weekDay, long startTimeInMs, long endTimeInMs) {
        this._intervalInMs = new IntervalImpl<>(startTimeInMs,endTimeInMs);
        this._weekDay = weekDay;

    }



    @Override
    public boolean ifIntersects(WeeklyTimeSlot timeSlot) {
        return timeSlot.getDayOfWeek().equals(_weekDay) &&
            _intervalInMs.ifIntersects(new IntervalImpl<>(timeSlot.getStartTimeInMs(), timeSlot.getEndTimeInMs()));
    }

    @Override
    public DayOfWeek getDayOfWeek() {
        return _weekDay;
    }


    @Override
    public long getStartTimeInMs() {
        return _intervalInMs.getStart();
    }

    @Override
    public long getEndTimeInMs() {
        return _intervalInMs.getEnd();
    }


    @Override
    public int compareTo(WeeklyTimeSlot t) {
        int weekDayComparison = _weekDay.compareTo(t.getDayOfWeek());
        if(weekDayComparison != 0)
            return weekDayComparison;
        int startTimeCompare = Long.compare(_intervalInMs.getStart(), t.getStartTimeInMs());
        if(startTimeCompare != 0 )
            return startTimeCompare;
        return Long.compare(_intervalInMs.getEnd(), t.getEndTimeInMs());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeeklyTimeSlotImpl that = (WeeklyTimeSlotImpl) o;
        return _weekDay == that._weekDay && Objects.equals(_intervalInMs, that._intervalInMs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_weekDay, _intervalInMs);
    }
}
