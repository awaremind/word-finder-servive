package com.wordservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordFinderStreamServiceTest {

  WordFinderStreamsService streamService;

  private Set<String> wordSet;


  @BeforeEach
  void setUp() {
    wordSet = ConcurrentHashMap.newKeySet();
    wordSet.add("startling");
    wordSet.add("starting");
    wordSet.add("staring");
    wordSet.add("string");
    wordSet.add("sting");
    wordSet.add("sing");
    wordSet.add("sin");
    wordSet.add("in");
  }


  @Test
  void testFindValidWords() {
    streamService = new WordFinderStreamsService(wordSet);
    Set<String> set = streamService.findValidWords();

    assertEquals(1, set.size());
  }
}
