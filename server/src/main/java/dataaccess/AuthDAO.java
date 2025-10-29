package dataaccess;


import dataobjects.AuthData;
import errors.ResponseException;

public interface AuthDAO {
    void createAuth(AuthData auth) throws ResponseException;

    AuthData getAuth(String authToken) throws ResponseException;

    void deleteAuth(String authToken) throws ResponseException;

    void clearAuths() throws ResponseException;
}
