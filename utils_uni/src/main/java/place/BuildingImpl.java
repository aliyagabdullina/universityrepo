package place;

import java.util.Objects;

public class BuildingImpl implements Building {
    private final String _name;
    private final Address _address;

    public BuildingImpl(String name, Address address) {
        _name = name;
        _address = address;
    }


    @Override
    public Address getAddress() {
        return _address;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildingImpl building = (BuildingImpl) o;
        return Objects.equals(_name, building._name) && Objects.equals(_address, building._address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_name, _address);
    }
}
