package time;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchoolShiftImpl implements SchoolShift {
    private final String _name;
    private final Map<DayOfWeek, List<WeeklyTimeSlot>> _timeSlotsPerDay;

    public SchoolShiftImpl(String name, Stream<WeeklyTimeSlot> timeSlotStream) {
        _name = name;
        _timeSlotsPerDay = timeSlotStream
                .collect(Collectors.groupingBy(WeeklyTimeSlot::getDayOfWeek, Collectors.toList()));
        _timeSlotsPerDay.values().forEach(list -> list.sort(WeeklyTimeSlot::compareTo));
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public List<WeeklyTimeSlot> getTimeSlotsForDay(DayOfWeek dayOfWeek) {
        List<WeeklyTimeSlot> list = _timeSlotsPerDay.getOrDefault(dayOfWeek, new ArrayList<>());
        return new ArrayList<>(list);
    }

    @Override
    public String toString() {
        return  _name;
    }
}
