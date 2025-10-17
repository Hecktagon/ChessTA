package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import errors.ResponseException;
import io.javalin.http.Context;
import service.Service;
import dataobjects.*;

public class Handler {

    Service service;

    public Handler(){
        service = new Service();
    }

    public void handleRegister(Context ctx) throws ResponseException{
        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
        nullDataCheck(userData.username(), userData.password(), userData.email());
        AuthData authData = service.register(userData);
        ctx.json(new Gson().toJson(authData));
    }

    public void handleLogin(Context ctx) throws ResponseException{
        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
        nullDataCheck(userData.username(), userData.password());
        AuthData authData = service.login(userData);
        ctx.json(new Gson().toJson(authData));
    }

    public void handleLogout(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");
        service.logout(authToken);
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
