package interval;

import java.util.Objects;

public class IntervalImpl<T extends Comparable<T>> implements Interval<T> {
    private final T _start;
    private final T _end;

    public IntervalImpl(T start, T end) {
        if(start.compareTo(end) > 0) {
            throw new IllegalArgumentException("interval.Interval start " + start + " should be less or equal to end " + end);
        }
        _start = start;
        _end = end;
    }

    @Override
    public T getStart() {
        return _start;
    }

    @Override
    public T getEnd() {
        return _end;
    }

    @Override
    public boolean ifIntersects(Interval<T> t) {
        var tStart = t.getStart();
        var tEnd = t.getEnd();
        return _start.compareTo(t.getEnd()) < 0 && _end.compareTo(t.getStart()) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntervalImpl<?> interval = (IntervalImpl<?>) o;
        return Objects.equals(_start, interval._start) && Objects.equals(_end, interval._end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_start, _end);
    }
}
