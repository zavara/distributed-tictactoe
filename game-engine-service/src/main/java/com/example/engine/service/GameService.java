package com.example.engine.service;

import com.example.engine.domain.Game;
import com.example.engine.dto.GameStateResponse;
import com.example.engine.dto.MoveRequest;
import com.example.engine.exception.GameNotFoundException;
import com.example.engine.model.GameSnapshot;
import com.example.engine.repository.GameRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public GameStateResponse makeMove(String gameId, MoveRequest request) {
        Game game = gameRepository.getOrCreate(gameId);
        GameSnapshot snapshot = game.applyMove(request.player(), request.row(), request.col());
        return toResponse(snapshot);
    }

    public GameStateResponse getGame(String gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
        return toResponse(game.snapshot());
    }

    private GameStateResponse toResponse(GameSnapshot snapshot) {
        return new GameStateResponse(snapshot.gameId(), snapshot.board(), snapshot.status(), snapshot.nextPlayer());
    }
}

