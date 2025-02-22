package pair;

import java.util.Map;
import java.util.Objects;

public class Pair<K, V> {
    private final K _key;
    private final V _value;

    public Pair(K key, V value) {
        _key = key;
        _value = value;
    }

    public Pair(Map.Entry<K, V> entry) {
        _key = entry.getKey();
        _value = entry.getValue();
    }

    public K getKey() {
        return _key;
    }

    public V getValue() {
        return _value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(_key, pair._key) && Objects.equals(_value, pair._value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_key, _value);
    }
}
