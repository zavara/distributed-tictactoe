package com.example.engine.repository;

import com.example.engine.domain.Game;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class GameRepository {

    private final ConcurrentHashMap<String, Game> games = new ConcurrentHashMap<>();

    public Game getOrCreate(String gameId) {
        return games.computeIfAbsent(gameId, Game::new);
    }

    public Optional<Game> findById(String gameId) {
        return Optional.ofNullable(games.get(gameId));
    }
}

