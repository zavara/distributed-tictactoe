package com.example.engine;

import com.example.engine.dto.GameStateResponse;
import com.example.engine.dto.MoveRequest;
import com.example.engine.exception.InvalidMoveException;
import com.example.engine.model.GameStatus;
import com.example.engine.model.PlayerSymbol;
import com.example.engine.repository.GameRepository;
import com.example.engine.service.GameService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameServiceTest {

    private final GameService gameService = new GameService(new GameRepository());

    @Test
    void shouldDetectWinnerForX() {
        String gameId = "game-win-x";

        gameService.makeMove(gameId, new MoveRequest(PlayerSymbol.X, 0, 0));
        gameService.makeMove(gameId, new MoveRequest(PlayerSymbol.O, 1, 0));
        gameService.makeMove(gameId, new MoveRequest(PlayerSymbol.X, 0, 1));
        gameService.makeMove(gameId, new MoveRequest(PlayerSymbol.O, 1, 1));
        GameStateResponse response = gameService.makeMove(gameId, new MoveRequest(PlayerSymbol.X, 0, 2));

        assertEquals(GameStatus.X_WON, response.status());
        assertEquals("X", response.board()[0][0]);
        assertEquals("X", response.board()[0][1]);
        assertEquals("X", response.board()[0][2]);
    }

    @Test
    void shouldRejectWrongTurn() {
        String gameId = "wrong-turn";
        gameService.makeMove(gameId, new MoveRequest(PlayerSymbol.X, 0, 0));

        assertThrows(InvalidMoveException.class,
                () -> gameService.makeMove(gameId, new MoveRequest(PlayerSymbol.X, 0, 1)));
    }
}

