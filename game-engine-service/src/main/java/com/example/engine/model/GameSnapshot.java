package com.example.engine.model;

public record GameSnapshot(
        String gameId,
        String[][] board,
        GameStatus status,
        PlayerSymbol nextPlayer
) {
}

