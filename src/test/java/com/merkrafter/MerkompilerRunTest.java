package com.merkrafter;

import com.merkrafter.config.CompilerStage;
import com.merkrafter.config.Config;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * This class contains integration test cases for the Merkompiler program.
 * In particular, it tests the Merkompiler class's run() method.
 *
 * @author merkrafter
 */
class MerkompilerRunTest {

    /**
     * Temporary directory for the compiler to write to
     */
    @SuppressWarnings("unused")
    @TempDir
    static Path tempDir; // access must NOT be private; otherwise JUnit could not create it

    // access can not be private; otherwise javadoc could not find the values
    @NotNull
    public static final String INPUT_FILE_SUFFIX = ".java";
    @NotNull
    public static final String OUTPUT_FILE_SUFFIX = ".output";
    @NotNull
    public static final String EXPECTED_FILE_SUFFIX = ".expected";

    /**
     * This test case runs the lexer on the file(s) given by ValueSource.
     * It assumes that there are <code>baseFileName</code>{@value INPUT_FILE_SUFFIX} and
     * <code>baseFileName</code>{@value EXPECTED_FILE_SUFFIX} present under
     * <code>src/test/resources</code> in the project.
     * The output file for this experiment will be named <code>baseFileName</code>{@value OUTPUT_FILE_SUFFIX}
     * and is created as a temporary file by JUnit.
     *
     * @param baseFileName is used to find the source file name and expected file name and create the output file
     * @throws ArgumentParserException if the arguments in the test case are misconfigured (should not happen)
     * @throws IOException if there is a read/write error in one of the files
     */
    @ParameterizedTest
    @ValueSource(strings = {"EmptyClass", "SmokeClass"})
    void scanWithOutputCreatesFile(@NotNull final String baseFileName)
    throws ArgumentParserException, IOException {
        // java source file to read
        final File inputFile = getFileFromResource(baseFileName + INPUT_FILE_SUFFIX);
        // file that should identical content as outputFile after run method
        final File expectedFile = getFileFromResource(baseFileName + EXPECTED_FILE_SUFFIX);
        // file where the program output is written to
        final File outputFile = tempDir.resolve(baseFileName + OUTPUT_FILE_SUFFIX).toFile();

        final Config config = Config.fromArgs(String.format("--skip-after %s %s --output %s",
                                                            CompilerStage.SCANNING.toString(),
                                                            inputFile.getAbsolutePath(),
                                                            outputFile.getAbsolutePath()));
        Merkompiler.run(config);

        assertFilesEqual(expectedFile, outputFile);
    }

    /**
     * This test case runs the lexer on the file(s) given by ValueSource.
     * It assumes that there are <code>baseFileName</code>{@value INPUT_FILE_SUFFIX} and
     * <code>baseFileName</code>{@value EXPECTED_FILE_SUFFIX} present under
     * <code>src/test/resources</code> in the project.
     * The output for this experiment is not specified, hence it tests writing to stdout.
     * <p>
     * This method resets System.out in order to test the output written to it.
     * If this method throws an exception, System.out might still be unavailable.
     *
     * @param baseFileName is used to find the source file name and expected file name
     * @throws ArgumentParserException if the arguments in the test case are misconfigured (should not happen)
     * @throws IOException if there is a read/write error in one of the files
     */
    @ParameterizedTest
    @ValueSource(strings = {"EmptyClass", "SmokeClass"})
    void scanWithoutOutput(@NotNull final String baseFileName)
    throws ArgumentParserException, IOException {
        final PrintStream originalOut = System.out;
        try { // will reset System.out in case of errors
            final ByteArrayOutputStream output = new ByteArrayOutputStream();

            // java source file to read
            final File inputFile = getFileFromResource(baseFileName + INPUT_FILE_SUFFIX);
            // file that should identical content as output after run method
            final File expectedFile = getFileFromResource(baseFileName + EXPECTED_FILE_SUFFIX);
            // set stdout to testable output stream
            System.setOut(new PrintStream(output));

            // run main program without specifying output
            final Config config = Config.fromArgs(String.format("--skip-after %s %s",
                                                                CompilerStage.SCANNING.toString(),
                                                                inputFile.getAbsolutePath()));
            Merkompiler.run(config);

            assertEquals(toString(expectedFile), output.toString().trim());
        } finally {
            System.setOut(originalOut); // reset System.out even in case of errors
        }
    }

    /**
     * This test case runs the lexer and parser on the file(s) given by ValueSource.
     * If there is no syntax error, the program should not write anything to stderr.
     * This test assumes that there is <code>baseFileName</code>{@value INPUT_FILE_SUFFIX} present under
     * <code>src/test/resources</code> in the project.
     * <p>
     * This method resets System.err in order to test the output written to it.
     * If this method throws an exception, System.err might still be unavailable.
     *
     * @param baseFileName is used to find the source file name
     * @throws ArgumentParserException if the arguments in the test case are misconfigured (should not happen)
     * @throws IOException if there is a read/write error in the input file
     */
    @ParameterizedTest
    @ValueSource(strings = "SmokeClass")
    void parseCorrectClass(@NotNull final String baseFileName)
    throws ArgumentParserException, IOException {
        final PrintStream originalErr = System.err;
        try { // will reset System.err in case of crashes
            final ByteArrayOutputStream output = new ByteArrayOutputStream();

            // java source file to read
            final File inputFile = getFileFromResource(baseFileName + INPUT_FILE_SUFFIX);
            // set stderr to testable output stream
            System.setErr(new PrintStream(output));

            // run main program without specifying output
            final Config config = Config.fromArgs(String.format("--skip-after %s %s",
                                                                CompilerStage.PARSING.toString(),
                                                                inputFile.getAbsolutePath()));
            Merkompiler.run(config);

            assertTrue(output.toString().trim().isEmpty());
        } finally {
            System.setErr(originalErr); // reset System.err even in case of crashes
        }
    }

    /**
     * This test case runs the lexer and parser on the faulty file(s) given by ValueSource.
     * Since there are syntax errors, the program should write some error message(s) to stderr.
     * This test assumes that there is <code>baseFileName</code>{@value INPUT_FILE_SUFFIX} present
     * under <code>src/test/resources</code> in the project.
     * <p>
     * This method resets System.err in order to test the output written to it.
     * If this method throws an exception, System.err might still be unavailable.
     *
     * @param baseFileName is used to find the source file name
     * @throws ArgumentParserException if the arguments in the test case are misconfigured (should not happen)
     * @throws IOException if there is a read/write error in the input file
     */
    @ParameterizedTest
    @ValueSource(strings = "EmptyClass")
    void parseFaultyFile(@NotNull final String baseFileName)
    throws ArgumentParserException, IOException {
        final PrintStream originalErr = System.err;
        try { // will reset System.err in case of crashes
            final ByteArrayOutputStream output = new ByteArrayOutputStream();

            // java source file to read
            final File inputFile = getFileFromResource(baseFileName + INPUT_FILE_SUFFIX);
            // set stderr to testable output stream
            System.setErr(new PrintStream(output));

            // run main program without specifying output
            final Config config = Config.fromArgs(String.format("--skip-after %s %s",
                                                                CompilerStage.PARSING.toString(),
                                                                inputFile.getAbsolutePath()));
            Merkompiler.run(config);

            // the compiler should output some message
            assertFalse(output.toString().trim().isEmpty());
        } finally {
            System.setErr(originalErr); // reset System.err even in case of crashes
        }
    }

    /**
     * Reads the given file using this class's class loader, checks for its existence and finally
     * returns it.
     *
     * @param fileName a file under <code>src/test/resources</code>
     * @return a file under the resource directory as specified by <code>fileName</code>
     */
    @NotNull
    private File getFileFromResource(@NotNull final String fileName) {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource(fileName);
        assert resource != null;
        final File file = new File(resource.getFile());

        assumeTrue(file.exists(),
                   "Misconfigured test environment: Missing file " + file.getAbsolutePath());

        return file;
    }

    /**
     * Checks whether two files are equal by comparing their contents line by line.
     *
     * @param expectedFile the file that defines the base line
     * @param actualFile should be equal to <code>expectedFile</code>
     * @throws IOException if there is a read/write error in one of the files
     */
    private static void assertFilesEqual(@NotNull final File expectedFile,
                                         @NotNull final File actualFile) throws IOException {
        assertEquals(Files.readAllLines(expectedFile.toPath()),
                     Files.readAllLines(actualFile.toPath()));
    }

    /**
     * Reads the file contents and joins them to a single string.
     *
     * @param file the file to read
     * @return a string containing the lines joined with a newline character
     *
     * @throws IOException if a read/write error occurs
     */
    @NotNull
    private static String toString(@NotNull final File file) throws IOException {
        return String.join("\n", Files.readAllLines(file.toPath()));
    }
}