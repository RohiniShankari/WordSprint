package com.rohini.wordchain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime turnStartedAt;
    private String roomCode;

    private String gameMode;

    private String status;

    private String currentWord;

    private int currentPlayer;

    private int turnTime;

    private int gameDuration; // Duration in seconds
    private LocalDateTime gameStartedAt;
    private LocalDateTime gameEndsAt;
    private String hostUsername;
    public Game() {
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getTurnStartedAt() {
        return turnStartedAt;
    }

    public void setTurnStartedAt(LocalDateTime turnStartedAt) {
        this.turnStartedAt = turnStartedAt;
    }
    
    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getTurnTime() {
        return turnTime;
    }

    public void setTurnTime(int turnTime) {
        this.turnTime = turnTime;
    }
    
    public int getGameDuration() {
        return gameDuration;
    }
    
    

    public void setGameDuration(int gameDuration) {
        this.gameDuration = gameDuration;
    }

    public LocalDateTime getGameStartedAt() {
        return gameStartedAt;
    }

    public void setGameStartedAt(LocalDateTime gameStartedAt) {
        this.gameStartedAt = gameStartedAt;
    }

    public LocalDateTime getGameEndsAt() {
        return gameEndsAt;
    }

    public void setGameEndsAt(LocalDateTime gameEndsAt) {
        this.gameEndsAt = gameEndsAt;
    }
    
    public String getHostUsername() {
        return hostUsername;
    }

    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }
}