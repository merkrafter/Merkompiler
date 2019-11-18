package com.merkrafter;

import com.merkrafter.lexing.Scanner;
import com.merkrafter.lexing.TokenType;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Merkompiler {

    /**
     * The main function of this compiler reads in the filename and handles other possible command line
     * options. It then runs the compiler on the input file.
     */
    public static void main(String[] args) {
        // to change the arguments in IntelliJ, press Alt+Shift+F10
        try {
            final Config config = Config.fromArgs(args);
            run(config);
        } catch (ArgumentParserException e) {
            e.getParser().handleError(e); // prints the help message
            System.exit(ErrorCode.ARGUMENTS_UNPARSABLE.id);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(ErrorCode.FILE_NOT_FOUND.id);
        }
    }

    /**
     * Contains the main application logic.
     * Passes errors etc. to the calling method which is expected to be main.
     *
     * @param config configuration data for this program call
     * @throws FileNotFoundException if the input or output file could not be found
     */
    private static void run(final Config config) throws FileNotFoundException {
        if (config.isVerbose()) {
            System.out.println(config);
        }

        final Input input = new Input(config.getInputFile());
        final Scanner scanner = new Scanner(input);
        scanner.setFilename(config.getInputFile());

        PrintStream out = System.out; // write to stdout by default

        // write to output file if given
        if (config.getOutputFile() != null) {
            out = new PrintStream(config.getOutputFile());
        }

        do {
            scanner.processToken();
            out.println(scanner.getSym());
        } while (scanner.getSym().getType() != TokenType.EOF);
    }
}