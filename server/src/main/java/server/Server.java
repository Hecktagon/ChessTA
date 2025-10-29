package server;

import errors.ResponseException;
import handler.Handler;
import io.javalin.*;

public class Server {

    private Handler handler;
    private final Javalin javalin;

    public Server() {
        try{
            handler = new Handler(true);
        } catch (Exception e){
            System.out.printf("""
                    
                    ### WARNING ###
                    SQL Server failed:
                    Error:
                    %s
                    
                    Switching to local storage...
                    
                    """, e);
            handler = new Handler(false);
        }

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", handler::handleRegister)
                .post("/session", handler::handleLogin)
                .delete("/session", handler::handleLogout)
                .get("/game", handler::handleListGames)
                .post("/game", handler::handleCreateGame)
                .put("/game", handler::handleJoinGame)
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
