package com.merkrafter;

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

    public static Config fromArgs(String[] args) {
        String input_file = "";
        String output_file = "";
        boolean verbose = false;
        boolean help = false;

        if (args.length < 1) {
            final Config config = new Config(input_file, output_file, verbose, help);
            config.error = new Error(ErrorCode.NOT_ENOUGH_ARGUMENTS, "missing input file");
            return config;
        }

        boolean output_comes_next = false;

        for (final String argument : args) {
            if (output_comes_next) {
                output_file = argument;
                output_comes_next = false;
                continue;
            }
            switch (argument) {
                case "--verbose":
                case "-v":
                    verbose = true;
                    break;
                case "--help":
                case "-h":
                    help = true;
                    break;
                case "--output":
                case "-o":
                    output_comes_next = true;
                    break;
                default:
                    input_file = argument;
            }
        }

        return new Config(input_file, output_file, verbose, help);
    }

    /**
     * @return a String representation of this Config class
     */
    @Override
    public String toString() {
        return String.format("Config(INPUT=%s, OUTPUT=%s, verbose=%b, help=%b)", input_file, output_file, verbose, help);
    }
}
