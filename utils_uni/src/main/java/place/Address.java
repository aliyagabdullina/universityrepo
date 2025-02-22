package place;

public interface Address {
    String getStreet();
    String getHouseId();

    static Address createEmpty() {
        return new AddressImpl("", "");
    }
}
