package model.variables;

import org.junit.jupiter.api.*;

import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;


class TestVariable_IntegerImpl {
    Variable closeIntVar;
    Variable openLbIntVar;
    Variable openUbIntVar;

    @BeforeEach
    void setUp() {
        closeIntVar = new Variable_IntegerImpl("lab_1", OptionalInt.of(2), OptionalInt.of(10));
        openLbIntVar = new Variable_IntegerImpl("lab_2", OptionalInt.empty(),OptionalInt.of(10));
        openUbIntVar = new Variable_IntegerImpl("lab_3", OptionalInt.of(2), OptionalInt.empty());
    }

    @Test
    void testConstructor() {
        String id = "label";
        assertThrows(IllegalArgumentException.class, () -> new Variable_IntegerImpl(id, OptionalInt.of(10), OptionalInt.of(5)));
        assertDoesNotThrow(() -> new Variable_IntegerImpl(id, OptionalInt.of(5), OptionalInt.of(50)));
        assertDoesNotThrow(() -> new Variable_IntegerImpl(id, OptionalInt.empty(), OptionalInt.of(5)));
        assertDoesNotThrow(() -> new Variable_IntegerImpl(id, OptionalInt.of(5), OptionalInt.empty()));
        assertDoesNotThrow(() -> new Variable_IntegerImpl(id, OptionalInt.empty(), OptionalInt.empty()));
    }

    @Test
    void testGetId() {
        assertEquals("lab_1", closeIntVar.getLabel());
    }

    @Test
    void testGetType() {
        assertEquals(VariableType.INTEGER, closeIntVar.getType());
    }

    @Test
    void testGetUbClose() {
        assertTrue( closeIntVar.getUb().isPresent());
        assertEquals(10.0, closeIntVar.getUb().getAsDouble());
    }

    @Test
    void testGetUbOpen() {
        assertTrue(openUbIntVar.getUb().isEmpty());
    }

    @Test
    void testGetLbClose() {
        assertTrue( closeIntVar.getLb().isPresent());
        assertEquals(2.0, closeIntVar.getLb().getAsDouble());
    }

    @Test
    void testGetLbOpen() {
        assertTrue(openLbIntVar.getLb().isEmpty());
    }

}