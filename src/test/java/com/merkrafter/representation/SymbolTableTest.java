package com.merkrafter.representation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SymbolTableTest {
    private SymbolTable symbolTable;

    /**
     * Creates a new empty SymbolTable using the parameterless constructor.
     */
    @BeforeEach
    void setup() {
        symbolTable = new SymbolTable();
    }

    /**
     * Adding a VariableDescription to the SymbolTable should be successful.
     */
    @Test
    void insertVariableDescription() {
        final VariableDescription varDesc = new VariableDescription("a", Type.INT, 5, true);
        final boolean success = symbolTable.insert(varDesc);
        assertTrue(success);
    }

    /**
     * Adding the same VariableDescription to the SymbolTable twice should NOT be successful.
     */
    @Test
    void insertVariableDescriptionTwice() {
        final VariableDescription varDesc = new VariableDescription("a", Type.INT, 5, true);
        symbolTable.insert(varDesc);
        final boolean success = symbolTable.insert(varDesc);
        assertFalse(success);
    }

    /**
     * Adding a VariableDescription to the SymbolTable should enable finding it.
     */
    @Test
    void findInsertedVariableDescription() {
        final VariableDescription varDesc = new VariableDescription("a", Type.INT, 5, true);
        symbolTable.insert(varDesc);
        final ObjectDescription objDesc = symbolTable.find(varDesc);
        assertSame(objDesc, varDesc);
    }

    /**
     * Adding a VariableDescription to the SymbolTable should enable finding it even if the value of
     * the description changed meanwhile.
     */
    @Test
    void findInsertedVariableDescriptionAfterChangingValue() {
        final int initialValue = 5;
        final int newValue = 8;
        final VariableDescription varDesc =
                new VariableDescription("a", Type.INT, initialValue, false);
        symbolTable.insert(varDesc);
        varDesc.setValue(newValue);
        final ObjectDescription objDesc = symbolTable.find(varDesc);
        assertSame(objDesc, varDesc);
    }

    /**
     * Adding a VariableDescription to the SymbolTable should enable finding it even if a new
     * (prototype) object description is used to find it.
     */
    @Test
    void findInsertedVariableDescriptionWithNewPrototypeDescription() {
        final VariableDescription varDesc = new VariableDescription("a", Type.INT, 5, true);
        final VariableDescription prototype = new VariableDescription("a", Type.INT, 6, false);
        symbolTable.insert(varDesc);
        final ObjectDescription objDesc = symbolTable.find(prototype);
        assertSame(objDesc, varDesc);
    }

    /**
     * Adding a VariableDescription to the SymbolTable and searching for another one should not be
     * successful.
     */
    @Test
    void DontFindInsertedVariableDescriptionWithDifferentPrototype() {
        final VariableDescription varDesc = new VariableDescription("a", Type.INT, 5, true);
        final VariableDescription prototype = new VariableDescription("b", Type.INT, 5, true);
        symbolTable.insert(varDesc);
        final ObjectDescription objDesc = symbolTable.find(prototype);
        assertNull(objDesc);
    }

    /**
     * Searching for a variable in an enclosing symbol table (that is, an inner block, for instance
     * for a variable that was defined in the inner symbol table (that is, an outer block) should
     * return that desired variable description.
     */
    @Test
    void findVariableDescriptionInEnclosingTable() {
        final VariableDescription varDesc = new VariableDescription("a", Type.INT, 5, true);
        symbolTable.insert(varDesc);
        final SymbolTable innerSymbolTable = new SymbolTable(symbolTable);
        final ObjectDescription objDesc = innerSymbolTable.find(varDesc);
        assertSame(objDesc, varDesc);
    }
}