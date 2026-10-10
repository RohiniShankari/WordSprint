package com.rohini.wordchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rohini.wordchain.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {

    Game findByRoomCode(String roomCode);
}