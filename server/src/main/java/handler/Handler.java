package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import errors.ResponseException;
import io.javalin.http.Context;
import service.Service;
import dataobjects.*;

public class Handler {

    Service service;
    Gson deserial;
    Gson serial;

    public Handler(){
        service = new Service();
        deserial = new GsonBuilder().serializeNulls().create();
        serial = new Gson();
    }

    public void handleRegister(Context ctx) throws ResponseException{
        UserData userData = serial.fromJson(ctx.body(), UserData.class);
        nullDataCheck(userData.username(), userData.password(), userData.email());
        AuthData authData = service.register(userData);
        ctx.json(deserial.toJson(authData));
    }

    public void handleLogin(Context ctx) throws ResponseException{
        UserData userData = serial.fromJson(ctx.body(), UserData.class);
        nullDataCheck(userData.username(), userData.password());
        AuthData authData = service.login(userData);
        ctx.json(deserial.toJson(authData));
    }

    public void handleLogout(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");
        service.logout(authToken);
    }

    public void handleCreateGame(Context ctx) throws ResponseException{
        String authToken = ctx.header("authorization");
        GameData gameDataName = serial.fromJson(ctx.body(), GameData.class);
        nullDataCheck(gameDataName.gameName());
        GameData gameDataID = new Gson()
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
