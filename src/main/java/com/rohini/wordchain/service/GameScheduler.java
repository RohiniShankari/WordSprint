
package com.rohini.wordchain.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rohini.wordchain.model.Game;
import com.rohini.wordchain.model.Player;
import com.rohini.wordchain.repository.GameRepository;
import com.rohini.wordchain.repository.PlayerRepository;

@Component
public class GameScheduler {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameService gameService;
    private final GameUpdateService gameUpdateService;

    public GameScheduler(GameRepository gameRepository,
                         PlayerRepository playerRepository,
                         GameService gameService,
                         GameUpdateService gameUpdateService) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.gameService = gameService;
        this.gameUpdateService = gameUpdateService;
    }

    @Scheduled(fixedRate = 1000)
    public void checkTurnTimeouts() {
        System.out.println("Scheduler is running");

        List<Game> games = gameRepository.findAll();

        for (Game game : games) {

            if (!"IN_PROGRESS".equals(game.getStatus())) {
                continue;
            }

            if (game.getGameEndsAt() != null &&
                    !LocalDateTime.now().isBefore(game.getGameEndsAt())) {

                game.setStatus("FINISHED");
                gameRepository.save(game);

                gameUpdateService.broadcastGameStatus(
                        game.getRoomCode(),
                        gameService.getGameStatus(game.getRoomCode())
                );

                continue;
            }

            if (game.getTurnStartedAt() == null) {
                continue;
            }

            long elapsed = Duration.between(
                    game.getTurnStartedAt(),
                    LocalDateTime.now()
            ).getSeconds();

            if (elapsed >= game.getTurnTime()) {

                List<Player> players =
                        playerRepository.findByGameId(game.getId());

                if (players.isEmpty()) {
                    continue;
                }

                int nextPlayer = game.getCurrentPlayer() + 1;

                if (nextPlayer > players.size()) {
                    nextPlayer = 1;
                }

                game.setCurrentPlayer(nextPlayer);
                game.setTurnStartedAt(LocalDateTime.now());

                gameRepository.save(game);

                gameUpdateService.broadcastGameStatus(
                        game.getRoomCode(),
                        gameService.getGameStatus(game.getRoomCode())
                );
            }
        }
    }
}
