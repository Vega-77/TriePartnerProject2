package com.example;

import java.util.*;

public class Trie {

	private Map<String, Long> words;
	public class Node {
		Map<Character, Node> children;

		long passCount;
		long endCount;

		public Node() {
			children = new HashMap<Character, Node>();
		}

		boolean isEndOfWord() {
			return endCount > 0;
		}

		@Override
		public String toString() {
			return "pass, end: " + passCount + ", " + endCount;
		}
	}

	private Node root;

	public Trie() {
		root = new Node();
	}

	/**
	 * Returns a String with N words generated using a weighted average
	 *
	 * @param N the amount of words to include
	 * @return block of text with N words
	 */
	public String randomTextBlock(int N) {

	    return "hello world this is I";
	}

	/**
	 * Returns a String completing prefix with the most likely word
	 *
	 * @param prefix word to autocomplee
	 * @return autocompleted word
	 */
	public String autocompleteWord(String prefix) {
	    return mostLikelyNextWord(prefix);
	}

	/**
	 * Returns a String containing the top N most likely next chars
	 * after the given prefix, along with their percent likelihood.
	 *
	 * @param pre the prefix to search for
	 * @param N the number of top characters to return
	 * @return a formatted String of the top N characters and percent
	 */
	public String topNLikelyCharsPercent(String pre, int N) {
		return "a(53%),e (22%), i(18%), o(5%), u(2%) [hardcoded]";
	}

	/**
	 * Returns a valid word that is closest to the given word
	 *
	 * @param word the invalid word
	 * @return a valid word in the trie
	 */
	public String spellCheck(String word) {
		return word;
	}


	public void insert(String word) {
		if (word == null || word.length() == 0) return;

		Node node = root;

		for (char c : word.toCharArray()) {
			node.passCount++;

			node.children.putIfAbsent(c, new Node());
			node = node.children.get(c);
		}

		node.passCount++;
		node.endCount++;
	}

	public boolean contains(String word) {
		if (word == null || word.length() == 0) return false;

		Node node = root;

		for (char c : word.toCharArray()) {
			if (node.children.containsKey(c)) node = node.children.get(c);
			else return false;
		}

		return node.isEndOfWord();
	}

	public char mostLikelyNextChar(String prefix) {
		if (prefix == null) return '_';

		Node node = root;

		for (char c : prefix.toCharArray()) {
			if (node.children.containsKey(c)) node = node.children.get(c);
			else return '_';
		}

		char top = '_';
		long count = 0;

		for (char c : node.children.keySet()) {
			long passCount = node.children.get(c).passCount;

			if ((c < top && passCount == count) || passCount > count) {
				top = c;
				count = passCount;
			}

		}

		return top;
	}

	public String mostLikelyNextWord(String prefix) {
        if (prefix == null) return "";

        Node node = root;

        for (char c : prefix.toCharArray()) {
            if (node.children.containsKey(c)) {
                node = node.children.get(c);
            } else {
                return "";
            }
        }

        return likeliestSuffix(new StringBuilder(prefix), node);
    }

    private String likeliestSuffix(StringBuilder prefix, Node node) {
        String bestWord = "";
        long maxCount = -1;

        if (node.endCount > 0) {
            bestWord = prefix.toString();
            maxCount = node.endCount;
        }

        for (Map.Entry<Character, Node> entry : node.children.entrySet()) {
            prefix.append(entry.getKey());

            String candidate = likeliestSuffix(prefix, entry.getValue());

            if (!candidate.isEmpty()) {

                Node temp = root;
                for (char c : candidate.toCharArray()) {
                    temp = temp.children.get(c);
                }
                long candidateCount = temp.endCount;

                if (candidateCount > maxCount) {
                    maxCount = candidateCount;
                    bestWord = candidate;
                }
            }

            prefix.setLength(prefix.length() - 1);
        }

        return bestWord;
    }

	public void printWordFrequencies(){
		words = new TreeMap<String, Long>();
		printingHelper(root, new StringBuilder());
		for(Map.Entry<String, Long> entry : words.entrySet())
			System.out.println(entry.getKey()+": "+entry.getValue());
	}

	private void printingHelper(Node node, StringBuilder currentWord){
        if(node.isEndOfWord())
            words.put(currentWord.toString(), node.endCount);
        for(Map.Entry<Character, Node> entry : node.children.entrySet()){
            currentWord.append(entry.getKey());
            printingHelper(entry.getValue(), currentWord);
            currentWord.deleteCharAt(currentWord.length() - 1);
        }
    }
}