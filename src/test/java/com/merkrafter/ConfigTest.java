package com.merkrafter;

import com.merkrafter.config.CompilerStage;
import com.merkrafter.config.Config;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.merkrafter.config.Config.fromString;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The test cases of this class verify that the conversion from command line arguments to program
 * configuration object works correctly. Therefore, the Config::fromArgs static method is tested
 * intensely.
 */
class ConfigTest {

    /**
     * The fromArgs method should be able to extract the input file name correctly.
     * It should not set the verbosity nor the output file name.
     *
     * @throws ArgumentParserException if the arguments can not be parsed; should not happen
     */
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

    /**
     * The fromArgs method should be able to detect the verbosity flag being set, independent of
     * whether the long or short argument was used or whether it was specified before or after
     * the input file.
     *
     * @throws ArgumentParserException if the arguments can not be parsed; should not happen
     */
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

    /**
     * The fromArgs method should be able to detect the output file being specified, independent of
     * whether the long or short argument was used, it was assigned using = or not or whether
     * it was specified before or after the input file.
     *
     * @throws ArgumentParserException if the arguments can not be parsed; should not happen
     */
    @ParameterizedTest
    // {short, long} x {before input file, after input file}
    @ValueSource(strings = {
            "-o=OtherTest.class Test.java",
            "--output=OtherTest.class Test.java",
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

    /**
     * The fromArgs method should be able to extract the output file and the verbosity flag if both
     * are given at the same time.
     *
     * @throws ArgumentParserException if the arguments can not be parsed; should not happen
     */
    @Test
    void parseInputFileAndOutputFileWithVerbosity() throws ArgumentParserException {
        final String[] args = fromString("-v -o OtherTest.class Test.java");
        final Config actualConfig = Config.fromArgs(args);

        final String expectedInputFilename = "Test.java";
        final String expectedOutputFilename = "OtherTest.class";
        final boolean expectedVerbosity = true;

        assertEquals(expectedInputFilename, actualConfig.getInputFile());
        assertEquals(expectedOutputFilename, actualConfig.getOutputFile());
        assertEquals(expectedVerbosity, actualConfig.isVerbose());
    }

    /**
     * The fromArgs method should be able to extract the compiler stage "scanning" while ignoring
     * the case.
     *
     * @throws ArgumentParserException if the arguments can not be parsed; should not happen
     */
    @ParameterizedTest
    @ValueSource(strings = {"scanning", "SCANNING", "sCanning", "sCaNnInG"})
    void skipAfterScanning(final String spelling) throws ArgumentParserException {
        final String[] args = fromString(String.format("--skip-after %s Test.java", spelling));
        final Config actualConfig = Config.fromArgs(args);

        final CompilerStage expectedStage = CompilerStage.SCANNING;

        assertEquals(expectedStage, actualConfig.getStage());
    }

    /**
     * The fromArgs method should be able to extract the compiler stage "parsing" while ignoring
     * the case.
     *
     * @throws ArgumentParserException if the arguments can not be parsed; should not happen
     */
    @ParameterizedTest
    @ValueSource(strings = {"parsing", "PARSING", "pArsing", "pArSinG"})
    void skipAfterParsing(final String spelling) throws ArgumentParserException {
        final String[] args = fromString(String.format("--skip-after %s Test.java", spelling));
        final Config actualConfig = Config.fromArgs(args);

        final CompilerStage expectedStage = CompilerStage.PARSING;

        assertEquals(expectedStage, actualConfig.getStage());
    }

    /**
     * The fromArgs method should set the latest compiler stage correctly when it is not specified
     * explicitly.
     *
     * @throws ArgumentParserException if the arguments can not be parsed; should not happen
     */
    @Test
    void defaultStage() throws ArgumentParserException {
        final String[] args = fromString("Test.java");
        final Config actualConfig = Config.fromArgs(args);

        final CompilerStage expectedStage = CompilerStage.PARSING;

        assertEquals(expectedStage, actualConfig.getStage());
    }
}