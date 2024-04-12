package com.wordservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WordFinderStreamsService {

  public WordFinderStreamsService(Set<String> wordSet) {
    this.wordSet = wordSet;
  }

  private final Set<String> wordSet;
  private static final String WORDS_URL = "https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt";

  public void initializeWordSet() {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(WORDS_URL).openStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.length() < 10) {
          wordSet.add(line.trim());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean isValidWordChain(String word, Set<String> visitedWords) {
    if (word.length() == 1) {
      return wordSet.contains(word);
    }
    for (int i = 0; i < word.length(); i++) {
      String subWord = word.substring(0, i) + word.substring(i + 1);
      if (wordSet.contains(subWord) && !visitedWords.contains(subWord)) {
        visitedWords.add(subWord);
        if (isValidWordChain(subWord, visitedWords)) {
          return true;
        }
        visitedWords.remove(subWord);
      }
    }
    return false;
  }

  public Set<String> findValidWords() {
    return wordSet.stream().parallel()
        .filter(word -> word.length() == 9 && isValidWordChain(word, new HashSet<>()))
        .collect(Collectors.toSet());
  }

  public static void main(String[] args) {
    WordFinderStreamsService service = new WordFinderStreamsService(ConcurrentHashMap.newKeySet());
    service.initializeWordSet();

    long startTime = System.currentTimeMillis();
    Set<String> validWords = service.findValidWords();
    long endTime = System.currentTimeMillis();

    System.out.println("Found " + validWords.size() + " words");
    System.out.println("Time taken: " + (endTime - startTime) + "ms");
  }
}