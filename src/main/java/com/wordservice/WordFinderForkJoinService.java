package com.wordservice;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WordFinderForkJoinService {

    private static final String WORD_LIST_URL = "https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt";
    private static Set<String> wordSet = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void main(String[] args) {
      loadValidWords();

      long startTime = System.currentTimeMillis();
      List<String> nineLetterWords = findNineLetterWords();
      List<String> result = new ArrayList<>();


      ForkJoinPool forkJoinPool = new ForkJoinPool();
      result = forkJoinPool.invoke(new WordFinderTask(nineLetterWords));

      System.out.println(result.size());
      long endTime = System.currentTimeMillis();
      System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }

    private static void loadValidWords() {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(WORD_LIST_URL).openStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.length() < 10) {
            wordSet.add(line.trim().toLowerCase());
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    private static List<String> findNineLetterWords() {
      return wordSet.stream()
          .filter(word -> word.length() == 9)
          .toList();
    }

    private static class WordFinderTask extends RecursiveTask<List<String>> {
      private final List<String> words;

      public WordFinderTask(List<String> words) {
        this.words = words;
      }

      @Override
      protected List<String> compute() {
        if (words.size() <= 1000) {
          return processWords();
        } else {
          int mid = words.size() / 2;
          WordFinderTask left = new WordFinderTask(words.subList(0, mid));
          WordFinderTask right = new WordFinderTask(words.subList(mid, words.size()));
          left.fork();
          List<String> rightResult = right.compute();
          List<String> leftResult = left.join();
          leftResult.addAll(rightResult);
          return leftResult;
        }
      }

      private List<String> processWords() {
        List<String> result = new ArrayList<>();
        for (String word : words) {
          if (isValidSequence(word)) {
            result.add(word);
          }
        }
        return result;
      }

      private boolean isValidSequence(String word) {
        if (word.length() == 1) {
          return wordSet.contains(word);
        }
        for (int i = 0; i < word.length(); i++) {
          String reducedWord = word.substring(0, i) + word.substring(i + 1);
          if (wordSet.contains(reducedWord) && isValidSequence(reducedWord)) {
            return true;
          }
        }
        return false;
      }
    }

  @Test
  void testWordFinderTask() {
    wordSet.add("startling");
    wordSet.add("starting");
    wordSet.add("staring");
    wordSet.add("string");
    wordSet.add("sting");
    wordSet.add("sing");
    wordSet.add("sin");
    wordSet.add("in");
    wordSet.add("i");
    ForkJoinPool forkJoinPool = new ForkJoinPool();
    List<String> set = forkJoinPool.invoke(new WordFinderTask(List.of("startling")));

    assertEquals(1, set.size());
  }
}
