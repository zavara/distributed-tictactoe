package com.example.engine.dto;

import com.example.engine.model.PlayerSymbol;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MoveRequest(
        @NotNull PlayerSymbol player,
        @Min(0) @Max(2) int row,
        @Min(0) @Max(2) int col
) {
}

