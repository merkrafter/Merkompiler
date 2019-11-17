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
            System.exit(ErrorCodes.NOT_ENOUGH_ARGUMENTS.id);
        }

        final String filename = args[0];

        try {
            final Input input = new Input(filename);
            final Scanner s = new Scanner(input);
            do {
                s.processToken();
                System.out.println(s.getSym());
            } while (s.getSym().getType() != TokenType.EOF);

        } catch (FileNotFoundException e) {
            System.err.println(filename + " not found");
            System.exit(ErrorCodes.FILE_NOT_FOUND.id);
        }
    }

    /**
     * This enum defines all error codes that this program can exit with.
     */
    public enum ErrorCodes {
        NOT_ENOUGH_ARGUMENTS(1), FILE_NOT_FOUND(2);

        public final int id;

        ErrorCodes(final int id) {
            this.id = id;
        }
    }
}
