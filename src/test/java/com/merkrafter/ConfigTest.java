package com.merkrafter;

import com.merkrafter.config.CompilerStage;
import com.merkrafter.config.Config;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.merkrafter.config.Config.fromString;
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

    @ParameterizedTest
    // {short, long} x {before input file, after input file}
    @ValueSource(strings = {
            "-v Test.java", "--verbose Test.java", "Test.java -v", "Test.java --verbose"})
    void parseInputFileWithVerbosity(final String string) throws ArgumentParserException {
        final String[] args = fromString(string);
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final boolean expectedVerbosity = true;

        assertEquals(expectedInputFilename, actualConfig.getInputFile());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    @ParameterizedTest
    // {short, long} x {before input file, after input file}
    @ValueSource(strings = {
            "-o OtherTest.class Test.java",
            "--output OtherTest.class Test.java",
            "Test.java -o OtherTest.class",
            "Test.java --output OtherTest.class"})
    void parseInputFileAndOutputFile(final String string) throws ArgumentParserException {
        final String[] args = fromString(string);
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = "OtherTest.class";

        assertEquals(expectedInputFilename, actualConfig.getInputFile());
        assertEquals(expectedOutputFilename, actualConfig.getOutputFile());
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

    @ParameterizedTest
    @ValueSource(strings = {"scanning", "SCANNING", "sCanning", "sCaNnInG"})
    void skipAfterScanning(final String spelling) throws ArgumentParserException {
        final String[] args = fromString(String.format("--skip-after %s Test.java", spelling));
        final Config actualConfig = Config.fromArgs(args);

        final CompilerStage expectedStage = CompilerStage.SCANNING;

        assertEquals(expectedStage, actualConfig.getStage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"parsing", "PARSING", "pArsing", "pArSinG"})
    void skipAfterParsing(final String spelling) throws ArgumentParserException {
        final String[] args = fromString(String.format("--skip-after %s Test.java", spelling));
        final Config actualConfig = Config.fromArgs(args);

        final CompilerStage expectedStage = CompilerStage.PARSING;

        assertEquals(expectedStage, actualConfig.getStage());
    }

    @Test
    void defaultStage() throws ArgumentParserException {
        final String[] args = fromString("Test.java");
        final Config actualConfig = Config.fromArgs(args);

        final CompilerStage expectedStage = CompilerStage.PARSING;

        assertEquals(expectedStage, actualConfig.getStage());
    }
}