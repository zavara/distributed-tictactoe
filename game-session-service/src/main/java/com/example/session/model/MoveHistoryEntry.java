package com.example.session.model;

import java.time.Instant;

public record MoveHistoryEntry(
        PlayerSymbol player,
        int row,
        int col,
        GameStatus statusAfterMove,
        Instant playedAt
) {
}

