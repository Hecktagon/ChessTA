package server;

import errors.ResponseException;
import handler.Handler;
import io.javalin.*;
import websocket.WebSocketHandler;

public class Server {

    private final Javalin javalin;

    public Server() {
        Handler handler = new Handler();
        WebSocketHandler wsHandler = new WebSocketHandler();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", handler::handleRegister)
                .post("/session", handler::handleLogin)
                .delete("/session", handler::handleLogout)
                .get("/game", handler::handleListGames)
                .post("/game", handler::handleCreateGame)
                .put("/game", handler::handleJoinGame)
                .delete("/db", handler::handleClear)
                .exception(ResponseException.class, handler::handleException)
                .ws("/ws", ws -> {
                    ws.onConnect(wsHandler);
                    ws.onMessage(wsHandler);
                    ws.onClose(wsHandler);
                });
        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
