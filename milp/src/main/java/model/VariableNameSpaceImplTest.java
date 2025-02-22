package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VariableNameSpaceImplTest {
    VariableNameSpace nameSpace;

    @BeforeEach
    void setUp() {
        nameSpace = new VariableNameSpaceImpl();
    }

    @Test
    void testAddVariableNames() {
        nameSpace.registerVariableName("A", 3);
        String varName = nameSpace.getVariableName("A", List.of("x", "y", "z"));
        assertNotNull(varName);
        var list = nameSpace.getVariableIndices(varName);
        assertNotNull(list);
        assertEquals("x", list.get(0));
        assertEquals("y", list.get(1));
        assertEquals("z", list.get(2));
    }

    @Test
    void testGetVariableNameException() {
        assertThrowsExactly(IllegalStateException.class, () -> nameSpace.getVariableName("A", List.of("x")));
    }

    @Test
    void testGetNextAuxVarName() {
        assertNotNull(nameSpace.getNextAuxVarName());
    }

    @Test
    void testIfLabeledBy() {
        assertTrue(nameSpace.ifLabeledBy("A_{x}", "A"));
        assertFalse(nameSpace.ifLabeledBy("B_{x}", "A"));
    }
}