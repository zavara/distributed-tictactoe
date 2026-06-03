package com.example.engine;

import com.example.engine.dto.GameStateResponse;
import com.example.engine.dto.MoveRequest;
import com.example.engine.dto.ErrorResponse;
import com.example.engine.model.GameStatus;
import com.example.engine.model.PlayerSymbol;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldPlayUntilDrawViaRestApi() {
        String gameId = "draw-game";

        move(gameId, PlayerSymbol.X, 0, 0);
        move(gameId, PlayerSymbol.O, 0, 1);
        move(gameId, PlayerSymbol.X, 0, 2);
        move(gameId, PlayerSymbol.O, 1, 1);
        move(gameId, PlayerSymbol.X, 1, 0);
        move(gameId, PlayerSymbol.O, 1, 2);
        move(gameId, PlayerSymbol.X, 2, 1);
        move(gameId, PlayerSymbol.O, 2, 0);
        ResponseEntity<GameStateResponse> finalMove = move(gameId, PlayerSymbol.X, 2, 2);

        assertEquals(HttpStatus.OK, finalMove.getStatusCode());
        assertEquals(GameStatus.DRAW, finalMove.getBody().status());
    }

    @Test
    void shouldReturnConflictForOccupiedCell() {
        String gameId = "occupied-cell";
        move(gameId, PlayerSymbol.X, 0, 0);

        ResponseEntity<ErrorResponse> invalidResponse = restTemplate.exchange(
                url("/games/" + gameId + "/move"),
                HttpMethod.POST,
                new HttpEntity<>(new MoveRequest(PlayerSymbol.O, 0, 0)),
                ErrorResponse.class
        );

        assertEquals(HttpStatus.CONFLICT, invalidResponse.getStatusCode());
    }

    private ResponseEntity<GameStateResponse> move(String gameId, PlayerSymbol player, int row, int col) {
        return restTemplate.postForEntity(
                url("/games/" + gameId + "/move"),
                new MoveRequest(player, row, col),
                GameStateResponse.class
        );
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}

