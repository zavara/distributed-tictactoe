package com.example.session.client.dto;

import com.example.session.model.GameStatus;
import com.example.session.model.PlayerSymbol;

public record EngineGameStateResponse(
        String gameId,
        String[][] board,
        GameStatus status,
        PlayerSymbol nextPlayer
) {
}

