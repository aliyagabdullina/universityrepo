package model.variables;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestVariable_BinaryImpl {
    Variable binaryVariable;

    @BeforeEach
    void setUp() {
        binaryVariable = new Variable_BinaryImpl("lab_1");
    }

    @Test
    void testGetId() {
        assertEquals("lab_1", binaryVariable.getLabel());
    }

    @Test
    void testGetType() {
        assertEquals(VariableType.BINARY, binaryVariable.getType());
    }

    @Test
    void testGetUb() {
        assertTrue( binaryVariable.getUb().isPresent());
        assertEquals(1.0, binaryVariable.getUb().getAsDouble());
    }

    @Test
    void testGetLb() {
        assertTrue( binaryVariable.getLb().isPresent());
        assertEquals(0.0, binaryVariable.getLb().getAsDouble());
    }

}