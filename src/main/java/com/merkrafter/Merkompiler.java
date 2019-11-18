package com.merkrafter;

import com.merkrafter.lexing.Scanner;
import com.merkrafter.lexing.TokenType;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;

public class Merkompiler {

    /**
     * The main function of this compiler reads in the filename and handles other possible command line
     * options. It then runs the compiler on the input file.
     */
    public static void main(String[] args) {
        // to change the arguments in IntelliJ, press Alt+Shift+F10
        Config config = null;
        try {
            config = Config.fromArgs(args);
        } catch (ArgumentParserException e) {
            e.getParser().handleError(e);
            System.exit(ErrorCode.ARGUMENTS_UNPARSABLE.id);
        }
        if (config.isVerbose()) {
            System.out.println(config);
        }

        /*
         * Main program
         */
        try {
            run(config);
        } catch (FileNotFoundException e) {
            System.err.println(config.getInput_file() + " not found");
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
        final Input input = new Input(config.getInput_file());
        final Scanner scanner = new Scanner(input);

        PrintStream out = System.out;

        // write to output file if given
        if (config.getOutput_file() != null) {
            out = new PrintStream(config.getOutput_file());
        }

        do {
            scanner.processToken();
            out.println(scanner.getSym());
        } while (scanner.getSym().getType() != TokenType.EOF);

    }
}