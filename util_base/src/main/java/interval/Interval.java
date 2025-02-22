package interval;

public interface Interval<T extends Comparable<T>> {
    T getStart();
    T getEnd();
    boolean ifIntersects(Interval<T> t);
}
