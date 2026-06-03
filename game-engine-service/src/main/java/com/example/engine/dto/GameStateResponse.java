package com.example.engine.dto;

import com.example.engine.model.GameStatus;
import com.example.engine.model.PlayerSymbol;

public record GameStateResponse(
        String gameId,
        String[][] board,
        GameStatus status,
        PlayerSymbol nextPlayer
) {
}

