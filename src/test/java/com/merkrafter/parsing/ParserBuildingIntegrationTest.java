package com.merkrafter.parsing;

import com.merkrafter.lexing.Position;
import com.merkrafter.lexing.Scanner;
import com.merkrafter.lexing.StringIteratorTestUtility;
import com.merkrafter.representation.ast.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.merkrafter.representation.Type.INT;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class contains some small(er) test cases that aim for edge cases and concrete scenarios.
 * They are tested together with the scanner which enables the source code to be much easier
 * readable.
 */
class ParserBuildingIntegrationTest {

    private final Position p = new Position("", 0, 0); // just a dummy position
    /**
     * This field enables iterating over Strings.
     */
    private StringIteratorTestUtility stringIterator;

    private Scanner scanner;

    /**
     * Subject under test
     */
    private Parser parser;

    @BeforeEach
    void setUp() {
        stringIterator = new StringIteratorTestUtility();
        scanner = new Scanner(stringIterator);
        parser = new Parser(scanner);
    }

    /**
     * The generated AST should reflect the operator precedence of multiplication over addition.
     */
    @Test
    void multiplicationBeforeAddition() {
        final String program = "5+3*8";
        stringIterator.setString(program);
        scanner = new Scanner(stringIterator);
        scanner.setFilename(""); // avoid setting it to null
        parser = new Parser(scanner);
        final Expression multTree = new BinaryOperationNode(new ConstantNode<>(INT, 3L, p),
                                                            BinaryOperationNodeType.TIMES,
                                                            new ConstantNode<>(INT, 8L, p));
        final AbstractSyntaxTree expectedAST =
                new BinaryOperationNode(new ConstantNode<>(INT, 5L, p),
                                        BinaryOperationNodeType.PLUS,
                                        multTree);

        final AbstractSyntaxTree actualAST = parser.parseExpression();

        assertEquals(expectedAST, actualAST);
    }
}