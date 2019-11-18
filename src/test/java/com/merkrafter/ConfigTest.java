package com.merkrafter;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void parseOnlyInputFile() throws ArgumentParserException {
        final String[] args = fromString("Test.java");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = null;
        final boolean expectedVerbosity = false;

        assertEquals(expectedInputFilename, actualConfig.getInputFile());
        assertEquals(expectedOutputFilename, actualConfig.getOutputFile());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileWithVerbosityShortFirst() throws ArgumentParserException {
        final String[] args = fromString("-v Test.java");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = null;
        final boolean expectedVerbosity = true;

        assertEquals(expectedInputFilename, actualConfig.getInputFile());
        assertEquals(expectedOutputFilename, actualConfig.getOutputFile());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileWithVerbosityShortAfter() throws ArgumentParserException {
        final String[] args = fromString("Test.java -v");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = null;
        final boolean expectedVerbosity = true;

        assertEquals(expectedInputFilename, actualConfig.getInputFile());
        assertEquals(expectedOutputFilename, actualConfig.getOutputFile());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileWithVerbosityLongFirst() throws ArgumentParserException {
        final String[] args = fromString("--verbose Test.java");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = null;
        final boolean expectedVerbosity = true;

        assertEquals(expectedInputFilename, actualConfig.getInputFile());
        assertEquals(expectedOutputFilename, actualConfig.getOutputFile());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileWithVerbosityLongAfter() throws ArgumentParserException {
        final String[] args = fromString("Test.java --verbose");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = null;
        final boolean expectedVerbosity = true;

        assertEquals(expectedInputFilename, actualConfig.getInputFile());
        assertEquals(expectedOutputFilename, actualConfig.getOutputFile());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileAndShortOutputFileFirst() throws ArgumentParserException {
        final String[] args = fromString("-o OtherTest.class Test.java");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = "OtherTest.class";
        final boolean expectedVerbosity = false;

        assertEquals(expectedInputFilename, actualConfig.getInputFile());
        assertEquals(expectedOutputFilename, actualConfig.getOutputFile());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileAndShortOutputFileAfter() throws ArgumentParserException {
        final String[] args = fromString("Test.java -o OtherTest.class");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = "OtherTest.class";
        final boolean expectedVerbosity = false;

        assertEquals(expectedInputFilename, actualConfig.getInputFile());
        assertEquals(expectedOutputFilename, actualConfig.getOutputFile());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileAndLongOutputFileFirst() throws ArgumentParserException {
        final String[] args = fromString("--output OtherTest.class Test.java");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = "OtherTest.class";
        final boolean expectedVerbosity = false;

        assertEquals(expectedInputFilename, actualConfig.getInputFile());
        assertEquals(expectedOutputFilename, actualConfig.getOutputFile());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileAndLongOutputFileAfter() throws ArgumentParserException {
        final String[] args = fromString("Test.java --output OtherTest.class");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = "OtherTest.class";
        final boolean expectedVerbosity = false;

        assertEquals(expectedInputFilename, actualConfig.getInputFile());
        assertEquals(expectedOutputFilename, actualConfig.getOutputFile());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @Test
    void parseInputFileAndOutputFileAndVerbosity() throws ArgumentParserException {
        final String[] args = fromString("--verbose Test.java --output OtherTest.class");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = "OtherTest.class";
        final boolean expectedVerbosity = true;

        assertEquals(expectedInputFilename, actualConfig.getInputFile());
        assertEquals(expectedOutputFilename, actualConfig.getOutputFile());
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