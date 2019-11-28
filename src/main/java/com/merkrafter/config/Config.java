package com.merkrafter.config;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
        final ArgumentParser parser = ArgumentParsers.newFor("Merkompiler")
                                                     .build()
                                                     .defaultHelp(true)
                                                     .description("Compiles JavaSST files");
        try {
            parser.version("${prog} " + getVersion());
        } catch (XmlPullParserException | IOException ignored) {
            parser.version("No version information available.");
        }
        parser.addArgument("INPUT")
              .required(true)
              .type(String.class)
              .help("JavaSST source code file");
        parser.addArgument("-v", "--verbose")
              .action(Arguments.storeTrue())
              .help("print more information (absolute paths instead of simple file names in error messages, for instance");
        parser.addArgument("-V", "--version")
              .action(Arguments.version())
              .help("print version information and exit");
        parser.addArgument("-o", "--output")
              .type(String.class)
              .metavar("OUTPUT")
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
        return String.format("Config(INPUT=%s, OUTPUT=%s, verbose=%b)",
                             inputFile,
                             outputFile,
                             verbose);
    }

    /**
     * Retrieve version information from the pom.xml file.
     *
     * @return a String containing the software version
     */
    private static String getVersion() throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));
        return model.getVersion();
    }
}
