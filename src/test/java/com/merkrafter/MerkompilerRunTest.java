package com.merkrafter;

import com.merkrafter.config.Config;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @TempDir
    static Path tempDir; // access must NOT be private; otherwise JUnit could not create it

    // access can not be private; otherwise javadoc could not find the values
    public static final String INPUT_FILE_SUFFIX = ".java";
    public static final String OUTPUT_FILE_SUFFIX = ".output";
    public static final String EXPECTED_FILE_SUFFIX = ".expected";

    /**
     * This test case runs the lexer on the file(s) given by ValueSource.
     * It assumes that there are <code>baseFileName</code>{@value INPUT_FILE_SUFFIX} and
     * <code>baseFileName</code>{@value EXPECTED_FILE_SUFFIX} present under
     * <code>src/test/resources</code> in the project.
     * The output file for this experiment will be named <code>baseFileName</code>{@value OUTPUT_FILE_SUFFIX}
     * and is created as a temporary file by JUnit.
     *
     * @param baseFileName if used to find the source file name and expected file name and create the output file
     * @throws ArgumentParserException if the arguments in the test case are misconfigured (should not happen)
     * @throws IOException if there is a read/write error in one of the files
     */
    @ParameterizedTest
    @ValueSource(strings = "EmptyClass")
    void runWithOutputCreatesFile(final String baseFileName)
    throws ArgumentParserException, IOException {
        // java source file to read
        final File inputFile = getFileFromResource(baseFileName + INPUT_FILE_SUFFIX);
        // file that should identical content as outputFile after run method
        final File expectedFile = getFileFromResource(baseFileName + EXPECTED_FILE_SUFFIX);
        // file where the program output is written to
        final File outputFile = tempDir.resolve(baseFileName + OUTPUT_FILE_SUFFIX).toFile();

        final Config config = Config.fromArgs(String.format("%s --output %s",
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
     * @param baseFileName if used to find the source file name and expected file name
     * @throws ArgumentParserException if the arguments in the test case are misconfigured (should not happen)
     * @throws IOException if there is a read/write error in one of the files
     */
    @ParameterizedTest
    @ValueSource(strings = "EmptyClass")
    void runWithoutOutput(final String baseFileName) throws ArgumentParserException, IOException {
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
            final Config config = Config.fromArgs(inputFile.getAbsolutePath());
            Merkompiler.run(config);

            assertEquals(toString(expectedFile), output.toString().trim());
        } finally {
            System.setOut(originalOut); // reset System.out even in case of errors
        }
    }

    /**
     * Reads the given file using this class's class loader, checks for its existence and finally
     * returns it.
     *
     * @param fileName a file under <code>src/test/resources</code>
     * @return a file under the resource directory as specified by <code>fileName</code>
     */
    private File getFileFromResource(final String fileName) {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource(fileName).getFile());

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
    private static void assertFilesEqual(final File expectedFile, final File actualFile)
    throws IOException {
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
    private static String toString(final File file) throws IOException {
        return String.join("\n", Files.readAllLines(file.toPath()));
    }
}