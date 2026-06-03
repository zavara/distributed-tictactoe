package com.example.session.client.dto;

import com.example.session.model.PlayerSymbol;

public record EngineMoveRequest(
        PlayerSymbol player,
        int row,
        int col
) {
}

