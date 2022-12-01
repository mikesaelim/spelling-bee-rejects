package io.github.mikesaelim;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main2 {

    public static void main(String[] args) throws Exception {
        // You'll need to run this from the project folder
        String filepath = "data/output.txt";
        Set<Character> letters = Set.of('a', 'b', 'c', 'e', 'i', 'm', 'n');
        Character centralLetter = 'c';

        List<String> matches = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            reader.lines().forEachOrdered((word) -> {
                if (matches(word, letters, centralLetter)) {
                    matches.add(word);
                }
            });
        }

        matches.stream().sorted().forEachOrdered(System.out::println);
    }

    static boolean matches(String word, Set<Character> letters, Character centralLetter) {
        // TODO: handle accented characters?
        char[] chars = word.trim().toLowerCase().toCharArray();

        if (chars.length < 4) { return false; }

        boolean foundCentralLetter = false;
        for (char c : chars) {
            if (!letters.contains(c)) {
                return false;
            }
            if (c == centralLetter) {
                foundCentralLetter = true;
            }
        }

        return foundCentralLetter;
    }
}