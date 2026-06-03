package com.example.session.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SessionState {

    private final String sessionId;
    private final String gameId;
    private final Instant createdAt;
    private Instant updatedAt;
    private final List<MoveHistoryEntry> history = new ArrayList<>();

    private GameStatus gameStatus = GameStatus.IN_PROGRESS;
    private SimulationStatus simulationStatus = SimulationStatus.NEW;
    private PlayerSymbol nextPlayer = PlayerSymbol.X;
    private String[][] board = new String[3][3];
    private String errorMessage;

    public SessionState(String sessionId, Instant createdAt) {
        this.sessionId = sessionId;
        this.gameId = sessionId;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public synchronized String sessionId() {
        return sessionId;
    }

    public synchronized String gameId() {
        return gameId;
    }

    public synchronized Instant createdAt() {
        return createdAt;
    }

    public synchronized Instant updatedAt() {
        return updatedAt;
    }

    public synchronized GameStatus gameStatus() {
        return gameStatus;
    }

    public synchronized SimulationStatus simulationStatus() {
        return simulationStatus;
    }

    public synchronized PlayerSymbol nextPlayer() {
        return nextPlayer;
    }

    public synchronized String[][] boardCopy() {
        String[][] copy = new String[3][3];
        for (int row = 0; row < 3; row++) {
            System.arraycopy(board[row], 0, copy[row], 0, 3);
        }
        return copy;
    }

    public synchronized List<MoveHistoryEntry> historyCopy() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    public synchronized String errorMessage() {
        return errorMessage;
    }

    public synchronized void markRunning() {
        simulationStatus = SimulationStatus.RUNNING;
        errorMessage = null;
        touch();
    }

    public synchronized void markFinished() {
        simulationStatus = SimulationStatus.FINISHED;
        touch();
    }

    public synchronized void markFailed(String message) {
        simulationStatus = SimulationStatus.FAILED;
        errorMessage = message;
        touch();
    }

    public synchronized void applyEngineState(String[][] newBoard, GameStatus newStatus, PlayerSymbol next, MoveHistoryEntry move) {
        board = newBoard;
        gameStatus = newStatus;
        nextPlayer = next;
        history.add(move);
        touch();
    }

    private void touch() {
        updatedAt = Instant.now();
    }
}

