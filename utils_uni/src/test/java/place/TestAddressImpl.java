package place;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestAddressImpl {
    Address address;

    @BeforeEach
    void setUp() {
        var street = "Sobinova";
        var houseId = "22a";
        address = new AddressImpl(street, houseId);
    }

    @Test
    void testGetStreet() {
        assertEquals("Sobinova", address.getStreet());
    }

    @Test
    void testGetHomeNumber() {
        assertEquals("22a", address.getHouseId());
    }

    @Test
    void testEquals() {
        assertEquals(new AddressImpl("Sobinova", "22a"), address);
    }
}