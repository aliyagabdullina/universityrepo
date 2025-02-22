package model.variables;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

class TestVariableFactoryImpl {
    VariableFactory factory;
    @BeforeEach
    void setUp() {
        factory = new VariableFactoryImpl();
    }

    @Test
    void testCreateBinaryVariableArray() {
        assertNotNull(factory.createBinaryVariableArray(0, i -> "name_" + i));
        assertEquals(10, factory.createBinaryVariableArray(10, i -> "name_" + i).size());
    }
}