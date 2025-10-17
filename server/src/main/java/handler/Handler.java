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
        if(userData.username() == null || userData.password() == null || userData.email() == null){
            throw new ResponseException(ResponseException.Type.BAD_REQUEST);
        }

        AuthData authData = service.register(userData);
        ctx.json(new Gson().toJson(authData));
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
}
