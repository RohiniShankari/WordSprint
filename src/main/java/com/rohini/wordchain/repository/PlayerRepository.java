package com.rohini.wordchain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rohini.wordchain.model.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByGameId(Long gameId);
    boolean existsByGameIdAndUsername(Long gameId, String username);
    
    Optional<Player> findByGameIdAndUsername(
            Long gameId, String username);
}