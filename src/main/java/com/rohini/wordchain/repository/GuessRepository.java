package com.rohini.wordchain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rohini.wordchain.model.Guess;

public interface GuessRepository extends JpaRepository<Guess, Long> {

    List<Guess> findByGameId(Long gameId);

    boolean existsByGameIdAndWordIgnoreCase(Long gameId, String word);
}
