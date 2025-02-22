package triplet;

import java.util.Objects;

public class Triplet<F, S, T> {
    private final F _first;
    private final S _second;
    private final T _third;

    public Triplet(F first, S second, T third) {
        _first = first;
        _second = second;
        _third = third;
    }

    public F getFirst() {
        return _first;
    }

    public S getSecond() {
        return _second;
    }

    public T getThird() {
        return _third;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>) o;
        return _first.equals(triplet._first) && _second.equals(triplet._second) && _third.equals(triplet._third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_first, _second, _third);
    }
}
