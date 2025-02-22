package place;


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class PlaceImpl implements Place {
    private final String _name;
    private final Building _building;
    private final Set<String> _tags = new HashSet<>();


    public PlaceImpl(String name, Building building) {
        _name = name;
        _building = building;
    }

    public PlaceImpl(String name) {
        _name = name;
        _building = Building.createEmpty();
    }

    @Override
    public Building getBuilding() {
        return _building;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public Stream<String> getTags() {
        return _tags.stream();
    }

    @Override
    public void addTag(String tag) {
        _tags.add(tag);
    }

    @Override
    public boolean hasTag(String tag) {
        return _tags.contains(tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaceImpl place = (PlaceImpl) o;
        return Objects.equals(_name, place._name) && Objects.equals(_building, place._building);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_name, _building);
    }

    @Override
    public String toString() {
        return _name;
    }
}
