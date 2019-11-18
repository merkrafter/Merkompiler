package com.merkrafter;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * This class holds configuration data for this program.
 *
 * @author merkrafter
 */
public class Config {
    private final String input_file;
    private final String output_file;

    private final boolean verbose;
    private final boolean help;

    private Error error;

    private Config(final String input_file, final String output_file, boolean verbose, boolean help) {
        this.input_file = input_file;
        this.output_file = output_file;
        this.verbose = verbose;
        this.help = help;
    }

    public String getInput_file() {
        return input_file;
    }

    public String getOutput_file() {
        return output_file;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public boolean requestsHelp() {
        return help;
    }

    public boolean hasError() {
        return error != null;
    }

    public Error getError() {
        return error;
    }

    public static Config fromArgs(final String[] args) throws ArgumentParserException {
        // define the parser
        final ArgumentParser parser =
                ArgumentParsers.newFor("Merkompiler").build().defaultHelp(true)
                               .description("Compiles JavaSST files");

        parser.addArgument("INPUT").required(true).type(String.class)
              .help("JavaSST source code file");
        parser.addArgument("-v", "--verbose").action(Arguments.storeTrue())
              .help("print more information");
        parser.addArgument("-o", "--output").type(String.class).metavar("OUTPUT")
              .help("output target; default is stdout");


        // parse the arguments
        Namespace namespace = null;
        try {
            namespace = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            throw e;
        }

        // build Config instance
        String inputFileName = null;
        String outputFileName = null;
        boolean verbose = false;

        if (namespace != null) {
            inputFileName = namespace.getString("INPUT");
            outputFileName = namespace.getString("output");
            verbose = namespace.getBoolean("verbose");
        }
        return new Config(inputFileName, outputFileName, verbose, false);
    }

    /**
     * @return a String representation of this Config class
     */
    @Override
    public String toString() {
        return String
                .format("Config(INPUT=%s, OUTPUT=%s, verbose=%b, help=%b)", input_file, output_file, verbose, help);
    }
}
