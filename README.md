# Distributed Tic Tac Toe

Distributed Tic Tac Toe implementation with two Spring Boot microservices and a lightweight UI.

## Services

- `game-engine-service` (port `8081`)
  - Owns Tic Tac Toe rules and game state
  - Validates moves and calculates win/draw
- `game-session-service` (port `8082`)
  - Creates sessions
  - Simulates both players automatically
  - Calls engine service over REST
  - Serves a simple UI at `/`

## API Overview

### Game Engine Service

- `POST /games/{gameId}/move`
  - Body:
	```json
	{
	  "player": "X",
	  "row": 0,
	  "col": 1
	}
	```
- `GET /games/{gameId}`

### Game Session Service

- `POST /sessions` - create session
- `POST /sessions/{sessionId}/simulate` - start async simulation
- `GET /sessions/{sessionId}` - get latest state + move history

## How to Run

### 1) Start game engine

```bash
cd /Users/serhiizavarytskyi/Desktop/distributed-tictactoe/game-engine-service
mvn spring-boot:run
```

### 2) Start session service

```bash
cd /Users/serhiizavarytskyi/Desktop/distributed-tictactoe/game-session-service
mvn spring-boot:run
```

### 3) Open UI

Open:

- `http://localhost:8082/`

Click **Start Simulation** to create a session and watch moves appear on the board.

## Testing

Run tests per service:

```bash
cd /Users/serhiizavarytskyi/Desktop/distributed-tictactoe/game-engine-service
mvn test

cd /Users/serhiizavarytskyi/Desktop/distributed-tictactoe/game-session-service
mvn test
```

Included tests cover:

- Engine logic and invalid move handling
- Engine REST integration (`/games/...` endpoints)
- Session flow: create session -> simulate -> terminal game state

## Notes

- State is stored in-memory (`ConcurrentHashMap`) as required.
- Session simulation runs asynchronously to support live polling from UI.
- Error responses are standardized for client-friendly handling.
