package com.example.engine.model;

public enum PlayerSymbol {
    X,
    O;

    public PlayerSymbol opposite() {
        return this == X ? O : X;
    }
}

