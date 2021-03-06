package com.merkrafter;

import com.merkrafter.config.CompilerStage;
import com.merkrafter.config.Config;
import com.merkrafter.config.ErrorCode;
import com.merkrafter.lexing.Scanner;
import com.merkrafter.lexing.TokenType;
import com.merkrafter.parsing.Parser;
import com.merkrafter.representation.ast.AbstractSyntaxTree;
import com.merkrafter.representation.ast.ClassNode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.jetbrains.annotations.NotNull;

public class Merkompiler {

  // META-INFORMATION
  // ==============================================================
  @NotNull public static final String VERSION = "v0.4.0";

  /**
   * The main function of this compiler reads in the filename and handles other possible command
   * line options. It then runs the compiler on the input file.
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
   * Contains the main application logic. Passes errors etc. to the calling method which is expected
   * to be main.
   *
   * @param config configuration data for this program call
   * @throws FileNotFoundException if the input or output file could not be found
   */
  static void run(@NotNull final Config config) throws FileNotFoundException {
    if (config.isVerbose()) {
      System.out.println(config);
    }

    final File inputFile = new File(config.getInputFile());
    final Input input = new Input(inputFile.getAbsolutePath());
    final Scanner scanner = new Scanner(input);
    if (config.isVerbose()) {
      scanner.setFilename(inputFile.getAbsolutePath());
    } else {
      scanner.setFilename(inputFile.getName());
    }

    PrintStream out = System.out; // write to stdout by default

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
    } else if (config.getStage() == CompilerStage.PARSING) {
      final Parser parser = new Parser(scanner);
      final AbstractSyntaxTree abstractSyntaxTree = parser.parse();
      int numErrors = 0;
      for (final String errMsg : abstractSyntaxTree.getAllErrors()) {
        numErrors++;
        System.err.println(errMsg);
      }
      if (config.isGraphical() && numErrors == 0 && abstractSyntaxTree instanceof ClassNode) {
        final PrintWriter dotFileWriter = new PrintWriter(config.getInputFile() + ".dot");
        dotFileWriter.print(((ClassNode) abstractSyntaxTree).getDotRepresentation());
        dotFileWriter.close();
      }
    }
    if (out != System.out) {
      out.close();
    }
  }
}
