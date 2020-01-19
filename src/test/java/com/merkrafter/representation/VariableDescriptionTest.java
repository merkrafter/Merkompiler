package com.merkrafter.representation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests VariableDescriptions.
 */
class VariableDescriptionTest {

    /**
     * A constant VariableDescription should indicate the constant-ness.
     */
    @Test
    void isConstant() {
        final boolean constant = true;
        final VariableDescription varDesc = new VariableDescription("", Type.INT, 5, constant);
        assertTrue(varDesc.isConstant());
    }

    /**
     * A non-constant VariableDescription should indicate the non-constant-ness.
     */
    @Test
    void isNotConstant() {
        final boolean constant = false;
        final VariableDescription varDesc = new VariableDescription("", Type.INT, 5, constant);
        assertFalse(varDesc.isConstant());
    }

    /**
     * Setting a new value to a non-constant should change the output of the getValue method.
     */
    @Test
    void setValue() {
        final int oldValue = 3;
        final VariableDescription varDesc = new VariableDescription("", Type.INT, oldValue, false);
        final int newValue = 5;
        varDesc.setValue(newValue);
        assertEquals(newValue, varDesc.getValue());
    }

    /**
     * Setting the same value to a variable it had before should still indicate success.
     */
    @Test
    void setSameValue() {
        final int oldValue = 3;
        final VariableDescription varDesc = new VariableDescription("", Type.INT, oldValue, false);
        final int newValue = 3;
        varDesc.setValue(newValue);
        assertEquals(newValue, varDesc.getValue());
    }

    /**
     * Trying to assign a new value to a constant variable should not be successful. The old value
     * should be retained.
     */
    @Test
    void setValueToConstant() {
        final int oldValue = 3;
        final VariableDescription varDesc = new VariableDescription("", Type.INT, oldValue, true);
        final int newValue = 5;
        final boolean success = varDesc.setValue(newValue);
        assertFalse(success);
        assertEquals(oldValue, varDesc.getValue());
    }

    /**
     * Trying to assign a value with mismatched type to a variable should not be successful. The old
     * value should be retained.
     */
    @Test
    void setValueWithMismatchedType() {
        final int oldValue = 3;
        final VariableDescription varDesc = new VariableDescription("", Type.INT, oldValue, false);
        final String newValue = "some string";
        final boolean success = varDesc.setValue(newValue);
        assertFalse(success);
        assertEquals(oldValue, varDesc.getValue());
    }

    /**
     * A variable description should be considered equal to another one if they have the same names
     * and types.
     */
    @Test
    void testEquals() {
        final String name = "myVar";
        final Type type = Type.INT;
        final int value = 5;
        final boolean constant = false;
        final VariableDescription varDesc1 = new VariableDescription(name, type, value, constant);
        final VariableDescription varDesc2 = new VariableDescription(name, type, value, constant);
        assertEquals(varDesc1, varDesc2);
        assertEquals(varDesc2, varDesc1);
    }

    /**
     * A variable description should be considered equal to another one if they have the same names
     * and types, even if their constant-nesses are different.
     */
    @Test
    void testEqualsWithoutSameConstantness() {
        final String name = "myVar";
        final Type type = Type.INT;
        final int value = 5;
        final VariableDescription varDesc1 = new VariableDescription(name, type, value, true);
        final VariableDescription varDesc2 = new VariableDescription(name, type, value, false);
        assertEquals(varDesc1, varDesc2);
        assertEquals(varDesc2, varDesc1);
    }

    /**
     * A variable description should be considered equal to another one if they have the same names
     * and types, even if their values are different.
     */
    @Test
    void testEqualsWithoutSameValues() {
        final String name = "myVar";
        final Type type = Type.INT;
        final int value1 = 5;
        final int value2 = 8;
        final boolean constant = true;
        final VariableDescription varDesc1 = new VariableDescription(name, type, value1, constant);
        final VariableDescription varDesc2 = new VariableDescription(name, type, value2, constant);
        assertEquals(varDesc1, varDesc2);
        assertEquals(varDesc2, varDesc1);
    }

    /**
     * A variable description should NOT be considered equal to another one if they have
     * different names.
     */
    @Test
    void testNotEqualsWithDifferentNames() {
        final String name1 = "myVar";
        final String name2 = "myOtherVar";
        final Type type = Type.INT;
        final int value = 5;
        final boolean constant = false;
        final VariableDescription varDesc1 = new VariableDescription(name1, type, value, constant);
        final VariableDescription varDesc2 = new VariableDescription(name2, type, value, constant);
        assertNotEquals(varDesc1, varDesc2);
        assertNotEquals(varDesc2, varDesc1);
    }

    /**
     * A variable description should NOT be considered equal to another one if they have
     * different types.
     */
    @Test
    void testNotEqualsWithDifferentTypes() {
        final String name = "myVar";
        final Type type1 = Type.INT;
        final Type type2 = Type.VOID;
        final int value = 5;
        final boolean constant = false;
        final VariableDescription varDesc1 = new VariableDescription(name, type1, value, constant);
        final VariableDescription varDesc2 = new VariableDescription(name, type2, value, constant);
        assertNotEquals(varDesc1, varDesc2);
        assertNotEquals(varDesc2, varDesc1);
    }

    /**
     * A VariableDescription should not be considered equal to null.
     */
    @ParameterizedTest
    @NullSource
    void testNotEqualsToNull(final Object other) {
        final String name = "myVar";
        final Type type = Type.INT;
        final int value = 5;
        final boolean constant = false;
        final VariableDescription varDesc = new VariableDescription(name, type, value, constant);
        assertNotEquals(varDesc, other);
    }

    /**
     * A VariableDescription should not be considered equal to objects that are no instances of
     * VariableDescription.
     */
    @Test
    void testNotEqualsToOtherThanVariableDescription() {
        final String name = "myVar";
        final Type type = Type.INT;
        final int value = 5;
        final boolean constant = false;
        final VariableDescription varDesc = new VariableDescription(name, type, value, constant);
        assertNotEquals(varDesc, "some string");
        assertNotEquals(varDesc, 1234);
        assertNotEquals(varDesc, new Object());
    }
}