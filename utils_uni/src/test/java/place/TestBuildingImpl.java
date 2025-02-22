package place;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestBuildingImpl {
    Building building;

    @BeforeEach
    void setUp() {
        var address = new AddressImpl("Sobinova", "22a");
        building = new BuildingImpl("School33", address);
    }

    @Test
    void testGetAddress() {
        assertNotNull(building.getAddress());
    }

    @Test
    void testGetBuildingName() {
        assertEquals("School33", building.getName());
    }

    @Test
    void testEquals() {
        assertEquals(new BuildingImpl("School33", new AddressImpl("Sobinova", "22a")), building);
    }
}