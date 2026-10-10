package com.rohini.wordchain.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rohini.wordchain.dto.GuessResponse;
import com.rohini.wordchain.model.Game;
import com.rohini.wordchain.model.Guess;
import com.rohini.wordchain.service.GameService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.rohini.wordchain.model.Player;
import com.rohini.wordchain.dto.GameStatusResponse;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }
    
    @PostMapping("/{roomCode}/join")
    public Player joinGame(
            @PathVariable String roomCode,
            @RequestBody Player player) {

        return gameService.joinGame(roomCode, player.getUsername());
    }
    @PostMapping("/{roomCode}/start")
    public Game startGame(@PathVariable String roomCode) {
        return gameService.startGame(roomCode);
    }
   
    @PostMapping
    public Game createGame() {
        return gameService.createGame();
    }
    
    @PostMapping("/{roomCode}/guess")
    public Guess submitGuess(
            @PathVariable String roomCode,
            @RequestBody Guess guess) {

        return gameService.submitGuess(
                roomCode,
                guess.getPlayerId(),
                guess.getWord()
        );
    }
    @GetMapping("/{roomCode}/history")
    public List<GuessResponse> getGameHistory(@PathVariable String roomCode){
        return gameService.getGameHistory(roomCode);
    }

    @GetMapping("/{roomCode}/time")
    public int getRemainingTime(@PathVariable String roomCode) {
        return gameService.getRemainingTime(roomCode);
    }

	
	@GetMapping("/{roomCode}/status")
	public GameStatusResponse getGameStatus(@PathVariable String roomCode) {
	    return gameService.getGameStatus(roomCode);
	}

    

  




}