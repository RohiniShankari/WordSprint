
package com.rohini.wordchain.dto;

import java.util.List;

public class GameStatusResponse {

    private String roomCode;
    private String status;
    private String currentWord;
    private int currentPlayer;
    private int remainingTime;
    private List<PlayerResponse> players;

    public GameStatusResponse(String roomCode, String status,
            String currentWord, int currentPlayer, int remainingTime,
            List<PlayerResponse> players) {
        this.roomCode = roomCode;
        this.status = status;
        this.currentWord = currentWord;
        this.currentPlayer = currentPlayer;
        this.remainingTime = remainingTime;
        this.players = players;
    }

    public String getRoomCode() { return roomCode; }
    public String getStatus() { return status; }
    public String getCurrentWord() { return currentWord; }
    public int getCurrentPlayer() { return currentPlayer; }
    public int getRemainingTime() { return remainingTime; }
    public List<PlayerResponse> getPlayers() { return players; }
}
