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
                new VariableDescription("a", Type.INT, initialValue, true);
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
     * After beginning a new scope, ending a scope should be possible.
     */
    @Test
    void beginAndEndScope() {
        symbolTable.beginScope();
        final boolean success = symbolTable.endScope();
        assertTrue(success);
    }

    /**
     * After beginning two new scopes, ending them should be possible.
     */
    @Test
    void beginAndEndScopeTwiceNestedly() {
        symbolTable.beginScope();
        symbolTable.beginScope();
        final boolean successFirst = symbolTable.endScope();
        final boolean successSecond = symbolTable.endScope();
        assertTrue(successFirst);
        assertTrue(successSecond);
    }

    /**
     * After beginning a new scope and ending it it should be possible to begin and end another one.
     */
    @Test
    void beginAndEndScopeTwiceSequentially() {
        symbolTable.beginScope();
        symbolTable.endScope();
        symbolTable.beginScope();
        final boolean success = symbolTable.endScope();
        assertTrue(success);
    }

    /**
     * It should not be possible to end a scope without beginning one first.
     */
    @Test
    void endScope() {
        final boolean success = symbolTable.endScope();
        assertFalse(success);
    }

    /**
     * It should not be possible to end more scopes than were started before.
     */
    @Test
    void endMoreScopesThanWereBegan() {
        symbolTable.beginScope();
        symbolTable.endScope();
        final boolean success = symbolTable.endScope();
        assertFalse(success);
    }

    /**
     * Adding a VariableDescription to a SymbolTable scope should enable finding it from a new one.
     */
    @Test
    void findInsertedVariableDescriptionInEnclosingScope() {
        final VariableDescription varDesc = new VariableDescription("a", Type.INT, 5, true);
        symbolTable.insert(varDesc);
        symbolTable.beginScope();
        final ObjectDescription objDesc = symbolTable.find(varDesc);
        assertSame(objDesc, varDesc);
    }

    /**
     * Adding a VariableDescription to a SymbolTable scope should enable finding it from two more
     * scopes.
     */
    @Test
    void findInsertedVariableDescriptionInSecondEnclosingScope() {
        final VariableDescription varDesc = new VariableDescription("a", Type.INT, 5, true);
        symbolTable.insert(varDesc);
        symbolTable.beginScope();
        symbolTable.beginScope();
        final ObjectDescription objDesc = symbolTable.find(varDesc);
        assertSame(objDesc, varDesc);
    }

    /**
     * Adding a VariableDescription to a scope in the SymbolTable and ending that scope should deny
     * finding it later on.
     */
    @Test
    void DontFindInsertedVariableDescriptionInEndedScope() {
        final VariableDescription varDesc = new VariableDescription("a", Type.INT, 5, true);
        symbolTable.beginScope();
        symbolTable.insert(varDesc);
        symbolTable.endScope();
        final ObjectDescription objDesc = symbolTable.find(varDesc);
        assertNull(objDesc);
    }
}