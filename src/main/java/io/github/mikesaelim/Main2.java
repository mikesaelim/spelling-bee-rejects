package io.github.mikesaelim;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main2 {

    public static void main(String[] args) throws Exception {
        // You'll need to run this from the project folder
        String filepath = "data/output.txt";
        Set<Character> letters = Set.of('a', 't', 'l', 'i', 'r', 'y', 'v');
        Character centralLetter = 'v';

        List<String> matches = new ArrayList<>();
        List<String> panagrams = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            reader.lines().forEachOrdered((word) -> {
                MatchData matchData = matches(word, letters, centralLetter);
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

    static MatchData matches(String word, Set<Character> letters, Character centralLetter) {
        // TODO: handle accented characters?
        char[] chars = word.trim().toLowerCase().toCharArray();

        if (chars.length < 4) { return new MatchData(false, false); }

        boolean foundCentralLetter = false;
        Set<Character> foundLetters = new HashSet<>();
        for (char c : chars) {
            if (!letters.contains(c)) {
                return new MatchData(false, false);
            }
            foundLetters.add(c);
            if (c == centralLetter) {
                foundCentralLetter = true;
            }
        }

        return new MatchData(foundCentralLetter, foundLetters.size() == letters.size());
    }

    private record MatchData(boolean match, boolean panagram) {}
}