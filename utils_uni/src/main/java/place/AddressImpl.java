package place;

import java.util.Objects;

public class AddressImpl implements Address {
    private final String _street;
    private final String _houseId;

    public AddressImpl(String street, String houseId) {
        _street = street;
        _houseId = houseId;
    }

    @Override
    public String getStreet() {
        return _street;
    }

    @Override
    public String getHouseId() {
        return _houseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressImpl address = (AddressImpl) o;
        return Objects.equals(_street, address._street) && Objects.equals(_houseId, address._houseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_street, _houseId);
    }
}
