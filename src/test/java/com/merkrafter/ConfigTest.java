package com.merkrafter;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void parseOnlyInputFile() {
        final String[] args = fromString("Test.java");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = null;
        final boolean expectedVerbosity = false;

        assertEquals(expectedInputFilename, actualConfig.getInput_file());
        assertEquals(expectedOutputFilename, actualConfig.getOutput_file());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileWithVerbosityShortFirst() {
        final String[] args = fromString("-v Test.java");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = null;
        final boolean expectedVerbosity = true;

        assertEquals(expectedInputFilename, actualConfig.getInput_file());
        assertEquals(expectedOutputFilename, actualConfig.getOutput_file());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileWithVerbosityShortAfter() {
        final String[] args = fromString("Test.java -v");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = null;
        final boolean expectedVerbosity = true;

        assertEquals(expectedInputFilename, actualConfig.getInput_file());
        assertEquals(expectedOutputFilename, actualConfig.getOutput_file());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileWithVerbosityLongFirst() {
        final String[] args = fromString("--verbose Test.java");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = null;
        final boolean expectedVerbosity = true;

        assertEquals(expectedInputFilename, actualConfig.getInput_file());
        assertEquals(expectedOutputFilename, actualConfig.getOutput_file());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileWithVerbosityLongAfter() {
        final String[] args = fromString("Test.java --verbose");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = null;
        final boolean expectedVerbosity = true;

        assertEquals(expectedInputFilename, actualConfig.getInput_file());
        assertEquals(expectedOutputFilename, actualConfig.getOutput_file());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileAndShortOutputFileFirst() {
        final String[] args = fromString("-o OtherTest.class Test.java");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = "OtherTest.class";
        final boolean expectedVerbosity = false;

        assertEquals(expectedInputFilename, actualConfig.getInput_file());
        assertEquals(expectedOutputFilename, actualConfig.getOutput_file());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileAndShortOutputFileAfter() {
        final String[] args = fromString("Test.java -o OtherTest.class");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = "OtherTest.class";
        final boolean expectedVerbosity = false;

        assertEquals(expectedInputFilename, actualConfig.getInput_file());
        assertEquals(expectedOutputFilename, actualConfig.getOutput_file());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileAndLongOutputFileFirst() {
        final String[] args = fromString("--output OtherTest.class Test.java");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = "OtherTest.class";
        final boolean expectedVerbosity = false;

        assertEquals(expectedInputFilename, actualConfig.getInput_file());
        assertEquals(expectedOutputFilename, actualConfig.getOutput_file());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileAndLongOutputFileAfter() {
        final String[] args = fromString("Test.java --output OtherTest.class");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = "OtherTest.class";
        final boolean expectedVerbosity = false;

        assertEquals(expectedInputFilename, actualConfig.getInput_file());
        assertEquals(expectedOutputFilename, actualConfig.getOutput_file());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileAndOutputFileAndVerbosity() {
        final String[] args = fromString("--verbose Test.java --output OtherTest.class");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = "OtherTest.class";
        final boolean expectedVerbosity = true;

        assertEquals(expectedInputFilename, actualConfig.getInput_file());
        assertEquals(expectedOutputFilename, actualConfig.getOutput_file());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    /**
     * Splits the String of arguments at whitespace into multiple argument tokens.
     *
     * @param argsAsString string of arguments as written on the command line
     * @return an array of arguments
     */
    private String[] fromString(final String argsAsString) {
        return argsAsString.split("\\s+");
    }
}