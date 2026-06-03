package com.example.session.service;

import com.example.session.client.EngineClient;
import com.example.session.client.dto.EngineGameStateResponse;
import com.example.session.client.dto.EngineMoveRequest;
import com.example.session.dto.SessionResponse;
import com.example.session.exception.SessionNotFoundException;
import com.example.session.exception.SimulationAlreadyRunningException;
import com.example.session.model.GameStatus;
import com.example.session.model.MoveHistoryEntry;
import com.example.session.model.PlayerSymbol;
import com.example.session.model.SessionState;
import com.example.session.model.SimulationStatus;
import com.example.session.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final EngineClient engineClient;
    private final Random random = new Random();
    private final ExecutorService simulationExecutor = Executors.newCachedThreadPool();
    private final ConcurrentHashMap<String, ReentrantLock> sessionLocks = new ConcurrentHashMap<>();

    public SessionService(SessionRepository sessionRepository, EngineClient engineClient) {
        this.sessionRepository = sessionRepository;
        this.engineClient = engineClient;
    }

    public SessionResponse createSession() {
        SessionState session = sessionRepository.createNew();
        return toResponse(session);
    }

    public SessionResponse getSession(String sessionId) {
        SessionState session = getExisting(sessionId);
        return toResponse(session);
    }

    public SessionResponse startSimulation(String sessionId) {
        SessionState session = getExisting(sessionId);
        ReentrantLock lock = sessionLocks.computeIfAbsent(sessionId, ignored -> new ReentrantLock());

        lock.lock();
        try {
            if (session.simulationStatus() == SimulationStatus.RUNNING) {
                throw new SimulationAlreadyRunningException(sessionId);
            }
            if (session.gameStatus() != GameStatus.IN_PROGRESS) {
                return toResponse(session);
            }
            session.markRunning();
            simulationExecutor.submit(() -> simulateUntilComplete(sessionId));
            return toResponse(session);
        } finally {
            lock.unlock();
        }
    }

    private void simulateUntilComplete(String sessionId) {
        SessionState session = getExisting(sessionId);

        try {
            while (session.gameStatus() == GameStatus.IN_PROGRESS) {
                PlayerSymbol player = session.nextPlayer() == null ? PlayerSymbol.X : session.nextPlayer();
                int[] move = chooseMove(session.boardCopy());

                if (move == null) {
                    session.markFailed("No legal moves available while game is still in progress");
                    return;
                }

                EngineGameStateResponse engineState = engineClient.move(
                        session.gameId(),
                        new EngineMoveRequest(player, move[0], move[1])
                );

                session.applyEngineState(
                        engineState.board(),
                        engineState.status(),
                        engineState.nextPlayer(),
                        new MoveHistoryEntry(player, move[0], move[1], engineState.status(), Instant.now())
                );

                Thread.sleep(250);
            }

            session.markFinished();
        } catch (RestClientResponseException ex) {
            String body = ex.getResponseBodyAsString();
            String message = (body == null || body.isBlank()) ? ex.getMessage() : ex.getMessage() + " | body: " + body;
            session.markFailed("Engine service error: " + message);
        } catch (RestClientException ex) {
            session.markFailed("Engine service error: " + ex.getMessage());
        } catch (Exception ex) {
            session.markFailed(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private int[] chooseMove(String[][] board) {
        List<int[]> freeCells = new ArrayList<>();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == null) {
                    freeCells.add(new int[]{row, col});
                }
            }
        }
        if (freeCells.isEmpty()) {
            return null;
        }
        return freeCells.get(random.nextInt(freeCells.size()));
    }

    private SessionState getExisting(String sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));
    }

    private SessionResponse toResponse(SessionState session) {
        return new SessionResponse(
                session.sessionId(),
                session.gameId(),
                session.boardCopy(),
                session.gameStatus(),
                session.simulationStatus(),
                session.nextPlayer(),
                session.historyCopy(),
                session.errorMessage(),
                session.createdAt(),
                session.updatedAt()
        );
    }
}

