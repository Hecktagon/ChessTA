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
        AuthData authData = service.register(userData);
        ctx.json(new Gson().toJson(authData));
    }
}
