package com.example.engine.domain;

import com.example.engine.exception.GameAlreadyFinishedException;
import com.example.engine.exception.InvalidMoveException;
import com.example.engine.model.GameSnapshot;
import com.example.engine.model.GameStatus;
import com.example.engine.model.PlayerSymbol;

public class Game {

    private final String id;
    private final PlayerSymbol[][] board = new PlayerSymbol[3][3];
    private GameStatus status = GameStatus.IN_PROGRESS;
    private PlayerSymbol nextPlayer = PlayerSymbol.X;
    private int movesCount;

    public Game(String id) {
        this.id = id;
    }

    public synchronized GameSnapshot applyMove(PlayerSymbol player, int row, int col) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new GameAlreadyFinishedException("Game is already finished");
        }
        if (player != nextPlayer) {
            throw new InvalidMoveException("It is " + nextPlayer + " turn");
        }
        if (board[row][col] != null) {
            throw new InvalidMoveException("Cell [" + row + "," + col + "] is already occupied");
        }

        board[row][col] = player;
        movesCount++;
        updateStatusAfterMove(player);

        if (status == GameStatus.IN_PROGRESS) {
            nextPlayer = nextPlayer.opposite();
        }

        return snapshot();
    }

    public synchronized GameSnapshot snapshot() {
        return new GameSnapshot(id, boardCopy(), status, status == GameStatus.IN_PROGRESS ? nextPlayer : null);
    }

    private void updateStatusAfterMove(PlayerSymbol player) {
        if (hasWinner(player)) {
            status = player == PlayerSymbol.X ? GameStatus.X_WON : GameStatus.O_WON;
            return;
        }
        if (movesCount == 9) {
            status = GameStatus.DRAW;
        }
    }

    private boolean hasWinner(PlayerSymbol player) {
        for (int row = 0; row < 3; row++) {
            if (board[row][0] == player && board[row][1] == player && board[row][2] == player) {
                return true;
            }
        }

        for (int col = 0; col < 3; col++) {
            if (board[0][col] == player && board[1][col] == player && board[2][col] == player) {
                return true;
            }
        }

        return (board[0][0] == player && board[1][1] == player && board[2][2] == player)
                || (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    private String[][] boardCopy() {
        String[][] copy = new String[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                copy[row][col] = board[row][col] == null ? null : board[row][col].name();
            }
        }
        return copy;
    }
}

