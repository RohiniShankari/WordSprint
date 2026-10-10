package com.rohini.wordchain.dto;

public class GuessResponse {

    private String word;
    private String username;
    private int points;

    public GuessResponse() {
    }

    public GuessResponse(String word, String username, int points) {
        this.word = word;
        this.username = username;
        this.points = points;
    }

    public String getWord() {
        return word;
    }

    public String getUsername() {
        return username;
    }

    public int getPoints() {
        return points;
    }
}