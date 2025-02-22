package place;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestPlaceImpl {
    Place place;
    @BeforeEach
    void setUp() {
        var address = new AddressImpl("Sobinova", "22a");
        var building = new BuildingImpl("School33", address);
        place = new PlaceImpl("Cabinet 1", building);
    }

    @Test
    void testGetBuilding() {
        assertNotNull(place.getBuilding());
        assertEquals("School33", place.getBuilding().getName());
    }

    @Test
    void testEquals() {
        var address = new AddressImpl("Sobinova", "22a");
        var building = new BuildingImpl("School33", address);
        var expectedPlace = new PlaceImpl("Cabinet 1", building);
        assertEquals(expectedPlace, place);
    }

    @Test
    void testGetName() {
        assertEquals("Cabinet 1", place.getName());
    }
}