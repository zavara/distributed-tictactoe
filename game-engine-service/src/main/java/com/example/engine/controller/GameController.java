package com.example.engine.controller;

import com.example.engine.dto.GameStateResponse;
import com.example.engine.dto.MoveRequest;
import com.example.engine.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<GameStateResponse> move(@PathVariable String gameId, @Valid @RequestBody MoveRequest request) {
        return ResponseEntity.ok(gameService.makeMove(gameId, request));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameStateResponse> get(@PathVariable String gameId) {
        return ResponseEntity.ok(gameService.getGame(gameId));
    }
}

