package com.example;

import org.springframework.stereotype.Service;
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
    
    /**
     * Loads data from text files when the application starts
     */
    @PostConstruct
    public void initialize() {
        loadDataFromFiles();
    }
    
    /**
     * Loads all text files from the data directory
     */
    private void loadDataFromFiles() {
        // Try multiple possible locations for the data directory
        String[] possiblePaths = {
            "src/main/java/com/example/data",  // Development path
            "data",                              // Root data folder
            "com/example/data",                  // Relative to classpath
            "./data"                             // Current directory
        };
        
        boolean loaded = false;
        
        for (String pathStr : possiblePaths) {
            try {
                Path dataDir = Paths.get(pathStr);
                System.out.println("Trying path: " + dataDir.toAbsolutePath());
                
                if (Files.exists(dataDir) && Files.isDirectory(dataDir)) {
                    List<Path> txtFiles = Files.list(dataDir)
                        .filter(path -> path.toString().endsWith(".txt"))
                        .collect(Collectors.toList());
                    
                    if (!txtFiles.isEmpty()) {
                        System.out.println("Found " + txtFiles.size() + " text files in: " + dataDir.toAbsolutePath());
                        txtFiles.forEach(this::loadFile);
                        loaded = true;
                        break;
                    }
                }
            } catch (IOException e) {
                // Continue to next path
            }
        }
        
        if (!loaded) {
            System.out.println("Could not find data directory in any expected location.");
            System.out.println("Loading sample data instead...");
            loadSampleData();
        }
    }
    
    /**
     * Loads a single text file into the trie
     */
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
    
    /**
     * Processes a line of text and inserts words into the trie
     * Returns the number of words inserted
     */
    private int processLine(String line) {
        // Clean and split the line into words
        String[] words = line.toLowerCase()
                            .replaceAll("[^a-z\\s]", "") // Remove non-letter chars except spaces
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
    
    /**
     * Loads sample data if no files are found
     */
    private void loadSampleData() {
        String[] sampleWords = {
            "hello", "hello", "hello", "help", "help", "world", 
            "welcome", "wonderful", "wonder", "word", "work",
            "test", "testing", "trie", "tree", "try"
        };
        
        for (String word : sampleWords) {
            trie.insert(word);
        }
        
        System.out.println("Sample data loaded");
    }
    
    /**
     * Gets predictions for the next word based on the input
     * Returns up to 5 predictions
     */
    public List<String> getNextWordPredictions(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Get the last word being typed
        String[] words = input.trim().toLowerCase().split("\\s+");
        String prefix = words[words.length - 1];
        
        List<String> predictions = new ArrayList<>();
        
        // Get the most likely word completion
        String completion = trie.autocompleteWord(prefix);
        if (completion != null && !completion.isEmpty() && !completion.equals(prefix)) {
            predictions.add(completion);
        }
        
        // Add some alternative predictions by trying different characters
        for (char c = 'a'; c <= 'z' && predictions.size() < 5; c++) {
            String testWord = trie.autocompleteWord(prefix + c);
            if (testWord != null && !testWord.isEmpty() && !predictions.contains(testWord)) {
                predictions.add(testWord);
            }
        }
        
        return predictions;
    }
    
    /**
     * Gets predictions for the next character based on the input
     * Returns up to 5 most likely characters
     */
    public List<Character> getNextCharPredictions(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Get the last word being typed
        String[] words = input.trim().toLowerCase().split("\\s+");
        String prefix = words[words.length - 1];
        
        List<Character> predictions = new ArrayList<>();
        
        // Get the most likely next character
        char mostLikely = trie.mostLikelyNextChar(prefix);
        if (mostLikely != '_') {
            predictions.add(mostLikely);
        }
        
        // Try to get additional likely characters by exploring the trie
        // This is a simple approach - you can enhance this based on your needs
        for (char c = 'a'; c <= 'z' && predictions.size() < 5; c++) {
            if (!predictions.contains(c) && trie.contains(prefix + c)) {
                predictions.add(c);
            }
        }
        
        return predictions;
    }
    
    /**
     * Checks if a word exists in the trie
     */
    public boolean containsWord(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        return trie.contains(word.toLowerCase());
    }
    
    /**
     * Gets spell check suggestion for a word
     */
    public String getSpellCheckSuggestion(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }
        return trie.spellCheck(word.toLowerCase());
    }
    
    /**
     * Generates random text block
     */
    public String generateRandomText(int wordCount) {
        return trie.randomTextBlock(wordCount);
    }
}