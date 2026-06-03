package com.example.session.exception;

public class SimulationAlreadyRunningException extends RuntimeException {

    public SimulationAlreadyRunningException(String sessionId) {
        super("Simulation already running for session: " + sessionId);
    }
}

