package com.merkrafter;

import com.merkrafter.config.CompilerStage;
import com.merkrafter.config.Config;
import com.merkrafter.config.ErrorCode;
import com.merkrafter.lexing.Scanner;
import com.merkrafter.lexing.TokenType;
import com.merkrafter.parsing.Parser;
import com.merkrafter.representation.ast.AbstractSyntaxTree;
import com.merkrafter.representation.ast.ClassNode;
import com.merkrafter.representation.ssa.SSATransformableClass;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class Merkompiler {

    // META-INFORMATION
    //==============================================================
    @NotNull
    public static final String VERSION = "v0.5.0";

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
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(ErrorCode.IO_ERROR.id);
        }
    }

    /**
     * Contains the main application logic.
     * Passes errors etc. to the calling method which is expected to be main.
     *
     * @param config configuration data for this program call
     * @throws FileNotFoundException if the input or output file could not be found
     */
    static void run(@NotNull final Config config) throws IOException {
        if (config.isVerbose()) {
            System.out.println(config);
        }

        final File inputFile = new File(config.getInputFile());
        final Input input = new Input(inputFile.getAbsolutePath());
        final Scanner scanner = new Scanner(input);
        PrintStream out = System.out; // write to stdout by default
        try {
            if (config.isVerbose()) {
                scanner.setFilename(inputFile.getAbsolutePath());
            } else {
                scanner.setFilename(inputFile.getName());
            }


            // write to output file if given
            if (config.getOutputFile() != null) {
                out = new PrintStream(config.getOutputFile());
            }

            if (config.getStage() == CompilerStage.SCANNING) {
                // only print the tokens if the processing should stop after scanning
                do {
                    scanner.processToken();
                    out.println(scanner.getSym());
                } while (scanner.getSym().getType() != TokenType.EOF);
                return;
            }

            final Parser parser = new Parser(scanner);
            final AbstractSyntaxTree abstractSyntaxTree = parser.parse();
            int numParsingErrors = 0;
            for (final String errMsg : abstractSyntaxTree.getAllErrors()) {
                numParsingErrors++;
                System.err.println(errMsg);
            }
            if (numParsingErrors > 0) {
                return;
            }
            if (config.getStage() == CompilerStage.PARSING) {
                if (config.isGraphical() && abstractSyntaxTree instanceof ClassNode) {
                    final PrintWriter dotFileWriter =
                            new PrintWriter(config.getInputFile() + ".dot");
                    dotFileWriter.print(((ClassNode) abstractSyntaxTree).getDotRepresentation());
                    dotFileWriter.close();
                    return;
                }
            }

            if (!(abstractSyntaxTree instanceof SSATransformableClass)) {
                System.err.println("Was not able to convert to SSA form");
                return;
            }
            ((SSATransformableClass) abstractSyntaxTree).transformToSSA();

            if (config.isGraphical()) {
                final PrintWriter dotFileWriter = new PrintWriter(config.getInputFile() + ".dot");
                dotFileWriter.print(((ClassNode) abstractSyntaxTree).getDotRepresentation());
                dotFileWriter.close();
            } // else print the instructions
        } finally {
            input.close();
            if (out != System.out) {
                out.close();
            }
        }
    }
}
