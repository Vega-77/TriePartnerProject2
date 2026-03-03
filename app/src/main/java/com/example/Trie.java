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
		String output = "";

		for (int i = 0; i < N; i++) {
			output += " " + randomWord();
		}

		return output.substring(1);
	}

	public String randomWord() {
		return autocompleteWord(null, 1);
	}

	/**
	 * Returns a String completing prefix with some likely words (weighted random)
	 *
	 * @param prefix word to autocomplee
	 * @param randomScale amount to vary it by
	 * @return autocompleted word
	 */
	public String autocompleteWord(String prefix, double randomScale) {
	    String output = "";
		Node node = root;

		if (prefix == null) prefix = "";

		for (char c : prefix.toCharArray()) {
			if (!node.children.containsKey(c)) return null;
			
			output += c;
			node = node.children.get(c);
		}

		while (true) {
			if (node.children.isEmpty()) break;
			if (Math.random() < (double) node.endCount / node.passCount) break;

			long index = (long) (Math.random() * randomScale * node.passCount);

			Node selected = null;
			char selectedChar = 0;
			for (Map.Entry<Character, Node> entry : node.children.entrySet()) {
				index -= entry.getValue().passCount;

				if (index > 0) continue;

				selected = entry.getValue();
				selectedChar = entry.getKey();
				break;
			}

			// fallback: if index was too large, just pick the last child
			if (selected == null) {
				Map.Entry<Character, Node> last = null;
				for (Map.Entry<Character, Node> entry : node.children.entrySet()) last = entry;
				selected = last.getValue();
				selectedChar = last.getKey();
			}

			node = selected;
			output += selectedChar;
		}

		return output;
	}

	// mr haver I know this looks like ai naming conventions but I swear I can't think of what to name it
	// unless I shorten words but thats a terrible crime so I cant do that
	public record CharacterProbabilityResult(char character, float probablity) implements Comparable {
		public int compareTo(Object other) {
			CharacterProbabilityResult cpr = (CharacterProbabilityResult) other;
			if (probablity < cpr.probablity) return -1;
			if (probablity > cpr.probablity) return 1;
			return 0;
		}
	}

	/**
	 * Returns a String containing the top N most likely next chars
	 * after the given prefix, along with their percent likelihood.
	 *
	 * @param pre the prefix to search for
	 * @return a sorted CharacterProbabilityResult[] of the top N characters and percent
	 */
	public ArrayList<CharacterProbabilityResult> topNLikelyCharsPercent(String pre, int N) {
		ArrayList<CharacterProbabilityResult> characters = new ArrayList<>();
		
		Node parent = fetchNode(pre);
		if (parent == null) return characters;
		
		for (Map.Entry<Character, Node> entry : parent.children.entrySet()) {
			characters.add(new CharacterProbabilityResult(
				entry.getKey(), 
				(float) entry.getValue().passCount / parent.endCount
			));
		}

		ArrayList<CharacterProbabilityResult> result = new ArrayList<>();

		Collections.sort(characters);

		for (CharacterProbabilityResult cpr : characters) {
			if (result.size() == N) break;
			result.add(cpr);
		}

		return result;
	}

	/**
	 * Decrements the frequency of 1 word in the Trie
	 *
	 * @param word the word
	 * @return whether the delete was successful
	 */
	public boolean delete(String word) {
		if (word == null || word.length() == 0) return false;

		Node node = root;

		for (char c : word.toCharArray()) {
			node.passCount--;

			if (!node.children.containsKey(c)) return false;
			else node = node.children.get(c);
		}

		node.passCount--;
		node.endCount--;

		return false;
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

		Node node = fetchNode(word);
		if (node == null) return false;

		return node.isEndOfWord();
	}

	public char mostLikelyNextChar(String prefix) {
		if (prefix == null) return '_';

		Node node = fetchNode(prefix);
		if (node == null) return '_';

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

	private Node fetchNode(String prefix) {
		Node node = root;

		for (char c : prefix.toCharArray()) {
			if (node.children.containsKey(c)) node = node.children.get(c);
			else return null;
		}

		return node;
	}
}