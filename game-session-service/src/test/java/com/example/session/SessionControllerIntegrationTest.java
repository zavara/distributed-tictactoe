package com.example.session;

import com.example.session.client.EngineClient;
import com.example.session.client.dto.EngineGameStateResponse;
import com.example.session.client.dto.EngineMoveRequest;
import com.example.session.dto.SessionResponse;
import com.example.session.model.GameStatus;
import com.example.session.model.PlayerSymbol;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SessionControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldSimulateFullGameFlow() throws InterruptedException {
        ResponseEntity<SessionResponse> created = restTemplate.postForEntity(url("/sessions"), null, SessionResponse.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        String sessionId = created.getBody().sessionId();

        ResponseEntity<SessionResponse> started = restTemplate.postForEntity(
                url("/sessions/" + sessionId + "/simulate"),
                null,
                SessionResponse.class
        );
        assertEquals(HttpStatus.ACCEPTED, started.getStatusCode());

        SessionResponse snapshot = null;
        for (int i = 0; i < 30; i++) {
            ResponseEntity<SessionResponse> response = restTemplate.getForEntity(
                    url("/sessions/" + sessionId),
                    SessionResponse.class
            );
            snapshot = response.getBody();
            if (snapshot.simulationStatus().name().equals("FINISHED")) {
                break;
            }
            Thread.sleep(200);
        }

        assertNotNull(snapshot);
        assertTrue(snapshot.moveHistory().size() >= 5);
        assertTrue(snapshot.gameStatus() == GameStatus.DRAW
                || snapshot.gameStatus() == GameStatus.X_WON
                || snapshot.gameStatus() == GameStatus.O_WON);
    }

    @Test
    void shouldRejectSecondStartWhileRunning() {
        ResponseEntity<SessionResponse> created = restTemplate.postForEntity(url("/sessions"), null, SessionResponse.class);
        String sessionId = created.getBody().sessionId();

        restTemplate.postForEntity(url("/sessions/" + sessionId + "/simulate"), null, SessionResponse.class);

        ResponseEntity<String> secondStart = restTemplate.exchange(
                url("/sessions/" + sessionId + "/simulate"),
                HttpMethod.POST,
                HttpEntity.EMPTY,
                String.class
        );

        assertEquals(HttpStatus.CONFLICT, secondStart.getStatusCode());
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @TestConfiguration
    static class StubEngineConfig {

        @Bean
        @Primary
        EngineClient engineClient() {
            return new InMemoryEngineClient();
        }
    }

    static class InMemoryEngineClient implements EngineClient {

        private final Map<String, EngineGame> games = new ConcurrentHashMap<>();

        @Override
        public EngineGameStateResponse move(String gameId, EngineMoveRequest request) {
            EngineGame game = games.computeIfAbsent(gameId, ignored -> new EngineGame());
            return game.move(gameId, request);
        }

        @Override
        public EngineGameStateResponse getGame(String gameId) {
            EngineGame game = games.computeIfAbsent(gameId, ignored -> new EngineGame());
            return game.snapshot(gameId);
        }
    }

    static class EngineGame {

        private final String[][] board = new String[3][3];
        private PlayerSymbol next = PlayerSymbol.X;
        private GameStatus status = GameStatus.IN_PROGRESS;
        private int moves;

        synchronized EngineGameStateResponse move(String gameId, EngineMoveRequest request) {
            if (status != GameStatus.IN_PROGRESS) {
                return snapshot(gameId);
            }
            if (request.player() != next || board[request.row()][request.col()] != null) {
                return snapshot(gameId);
            }

            board[request.row()][request.col()] = request.player().name();
            moves++;
            if (hasWinner(request.player().name())) {
                status = request.player() == PlayerSymbol.X ? GameStatus.X_WON : GameStatus.O_WON;
            } else if (moves == 9) {
                status = GameStatus.DRAW;
            } else {
                next = next.opposite();
            }
            return snapshot(gameId);
        }

        synchronized EngineGameStateResponse snapshot(String gameId) {
            String[][] copy = new String[3][3];
            for (int i = 0; i < 3; i++) {
                System.arraycopy(board[i], 0, copy[i], 0, 3);
            }
            return new EngineGameStateResponse(gameId, copy, status, status == GameStatus.IN_PROGRESS ? next : null);
        }

        private boolean hasWinner(String symbol) {
            for (int i = 0; i < 3; i++) {
                if (symbol.equals(board[i][0]) && symbol.equals(board[i][1]) && symbol.equals(board[i][2])) {
                    return true;
                }
                if (symbol.equals(board[0][i]) && symbol.equals(board[1][i]) && symbol.equals(board[2][i])) {
                    return true;
                }
            }
            return symbol.equals(board[0][0]) && symbol.equals(board[1][1]) && symbol.equals(board[2][2])
                    || symbol.equals(board[0][2]) && symbol.equals(board[1][1]) && symbol.equals(board[2][0]);
        }
    }
}

