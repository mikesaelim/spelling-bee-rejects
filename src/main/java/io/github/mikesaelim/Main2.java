package io.github.mikesaelim;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Main2 {
    static final Pattern PROMPT_PATTERN = Pattern.compile("[a-z]:[a-z]{6}");

    public static void main(String[] args) throws Exception {
        // You'll need to run this from the project folder
        String filepath = "data/wordlist.txt";
        String promptString = args[0];
        if (promptString == null) {
            throw new Exception();  // TODO respond to bad inputs
        }
        Prompt prompt = processPrompt(promptString);

        List<String> matches = new ArrayList<>();
        List<String> panagrams = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            reader.lines().forEachOrdered((word) -> {
                MatchData matchData = matches(word, prompt);
                if (matchData.match()) {
                    matches.add(word);
                }
                if (matchData.panagram()) {
                    panagrams.add(word);
                }
            });
        }

        matches.stream().sorted().forEachOrdered(System.out::println);
        System.out.println();
        System.out.println("Panagrams:");
        panagrams.stream().sorted().forEachOrdered(System.out::println);
    }

    static Prompt processPrompt(String promptString) throws Exception {
        String cleanedPromptString = promptString.trim().toLowerCase();
        if (!PROMPT_PATTERN.matcher(cleanedPromptString).matches()) {
            throw new Exception();  // TODO respond to bad inputs
        }

        Set<Character> letters = new HashSet<>();
        for (char c : cleanedPromptString.toCharArray()) {
            if (c == ':') continue;
            letters.add(c);
        }
        if (letters.size() != 7) {
            throw new Exception();  // TODO respond to bad inputs
        }
        return new Prompt(letters, cleanedPromptString.charAt(0));
    }

    static MatchData matches(String word, Prompt prompt) {
        // TODO: handle accented characters?
        char[] chars = word.trim().toLowerCase().toCharArray();

        if (chars.length < 4) { return new MatchData(false, false); }

        boolean foundCentralLetter = false;
        Set<Character> foundLetters = new HashSet<>();
        for (char c : chars) {
            if (!prompt.letters().contains(c)) {
                return new MatchData(false, false);
            }
            foundLetters.add(c);
            if (c == prompt.centralLetter()) {
                foundCentralLetter = true;
            }
        }

        return new MatchData(foundCentralLetter, foundLetters.size() == prompt.letters().size());
    }

    private record Prompt(Set<Character> letters, Character centralLetter) {}
    private record MatchData(boolean match, boolean panagram) {}
}