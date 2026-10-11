package com.rohini.wordchain.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rohini.wordchain.dto.GuessResponse;
import com.rohini.wordchain.model.Game;
import com.rohini.wordchain.model.Guess;
import com.rohini.wordchain.service.GameService;
import com.rohini.wordchain.service.GameUpdateService;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.rohini.wordchain.model.Player;
import com.rohini.wordchain.repository.UserRepository;
import com.rohini.wordchain.dto.GameStatusResponse;

import com.rohini.wordchain.dto.InvitationRequest;
import com.rohini.wordchain.service.BrevoEmailService;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    private final UserRepository userRepository;
    private final BrevoEmailService brevoEmailService;
    private final GameUpdateService gameUpdateService;
    public GameController(GameService gameService,
    		BrevoEmailService brevoEmailService,
    		UserRepository userRepository,
    		GameUpdateService gameUpdateService) {
        this.gameService = gameService;
        this.brevoEmailService=brevoEmailService;
        this.userRepository=userRepository;
        this.gameUpdateService=gameUpdateService;  }
    


	@PostMapping("/{roomCode}/join")
	public Player joinGame(
	        @PathVariable String roomCode,
	        @AuthenticationPrincipal String username) {
	
	    Player player = gameService.joinGame(roomCode, username);
	
	    gameUpdateService.broadcastGameStatus(
	            roomCode,
	            gameService.getGameStatus(roomCode)
	    );
	
	    return player;
	}



	@PostMapping("/{roomCode}/start")
	public Game startGame(
	        @PathVariable String roomCode,
	        @AuthenticationPrincipal String username) {
	
	    Game game = gameService.startGame(roomCode, username);
	
	    gameUpdateService.broadcastGameStatus(
	            roomCode,
	            gameService.getGameStatus(roomCode)
	    );
	
	    return game;
	}



		@PostMapping
		public Game createGame(
		        @RequestBody Map<String, Integer> request,
		        @AuthenticationPrincipal String username) {
		
		    int durationMinutes = request.get("durationMinutes");
		
		    Game game = gameService.createGame(durationMinutes);
		    game.setHostUsername(username);
		
		    return gameService.saveGame(game);
		}

   


		@PostMapping("/{roomCode}/guess")
		public Guess submitGuess(
		        @PathVariable String roomCode,
		        @AuthenticationPrincipal String username,
		        @RequestBody Guess guess) {
		
		    Player player = gameService.getPlayerByUsername(
		            roomCode, username
		    );
		
		    return gameService.submitGuess(
		            roomCode,
		            player.getId(),
		            guess.getWord()
		    );
		}




	@GetMapping("/{roomCode}/history")
	public List<GuessResponse> getGameHistory(
	        @PathVariable String roomCode,
	        @AuthenticationPrincipal String username) {
	
	    gameService.checkPlayerInGame(roomCode, username);
	    return gameService.getGameHistory(roomCode);
	}

	@GetMapping("/{roomCode}/time")
	public int getRemainingTime(
	        @PathVariable String roomCode,
	        @AuthenticationPrincipal String username) {
	
	    gameService.checkPlayerInGame(roomCode, username);
	    return gameService.getRemainingTime(roomCode);
	}
	
	@GetMapping("/{roomCode}/status")
	public GameStatusResponse getGameStatus(
	        @PathVariable String roomCode,
	        @AuthenticationPrincipal String username) {
	
	    gameService.checkPlayerInGame(roomCode, username);
	    return gameService.getGameStatus(roomCode);
	}
	
	@PostMapping("/{roomCode}/invite")
	public Map<String, String> inviteFriend(
	        @PathVariable String roomCode,
	        @AuthenticationPrincipal String username,
	        @RequestBody InvitationRequest request) {

	    Game game = gameService.getGameByRoomCode(roomCode);

	    if (!username.equals(game.getHostUsername())) {
	        throw new ResponseStatusException(
	                HttpStatus.FORBIDDEN,
	                "Only the host can invite friends"
	        );
	    }

	 // Only allow invitations while the game is waiting
	    if (!"WAITING".equalsIgnoreCase(game.getStatus())) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Invitations are allowed only before the game starts"
	        );
	    }
	    if (request.getEmail() == null ||
	            !request.getEmail().matches(
	                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Please provide a valid email address"
	        );
	    }
	    
	
	 // Prevent inviting yourself
	    String hostEmail = userRepository.findByUsername(username)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND,
	                    "Host account not found"
	            ))
	            .getEmail();

	    if (hostEmail != null &&
	            hostEmail.equalsIgnoreCase(request.getEmail().trim())) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "You cannot invite yourself"
	        );
	    }

	 
	    brevoEmailService.sendInvitation(
	            request.getEmail().trim(),
	            roomCode,
	            username
	    );

	    return Map.of("message", "Invitation sent successfully");
	}
	

}