package com.example.engine.exception;

public class GameAlreadyFinishedException extends RuntimeException {

    public GameAlreadyFinishedException(String message) {
        super(message);
    }
}

