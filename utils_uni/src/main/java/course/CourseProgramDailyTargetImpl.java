package course;

import java.time.DayOfWeek;
import java.util.Objects;

public class CourseProgramDailyTargetImpl implements CourseProgramDailyTarget {
    private final DailyTargetType _targetType;
    private final DayOfWeek _dayOfWeek;
    private final int _value;

    public CourseProgramDailyTargetImpl(DailyTargetType targetType, DayOfWeek dayOfWeek, int value) {
        _targetType = targetType;
        _dayOfWeek = dayOfWeek;
        _value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseProgramDailyTargetImpl that = (CourseProgramDailyTargetImpl) o;
        return _value == that._value && Objects.equals(_targetType, that._targetType) && _dayOfWeek == that._dayOfWeek;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_targetType, _dayOfWeek, _value);
    }

    @Override
    public DailyTargetType getTargetType() {
        return _targetType;
    }

    @Override
    public DayOfWeek getDayOfWeek() {
        return _dayOfWeek;
    }

    @Override
    public int getValue() {
        return _value;
    }
}
