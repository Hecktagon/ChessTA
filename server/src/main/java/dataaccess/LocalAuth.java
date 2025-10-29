package dataaccess;

import dataobjects.AuthData;
import errors.ResponseException;

import java.util.HashMap;

public class LocalAuth implements AuthDAO{
    private final HashMap<String, AuthData> authTable = new HashMap<>();

    @Override
    public void createAuth(AuthData auth) throws ResponseException {
        authTable.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws ResponseException {
        if (authTable.containsKey(authToken)){
           return authTable.get(authToken);
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {
        authTable.remove(authToken);
    }

    @Override
    public void clearAuths() throws ResponseException {
        authTable.clear();
    }
}
