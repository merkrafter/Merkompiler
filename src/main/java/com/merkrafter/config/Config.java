package com.merkrafter.config;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * This class holds configuration data for this program.
 * It also contains the description of this program's command line options etc.
 *
 * @author merkrafter
 */
public class Config {
    private final String inputFile;
    private final String outputFile;

    private final boolean verbose;

    private Config(final String inputFile, final String outputFile, boolean verbose) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.verbose = verbose;
    }

    public String getInputFile() {
        return inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public static Config fromArgs(final String args) throws ArgumentParserException {
        return fromArgs(fromString(args));
    }

    public static Config fromArgs(final String[] args) throws ArgumentParserException {
        // define the parser
        final ArgumentParser parser =
                ArgumentParsers.newFor("Merkompiler").build().defaultHelp(true)
                               .description("Compiles JavaSST files");

        parser.addArgument("INPUT").required(true).type(String.class)
              .help("JavaSST source code file");
        parser.addArgument("-v", "--verbose").action(Arguments.storeTrue())
              .help("print more information (absolute paths instead of simple file names in error messages, for instance");
        parser.addArgument("-o", "--output").type(String.class).metavar("OUTPUT")
              .help("output target; default is stdout");


        // parse the arguments
        Namespace namespace;
        namespace = parser.parseArgs(args);

        // build Config instance
        String inputFileName = null;
        String outputFileName = null;
        boolean verbose = false;

        if (namespace != null) {
            inputFileName = namespace.getString("INPUT");
            outputFileName = namespace.getString("output");
            verbose = namespace.getBoolean("verbose");
        }
        return new Config(inputFileName, outputFileName, verbose);
    }

    /**
     * Splits the String of arguments at whitespace into multiple argument tokens.
     *
     * @param argsAsString string of arguments as written on the command line
     * @return an array of arguments
     */
    public static String[] fromString(final String argsAsString) {
        return argsAsString.split("\\s+");
    }

    /**
     * @return a String representation of this Config class
     */
    @Override
    public String toString() {
        return String
                .format("Config(INPUT=%s, OUTPUT=%s, verbose=%b)", inputFile, outputFile, verbose);
    }
}
