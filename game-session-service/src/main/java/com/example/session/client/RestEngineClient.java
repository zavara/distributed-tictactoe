package com.example.session.client;

import com.example.session.client.dto.EngineGameStateResponse;
import com.example.session.client.dto.EngineMoveRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class RestEngineClient implements EngineClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public RestEngineClient(RestClient engineRestClient, ObjectMapper objectMapper) {
        this.restClient = engineRestClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public EngineGameStateResponse move(String gameId, EngineMoveRequest request) {
        try {
            String body = objectMapper.writeValueAsString(request);
            String response = restClient.post()
                    .uri("/games/{gameId}/move", gameId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
            if (response == null || response.isBlank()) {
                throw new RestClientException("Engine returned empty response body for move");
            }
            return objectMapper.readValue(response, EngineGameStateResponse.class);
        } catch (JsonProcessingException ex) {
            throw new RestClientException("Cannot serialize or parse engine response", ex);
        }
    }

    @Override
    public EngineGameStateResponse getGame(String gameId) {
        try {
            String response = restClient.get()
                    .uri("/games/{gameId}", gameId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);
            if (response == null || response.isBlank()) {
                throw new RestClientException("Engine returned empty response body for getGame");
            }
            return objectMapper.readValue(response, EngineGameStateResponse.class);
        } catch (JsonProcessingException ex) {
            throw new RestClientException("Cannot parse engine response", ex);
        }
    }
}

