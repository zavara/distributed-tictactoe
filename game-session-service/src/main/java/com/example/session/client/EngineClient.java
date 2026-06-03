package com.example.session.client;

import com.example.session.client.dto.EngineGameStateResponse;
import com.example.session.client.dto.EngineMoveRequest;

public interface EngineClient {

    EngineGameStateResponse move(String gameId, EngineMoveRequest request);

    EngineGameStateResponse getGame(String gameId);
}

