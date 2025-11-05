package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import errors.ResponseException;
import io.javalin.http.Context;
import records.JoinGameRequest;
import service.Service;
import dataobjects.*;

import java.util.Collection;
import java.util.Map;

public class Handler {

    Service service;
    Gson gson;

    public Handler(){
        service = new Service();
        // removes null fields while making jsons
        gson = new Gson();
    }


    public void handleRegister(Context ctx) throws ResponseException{
        UserData userData = gson.fromJson(ctx.body(), UserData.class);
        nullDataCheck(userData.username(), userData.password(), userData.email());
        AuthData authData = service.register(userData);
        ctx.json(gson.toJson(authData));
    }

    public void handleLogin(Context ctx) throws ResponseException{
        UserData userData = gson.fromJson(ctx.body(), UserData.class);
        nullDataCheck(userData.username(), userData.password());
        AuthData authData = service.login(userData);
        ctx.json(gson.toJson(authData));
    }

    public void handleLogout(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");
        service.logout(authToken);
    }

    public void handleListGames(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");
        Collection<GameData> games =  service.listGames(authToken);
        ctx.json(gson.toJson(Map.of("games", games)));
    }

    public void handleCreateGame(Context ctx) throws ResponseException{
        String authToken = ctx.header("authorization");
        GameData gameDataName = gson.fromJson(ctx.body(), GameData.class);
        nullDataCheck(gameDataName.gameName());
        GameData gameDataID = service.createGame(authToken, gameDataName.gameName());
        ctx.json(gson.toJson(gameDataID));
    }

    public void handleJoinGame(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");
        JoinGameRequest joinGameRequest = gson.fromJson(ctx.body(), JoinGameRequest.class);
        nullDataCheck(joinGameRequest.gameID(), joinGameRequest.playerColor());
        service.joinGame(authToken, joinGameRequest);
    }

    public void handleClear(Context ctx) throws ResponseException{
        service.clearAll();
    }

    public void handleException(ResponseException error, Context ctx){
        int statusCode = exceptionToHttpCode(error);
        ctx.status(statusCode);
        ctx.json(error.toJson(statusCode));
    }

    private int exceptionToHttpCode(ResponseException error){
        return switch(error.getType()){
            case UNAUTHORIZED -> 401;
            case BAD_REQUEST -> 400;
            case ALREADY_TAKEN -> 403;
            case DATA_ACCESS_ERROR -> 500;
            default -> 500;
        };
    }

    private void nullDataCheck(Object... args) throws ResponseException {
        for(Object arg : args){
            if (arg == null){
                throw new ResponseException(ResponseException.Type.BAD_REQUEST);
            }
        }
    }
}
