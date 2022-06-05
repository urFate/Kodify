package me.urfate;

import org.apache.commons.cli.*;

public class OptionsParser {
    private final Options options;
    private final CommandLineParser parser;
    private final String[] args;

    public OptionsParser(String[] args){
        options = new Options();
        parser = new DefaultParser();
        this.args = args;
    }

    public void registerOption(String opt, String longOpt, boolean hasArg, String description){
        options.addOption(opt, longOpt, hasArg, description);
    }

    public String parseOption(String opt){
        try {
            CommandLine cmd = parser.parse(options, args);
            return cmd.getOptionValue(opt);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void printHelp(){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Kodify", options);
    }
}
