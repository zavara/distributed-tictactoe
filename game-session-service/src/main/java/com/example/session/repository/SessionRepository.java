package com.example.session.repository;

import com.example.session.model.SessionState;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SessionRepository {

    private final ConcurrentHashMap<String, SessionState> sessions = new ConcurrentHashMap<>();

    public SessionState createNew() {
        String id = UUID.randomUUID().toString();
        SessionState session = new SessionState(id, Instant.now());
        sessions.put(id, session);
        return session;
    }

    public Optional<SessionState> findById(String id) {
        return Optional.ofNullable(sessions.get(id));
    }
}

