package com.merkrafter;

import com.merkrafter.lexing.Scanner;
import com.merkrafter.lexing.TokenType;

import java.io.FileNotFoundException;

public class Merkompiler {

    /**
     * The main function of this compiler reads in the filename and handles other possible command line
     * options. It then runs the compiler on the input file.
     */
    public static void main(String[] args) {
        // to change the arguments in IntelliJ, press Alt+Shift+F10
        if (args.length < 1) {
            System.err.println("Usage: java Merkompiler <filename>");
            System.exit(ErrorCode.NOT_ENOUGH_ARGUMENTS.id);
        }

        final Config config = Config.fromArgs(args);
        if (config.isVerbose()) {
            System.out.println(config);
        }
        try {
            final Input input = new Input(config.getInput_file());
            final Scanner scanner = new Scanner(input);
            do {
                scanner.processToken();
                System.out.println(scanner.getSym());
            } while (scanner.getSym().getType() != TokenType.EOF);

        } catch (FileNotFoundException e) {
            System.err.println(config.getInput_file() + " not found");
            System.exit(ErrorCode.FILE_NOT_FOUND.id);
        }
    }

}
