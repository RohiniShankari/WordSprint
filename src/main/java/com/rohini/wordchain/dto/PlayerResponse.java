
package com.rohini.wordchain.dto;

public class PlayerResponse {

    private Long id;
    private String username;
    private int score;
    private int turnOrder;

    public PlayerResponse(Long id, String username, int score, int turnOrder) {
        this.id = id;
        this.username = username;
        this.score = score;
        this.turnOrder = turnOrder;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public int getScore() { return score; }
    public int getTurnOrder() { return turnOrder; }
}
