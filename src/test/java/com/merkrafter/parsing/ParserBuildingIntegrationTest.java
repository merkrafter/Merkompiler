package com.merkrafter.parsing;

import com.merkrafter.lexing.Scanner;
import com.merkrafter.lexing.StringIteratorTestUtility;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains some small(er) test cases that aim for edge cases and concrete scenarios.
 * They are tested together with the scanner which enables the source code to be much easier
 * readable.
 */
class ParserBuildingIntegrationTest {

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
}