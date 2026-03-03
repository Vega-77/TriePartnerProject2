package com.example;

import org.springframework.stereotype.Service;

import com.example.Trie.CharacterProbabilityResult;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrieController {
    private Trie trie;

    public TrieController() {
        this.trie = new Trie();
    }

    @PostConstruct
    public void initialize() {
            loadDataFromFiles();
    }

    private void loadDataFromFiles() {
        String path = "app/src/main/java/com/example/data";

        try {
            Path dataDir = Paths.get(path);
            System.out.println("Trying path: " + dataDir.toAbsolutePath());

            if (Files.exists(dataDir) && Files.isDirectory(dataDir)) {
                List<Path> txtFiles = Files.list(dataDir)
                    .filter(file -> file.toString().endsWith(".txt"))
                    .collect(Collectors.toList());

                if (!txtFiles.isEmpty()) {
                    System.out.println("Found " + txtFiles.size() + " text files in: " + dataDir.toAbsolutePath());
                    txtFiles.forEach(this::loadFile);
                }
            }
        } catch (Exception e) {
            System.err.print(e);
        }
    }

    private void loadFile(Path filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            int wordCount = 0;
            while ((line = reader.readLine()) != null) {
                wordCount += processLine(line);
            }
            System.out.println("Loaded " + filePath.getFileName() + " - inserted " + wordCount + " words");
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
        }
    }

    private int processLine(String line) {
        String[] words = line.toLowerCase()
                            .replaceAll("[^a-z\\s]", "")
                            .split("\\s+");
        int count = 0;
        for (String word : words) {
            if (!word.isEmpty()) {
                trie.insert(word);
                count++;
            }
        }
        return count;
    }

    public List<String> getNextWordPredictions(String input) {
        if (input == null || input.trim().isEmpty()) return new ArrayList<>();

        String[] words = input.trim().toLowerCase().split("\\s+");
        String prefix = words[words.length - 1];

        Set<String> predictions = new HashSet<>();
        double randomScale = 0.2;
        int attempts = 0;

        while (true) {
            String completion = trie.autocompleteWord(prefix, randomScale);
            predictions.add(completion);
            randomScale *= 2;
            attempts++;
            if (predictions.size() == 5) break;
            if (attempts >= 100) break;
        }

        return new ArrayList<>(predictions);
    }

    public List<Character> getNextCharPredictions(String input) {
        if (input == null || input.trim().isEmpty()) return new ArrayList<>();

        String[] words = input.trim().toLowerCase().split("\\s+");
        String prefix = words[words.length - 1];

        List<Character> output = new ArrayList<>();
        List<CharacterProbabilityResult> predictions = trie.topNLikelyCharsPercent(prefix, 5);
        for (CharacterProbabilityResult cpr : predictions) {
            output.add(cpr.character());
        }
        return output;
    }

    public boolean containsWord(String word) {
        if (word == null || word.isEmpty()) return false;
        return trie.contains(word.toLowerCase());
    }

    public String generateRandomText(int wordCount) {
        return trie.randomTextBlock(wordCount);
    }
}
