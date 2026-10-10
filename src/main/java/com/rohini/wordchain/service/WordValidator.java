package com.rohini.wordchain.service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WordValidator {

    private final Map<String, Map<String, List<String>>> dictionary;

    public WordValidator() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            InputStream inputStream =
                    getClass().getResourceAsStream("/dictionary/game_dictionary.json");

            dictionary = objectMapper.readValue(
                    inputStream,
                    new TypeReference<Map<String, Map<String, List<String>>>>() {}
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to load dictionary", e);
        }
    }

    public boolean isValid(String word) {

        word = word.toLowerCase().trim();

        if (word.isEmpty()) {
            return false;
        }

        String firstLetter = String.valueOf(word.charAt(0));
        String length = String.valueOf(word.length());

        Map<String, List<String>> wordsByLength =
                dictionary.get(firstLetter);

        if (wordsByLength == null) {
            return false;
        }

        List<String> words = wordsByLength.get(length);

        if (words == null) {
            return false;
        }

        //return words.contains(word);
        return Collections.binarySearch(words, word) >= 0;
    }
}