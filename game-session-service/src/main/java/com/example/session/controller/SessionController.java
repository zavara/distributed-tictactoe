package com.example.session.controller;

import com.example.session.dto.SessionResponse;
import com.example.session.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession() {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.createSession());
    }

    @PostMapping("/{sessionId}/simulate")
    public ResponseEntity<SessionResponse> simulate(@PathVariable String sessionId) {
        return ResponseEntity.accepted().body(sessionService.startSimulation(sessionId));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable String sessionId) {
        return ResponseEntity.ok(sessionService.getSession(sessionId));
    }
}

