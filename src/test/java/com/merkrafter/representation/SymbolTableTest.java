package com.merkrafter.representation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

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

    /**
     * Adding two ProcedureDescriptions with parameters that only differ in their names to the
     * SymbolTable should not be allowed since calling those will be ambiguous.
     */
    @Test
    void dontInsertProcedureDescriptionWithDifferentParameterNames() {
        // definition of the actual procedure's parameter list
        final VariableDescription param1 = new VariableDescription("a", Type.INT, 5, true);
        final LinkedList<ObjectDescription> paramList1 = new LinkedList<>();
        paramList1.add(param1);

        // definition of the prototype param list; note the different name
        final VariableDescription param2 = new VariableDescription("b", Type.INT, 5, true);
        final LinkedList<ObjectDescription> paramList2 = new LinkedList<>();
        paramList2.add(param2);

        final ProcedureDescription proc1 =
                new ActualProcedureDescription(Type.INT, "main", paramList1, null);
        final ProcedureDescription proc2 =
                new ActualProcedureDescription(Type.INT, "main", paramList2, null);

        symbolTable.insert(proc1);
        assertFalse(symbolTable.insert(proc2));
    }

    /**
     * Adding a ProcedureDescription to the SymbolTable should enable finding it.
     * This covers a procedure without parameters and without local variable declarations.
     */
    @Test
    void findInsertedProcedureDescription() {
        final ProcedureDescription procDesc =
                new ActualProcedureDescription(Type.INT, "main", new LinkedList<>(), null);
        symbolTable.insert(procDesc);
        final ObjectDescription objDesc = symbolTable.find(procDesc);
        assertSame(objDesc, procDesc);
    }

    /**
     * Adding a ProcedureDescription to the SymbolTable and setting the enclosing symbol table to
     * the mentioned one should enable finding the procedure from its own symbol table, allowing
     * recursion.
     */
    @Test
    void findProcedureDescriptionInItsOwnSymbolTable() {
        final ProcedureDescription procDesc =
                new ActualProcedureDescription(Type.INT, "main", new LinkedList<>(), symbolTable);
        symbolTable.insert(procDesc);
        final ObjectDescription objDesc = procDesc.getSymbols().find(procDesc);
        assertEquals(objDesc, procDesc);
    }

    /**
     * Adding a ProcedureDescription to the SymbolTable should not be found by searching for a
     * different parameter list.
     */
    @Test
    void dontFindProcedureDescriptionWithDifferentParameterList() {
        // definition of the actual procedure's parameter list
        final VariableDescription param = new VariableDescription("a", Type.INT, 5, true);
        final LinkedList<ObjectDescription> paramList = new LinkedList<>();
        paramList.add(param);

        final ProcedureDescription procDesc =
                new ActualProcedureDescription(Type.INT, "main", paramList, null);
        final ProcedureDescription prototype =
                new ActualProcedureDescription(Type.INT, "main", new LinkedList<>(), null);
        symbolTable.insert(procDesc);
        final ObjectDescription objDesc = symbolTable.find(prototype);
        assertNull(objDesc);
    }

    /**
     * Adding a ProcedureDescription to the SymbolTable should enable finding it even when the
     * parameter's names differ.
     */
    @Test
    void findProcedureDescriptionWithDifferentParameterNames() {
        // definition of the actual procedure's parameter list
        final VariableDescription param = new VariableDescription("a", Type.INT, 5, true);
        final LinkedList<ObjectDescription> paramList = new LinkedList<>();
        paramList.add(param);

        // definition of the prototype param list; note the different name
        final VariableDescription protoParam = new VariableDescription("b", Type.INT, 5, true);
        final LinkedList<ObjectDescription> protoParamList = new LinkedList<>();
        protoParamList.add(protoParam);

        final ProcedureDescription procDesc =
                new ActualProcedureDescription(Type.INT, "main", paramList, null);
        final ProcedureDescription prototype =
                new ActualProcedureDescription(Type.INT, "main", protoParamList, null);

        symbolTable.insert(procDesc);
        final ObjectDescription objDesc = symbolTable.find(prototype);
        assertEquals(objDesc, procDesc);
    }

    /**
     * Adding a ProcedureDescription to the SymbolTable should not enable finding it when the order
     * of parameters is inverted in the description and prototype.
     */
    @Test
    void dontFindProcedureDescriptionWithSwitchedParameterTypes() {
        // definition of the actual procedure's parameter list
        final VariableDescription param1 = new VariableDescription("a", Type.INT, 5, true);
        final VariableDescription param2 = new VariableDescription("b", Type.BOOLEAN, true, true);
        final LinkedList<ObjectDescription> paramList = new LinkedList<>();
        paramList.add(param1);
        paramList.add(param2);

        // definition of the prototype param list; note the inverted order
        final LinkedList<ObjectDescription> protoParamList = new LinkedList<>();
        protoParamList.add(param2);
        protoParamList.add(param1);

        final ProcedureDescription procDesc =
                new ActualProcedureDescription(Type.INT, "main", paramList, null);
        final ProcedureDescription prototype =
                new ActualProcedureDescription(Type.INT, "main", protoParamList, null);

        symbolTable.insert(procDesc);
        final ObjectDescription objDesc = symbolTable.find(prototype);
        assertNull(objDesc);
    }

    /**
     * Adding a ProcedureDescription to the SymbolTable should not be found by searching for a
     * different name.
     */
    @Test
    void dontFindProcedureDescriptionWithDifferentName() {
        final ProcedureDescription procDesc =
                new ActualProcedureDescription(Type.INT, "main", new LinkedList<>(), null);
        final ProcedureDescription prototype =
                new ActualProcedureDescription(Type.INT, "niam", new LinkedList<>(), null);
        symbolTable.insert(procDesc);
        final ObjectDescription objDesc = symbolTable.find(prototype);
        assertNull(objDesc);
    }

    /**
     * Adding a ProcedureDescription to the SymbolTable should enable finding it, even when the
     * prototype has an other return type.
     */
    @Test
    void findProcedureDescriptionWithDifferentReturnType() {
        final ProcedureDescription procDesc =
                new ActualProcedureDescription(Type.INT, "main", new LinkedList<>(), null);
        final ProcedureDescription prototype =
                new ActualProcedureDescription(Type.VOID, "main", new LinkedList<>(), null);
        symbolTable.insert(procDesc);
        final ObjectDescription objDesc = symbolTable.find(prototype);
        assertEquals(objDesc, procDesc);
    }

    /**
     * Adding a ProcedureDescription to the SymbolTable should enable finding it, even when the
     * prototype has an other symbol table.
     */
    @Test
    void findInsertedProcedureDescriptionWithDifferentSymbolTable() {
        final VariableDescription varDes = new VariableDescription("a", Type.INT, 5, true);
        final ProcedureDescription procDesc =
                new ActualProcedureDescription(Type.INT, "main", new LinkedList<>(), null);
        procDesc.getSymbols().insert(varDes);

        // the search prototype does not need to match the symbol table
        final ProcedureDescription prototype =
                new ActualProcedureDescription(Type.INT, "main", new LinkedList<>(), null);
        symbolTable.insert(procDesc);
        final ObjectDescription objDesc = symbolTable.find(prototype);
        assertEquals(objDesc, procDesc);
    }

    /**
     * Adding a VariableDescription to the SymbolTable and searching for a procedure with the same
     * name and return type should not yield any results.
     */
    @Test
    void dontFindInsertedVariableDescriptionWhenSearchingForProcedure() {
        final String name = "a";
        final Type type = Type.INT;
        final VariableDescription varDesc = new VariableDescription(name, type, 5, true);
        final ProcedureDescription prototype =
                new ActualProcedureDescription(type, name, new LinkedList<>(), null);
        symbolTable.insert(varDesc);
        final ObjectDescription objDesc = symbolTable.find(prototype);
        assertNull(objDesc);
    }
}