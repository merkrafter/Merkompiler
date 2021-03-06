package com.merkrafter.config;

import com.merkrafter.Merkompiler;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class holds configuration data for this program. It also contains the description of this
 * program's command line options etc.
 *
 * @author merkrafter
 * @since v0.1.0
 */
public class Config {
  // ATTRIBUTES
  // ==============================================================
  @NotNull private final String inputFile;
  @Nullable private final String outputFile;
  private final boolean verbose;
  @Nullable private final CompilerStage stage;
  private final boolean graphical;

  // CONSTRUCTORS
  // ==============================================================
  private Config(
      @NotNull final String inputFile,
      @Nullable final String outputFile,
      boolean verbose,
      @Nullable final CompilerStage stage,
      final boolean graphical) {
    this.inputFile = inputFile;
    this.outputFile = outputFile;
    this.verbose = verbose;
    this.stage = stage;
    this.graphical = graphical;
  }

  // GETTER
  // ==============================================================
  @NotNull
  public String getInputFile() {
    return inputFile;
  }

  @Nullable
  public String getOutputFile() {
    return outputFile;
  }

  public boolean isVerbose() {
    return verbose;
  }

  @Nullable
  public CompilerStage getStage() {
    return stage;
  }

  public boolean isGraphical() {
    return graphical;
  }

  // METHODS
  // ==============================================================
  // public methods
  // --------------------------------------------------------------
  @NotNull
  public static Config fromArgs(@NotNull final String args) throws ArgumentParserException {
    // only called from test code
    return fromArgs(fromString(args));
  }

  @NotNull
  public static Config fromArgs(@NotNull final String[] args) throws ArgumentParserException {
    // define the parser
    final ArgumentParser parser =
        ArgumentParsers.newFor("Merkompiler")
            .build()
            .defaultHelp(true)
            .description("Compiles JavaSST files");
    parser.version("${prog} " + Merkompiler.VERSION);
    parser.addArgument("INPUT").required(true).type(String.class).help("JavaSST source code file");
    parser
        .addArgument("-v", "--verbose")
        .action(Arguments.storeTrue())
        .help(
            "print more information (absolute paths instead of simple file names in error"
                + " messages, for instance");
    parser
        .addArgument("-V", "--version")
        .action(Arguments.version())
        .help("print version information and exit");
    parser
        .addArgument("-o", "--output")
        .type(String.class)
        .metavar("OUTPUT")
        .help("output target; default is stdout");
    parser
        .addArgument("--skip-after")
        .type(Arguments.caseInsensitiveEnumType(CompilerStage.class))
        .dest("compilerStage")
        .setDefault(CompilerStage.latest())
        .help("only process the input file up to the given stage (including)");
    parser
        .addArgument("-g", "--graphical")
        .action(Arguments.storeTrue())
        .help("output a .dot file showing the abstract syntax tree of the specified source file");

    // parse the arguments
    Namespace namespace;
    namespace = parser.parseArgs(args);

    // build Config instance
    String inputFileName = null;
    String outputFileName = null;
    boolean verbose = false;
    CompilerStage stage = CompilerStage.latest();
    boolean graphical = false;

    if (namespace != null) {
      inputFileName = namespace.getString("INPUT");
      outputFileName = namespace.getString("output");
      verbose = namespace.getBoolean("verbose");
      stage = namespace.get("compilerStage");
      graphical = namespace.get("graphical");
    }

    assert inputFileName != null; // because it is required and thus handled by Argparse
    return new Config(inputFileName, outputFileName, verbose, stage, graphical);
  }

  /**
   * Splits the String of arguments at whitespace into multiple argument tokens.
   *
   * @param argsAsString string of arguments as written on the command line
   * @return an array of arguments
   */
  @NotNull
  public static String[] fromString(@NotNull final String argsAsString) {
    return argsAsString.split("\\s+");
  }

  /** @return a String representation of this Config class */
  @NotNull
  @Override
  public String toString() {
    return String.format(
        "Config(INPUT=%s, OUTPUT=%s, verbose=%b, stage=%s, graphical=%b)",
        inputFile, outputFile, verbose, stage, graphical);
  }
}
