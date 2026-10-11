
package com.rohini.wordchain.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.rohini.wordchain.dto.GuessResponse;
import com.rohini.wordchain.model.Game;
import com.rohini.wordchain.model.Guess;
import com.rohini.wordchain.model.Player;
import com.rohini.wordchain.repository.GameRepository;
import com.rohini.wordchain.repository.GuessRepository;
import com.rohini.wordchain.repository.PlayerRepository;

import com.rohini.wordchain.dto.GameStatusResponse;
import com.rohini.wordchain.dto.PlayerResponse;



@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GuessRepository guessRepository;
    private final WordValidator wordValidator;
    private final GameUpdateService gameUpdateService;
    
    public GameService(GameRepository gameRepository,
            PlayerRepository playerRepository,
            GuessRepository guessRepository,
            WordValidator wordValidator,
            GameUpdateService gameUpdateService) {
			
			this.gameRepository = gameRepository;
			this.playerRepository = playerRepository;
			this.guessRepository = guessRepository;
			this.wordValidator = wordValidator;
			this.gameUpdateService=gameUpdateService;
			}


		
		public Game createGame(int durationMinutes) {
		
		    if (durationMinutes != 2 &&
		        durationMinutes != 5 &&
		        durationMinutes != 10) {
		        throw new RuntimeException(
		                "Game duration must be 2, 5, or 10 minutes");
		    }
		
		    Game game = new Game();
		
		    game.setRoomCode(
		            UUID.randomUUID().toString().substring(0, 6).toUpperCase());
		    game.setGameMode("MULTIPLAYER");
		    game.setStatus("WAITING");
		    game.setCurrentWord("");
		    game.setCurrentPlayer(1);
		    game.setTurnTime(15);
		
		    game.setGameDuration(durationMinutes * 60);
		
		    return gameRepository.save(game);
		}

    public Player joinGame(String roomCode, String username) {

        Game game = gameRepository.findByRoomCode(roomCode);

        if (game == null) {
            throw new RuntimeException("Game not found");
        }
        if (!game.getStatus().equals("WAITING")) {
            throw new RuntimeException("Game has already started");
        }

        Player player = new Player();

        player.setUsername(username);
        player.setGameId(game.getId());
        player.setScore(0);

        List<Player> players = playerRepository.findByGameId(game.getId());
        if (playerRepository.existsByGameIdAndUsername(
                game.getId(), username.trim())) {
            throw new RuntimeException("Player has already joined this game");
        }
        if (players.size() >= 4) {
            throw new RuntimeException("Game is full");
        }

        player.setTurnOrder(players.size() + 1);

        return playerRepository.save(player);
    }

    public Game startGame(String roomCode,String username) {

        Game game = gameRepository.findByRoomCode(roomCode);

        if (game == null) {
            throw new RuntimeException("Game not found");
        }
        if (!username.equals(game.getHostUsername())) {
            throw new RuntimeException("Only the host can start the game");
        }
        if (!game.getStatus().equals("WAITING")) {
            throw new RuntimeException("Game has already started");
        }

        List<Player> players = playerRepository.findByGameId(game.getId());

        if (players.size() < 2) {
            throw new RuntimeException("At least 2 players are required");
        }
        
        LocalDateTime now = LocalDateTime.now();

        game.setStatus("IN_PROGRESS");
        game.setCurrentWord("");
        game.setGameStartedAt(now);
        game.setGameEndsAt(now.plusSeconds(game.getGameDuration()));
        game.setTurnStartedAt(now);

        return gameRepository.save(game);
    }

    public Guess submitGuess(String roomCode, Long playerId, String word) {

        Game game = gameRepository.findByRoomCode(roomCode);

        if (game == null) {
            throw new RuntimeException("Game not found");
        }

        if (!game.getStatus().equals("IN_PROGRESS")) {
            throw new RuntimeException("Game is not in progress");
        }

        if (game.getGameEndsAt() != null &&
                !LocalDateTime.now().isBefore(game.getGameEndsAt())) {

            game.setStatus("FINISHED");
            gameRepository.save(game);

            throw new RuntimeException("The game has ended");
        }
        
        Player player = playerRepository.findById(playerId).orElse(null);

        if (player == null) {
            throw new RuntimeException("Player not found");
        }

        if (!player.getGameId().equals(game.getId())) {
            throw new RuntimeException("Player does not belong to this game");
        }

        if (player.getTurnOrder() != game.getCurrentPlayer()) {
            throw new RuntimeException("It is not your turn");
        }

        LocalDateTime now = LocalDateTime.now();

        if (game.getTurnStartedAt() != null &&
                Duration.between(game.getTurnStartedAt(), now).getSeconds() >= game.getTurnTime()) {

            List<Player> players = playerRepository.findByGameId(game.getId());

            int nextPlayer = game.getCurrentPlayer() + 1;

            if (nextPlayer > players.size()) {
                nextPlayer = 1;
            }

            game.setCurrentPlayer(nextPlayer);
            game.setTurnStartedAt(now);
            gameRepository.save(game);

            throw new RuntimeException("Time is up! Your turn has been skipped");
        }
        
        word = word.trim().toLowerCase();

        if (word.isEmpty()) {
            throw new RuntimeException("Word cannot be empty");
        }
        if (!wordValidator.isValid(word)) {
            throw new RuntimeException("Invalid English word");
        }

        if (!game.getCurrentWord().isEmpty()) {

            char requiredLetter =
                    game.getCurrentWord()
                        .charAt(game.getCurrentWord().length() - 1);

            if (word.charAt(0) != requiredLetter) {
                throw new RuntimeException(
                        "Word must start with " + requiredLetter);
            }
        }

        if (guessRepository.existsByGameIdAndWordIgnoreCase(game.getId(), word)) {
            throw new RuntimeException("Word has already been used");
        }

        Guess guess = new Guess();

        guess.setGameId(game.getId());
        guess.setPlayerId(playerId);
        guess.setWord(word);
        guess.setValid(true);
        int points = calculatePoints(word);
        guess.setPoints(points);

        guessRepository.save(guess);

        player.setScore(player.getScore() + points);
        playerRepository.save(player);

        game.setCurrentWord(word);

        List<Player> players = playerRepository.findByGameId(game.getId());

        int nextPlayer = game.getCurrentPlayer() + 1;

        if (nextPlayer > players.size()) {
            nextPlayer = 1;
        }

        game.setCurrentPlayer(nextPlayer);
 
        game.setTurnStartedAt(LocalDateTime.now());
        gameRepository.save(game);

        gameUpdateService.broadcastGameStatus(
                roomCode,
                getGameStatus(roomCode)
        );
        
        return guess;
    }
    public List<GuessResponse> getGameHistory(String roomCode) {

        Game game = gameRepository.findByRoomCode(roomCode);

        if (game == null) {
            throw new RuntimeException("Game not found");
        }

        List<Guess> guesses = guessRepository.findByGameId(game.getId());

        return guesses.stream()
                .map(guess -> {
                    Player player = playerRepository
                            .findById(guess.getPlayerId())
                            .orElseThrow(() -> new RuntimeException("Player not found"));

                    return new GuessResponse(
                            guess.getWord(),
                            player.getUsername(),
                            guess.getPoints()
                    );
                })
                .toList();
    }

	public GameStatusResponse getGameStatus(String roomCode) {
	
	    Game game = gameRepository.findByRoomCode(roomCode);
	
	    if (game == null) {
	        throw new RuntimeException("Game not found");
	    }
	
	    List<Player> players = playerRepository.findByGameId(game.getId());
	
	    List<PlayerResponse> playerResponses = players.stream()
	            .map(player -> new PlayerResponse(
	                    player.getId(),
	                    player.getUsername(),
	                    player.getScore(),
	                    player.getTurnOrder()
	            ))
	            .toList();
	
	    String winner = null;
	    boolean draw = false;

	    if ("FINISHED".equals(game.getStatus()) && !players.isEmpty()) {

	        int highestScore = players.stream()
	                .mapToInt(Player::getScore)
	                .max()
	                .orElse(0);

	        List<Player> topPlayers = players.stream()
	                .filter(player -> player.getScore() == highestScore)
	                .toList();

	        if (topPlayers.size() == 1) {
	            winner = topPlayers.get(0).getUsername();
	        } else {
	            draw = true;
	        }
	    }
	    return new GameStatusResponse(
	            game.getRoomCode(),
	            game.getStatus(),
	            game.getCurrentWord(),
	            game.getCurrentPlayer(),
	            getRemainingTime(roomCode),
	            playerResponses,
	            winner,
	            draw
	    );
	}


	public int getRemainingTime(String roomCode) {
	
	    Game game = gameRepository.findByRoomCode(roomCode);
	
	    if (game == null) {
	        throw new RuntimeException("Game not found");
	    }
	
	    if (!"IN_PROGRESS".equals(game.getStatus())) {
	        return 0;
	    }
	
	    long elapsed = Duration.between(
	            game.getTurnStartedAt(),
	            LocalDateTime.now()
	    ).getSeconds();
	
	    int remaining = game.getTurnTime() - (int) elapsed;
	
	    return Math.max(0, remaining);
	}
	



    private int calculatePoints(String word) {

        int length = word.length();

        if (length <= 2) {
            return 5;
        } else if (length <= 4) {
            return 10;
        } else if (length <= 6) {
            return 15;
        } else if (length <= 8) {
            return 20;
        } else {
            return 25;
        }
    }
    
		
		public Player getPlayerByUsername(String roomCode, String username) {
		    Game game = gameRepository.findByRoomCode(roomCode);
		
		    if (game == null) {
		        throw new RuntimeException("Game not found");
		    }
		
		    return playerRepository.findByGameIdAndUsername(
		            game.getId(), username
		    ).orElseThrow(() ->
		            new RuntimeException("You have not joined this game"));
		}

		public Game saveGame(Game game) {
		    return gameRepository.save(game);
		}


	public void checkPlayerInGame(String roomCode, String username) {
	    Game game = gameRepository.findByRoomCode(roomCode);
	
	    if (game == null) {
	        throw new RuntimeException("Game not found");
	    }
	
	    playerRepository.findByGameIdAndUsername(game.getId(), username)
	            .orElseThrow(() ->
	                    new RuntimeException("You have not joined this game"));
	}
	public Game getGameByRoomCode(String roomCode) {
	    Game game = gameRepository.findByRoomCode(roomCode);

	    if (game == null) {
	        throw new RuntimeException("Game not found");
	    }

	    return game;
	}


}