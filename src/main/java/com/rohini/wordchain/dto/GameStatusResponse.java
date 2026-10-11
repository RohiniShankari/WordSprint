
package com.rohini.wordchain.dto;

import java.util.List;

public class GameStatusResponse {

    private String roomCode;
    private String status;
    private String currentWord;
    private int currentPlayer;
    private int remainingTime;
    private List<PlayerResponse> players;

    private String winner;
    private boolean draw;
    
    public GameStatusResponse(String roomCode, String status,
            String currentWord, int currentPlayer, int remainingTime,
            List<PlayerResponse> players,String winner,boolean draw) {
        this.roomCode = roomCode;
        this.status = status;
        this.currentWord = currentWord;
        this.currentPlayer = currentPlayer;
        this.remainingTime = remainingTime;
        this.players = players;
        this.winner = winner;
        this.draw = draw;
        
    }

    public String getRoomCode() { return roomCode; }
    public String getStatus() { return status; }
    public String getCurrentWord() { return currentWord; }
    public int getCurrentPlayer() { return currentPlayer; }
    public int getRemainingTime() { return remainingTime; }
    public List<PlayerResponse> getPlayers() { return players; }
    public String getWinner() {
        return winner;
    }

    public boolean isDraw() {
        return draw;
    }
}
