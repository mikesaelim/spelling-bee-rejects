package io.github.mikesaelim;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class Main {
    private static final String DEFAULT_WORD_LIST_PATH = "data/wordlist.txt";

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        options.addOption("g", "generate", false, "generate a word list file");
        options.addOption("w", "wordlist", true, "specify a path to the word list file");

        try {
            CommandLine commandLine = parser.parse(options, args);

            if (commandLine.getArgList().size() < 1) {
                throw new Exception("Arguments missing - see the README for instructions");
            }

            if (commandLine.hasOption("g")) {
                String inputFilepath = commandLine.getArgList().get(0);
                String outputFilepath = commandLine.getOptionValue("w", DEFAULT_WORD_LIST_PATH);
                WiktionaryWordListGenerator.generateWordList(inputFilepath, outputFilepath);
            } else {
                String promptString = commandLine.getArgList().get(0);
                String wordListPath = commandLine.getOptionValue("w", DEFAULT_WORD_LIST_PATH);
                Solver.solve(promptString, wordListPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}