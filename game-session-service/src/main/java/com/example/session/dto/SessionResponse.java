package com.example.session.dto;

import com.example.session.model.GameStatus;
import com.example.session.model.MoveHistoryEntry;
import com.example.session.model.PlayerSymbol;
import com.example.session.model.SimulationStatus;

import java.time.Instant;
import java.util.List;

public record SessionResponse(
        String sessionId,
        String gameId,
        String[][] board,
        GameStatus gameStatus,
        SimulationStatus simulationStatus,
        PlayerSymbol nextPlayer,
        List<MoveHistoryEntry> moveHistory,
        String errorMessage,
        Instant createdAt,
        Instant updatedAt
) {
}

