package server;

import errors.ResponseException;
import handler.Handler;
import io.javalin.*;

public class Server {

    private Handler handler;
    private final Javalin javalin;

    public Server() {
        handler = new Handler();
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", handler::handleRegister)
                .post("/session", handler::handleLogin)
                .delete("/db", handler::handleClear)
                .exception(ResponseException.class, handler::handleException);

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
