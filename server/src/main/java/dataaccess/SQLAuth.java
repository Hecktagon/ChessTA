package dataaccess;

import dataobjects.AuthData;
import errors.ResponseException;

public class SQLAuth implements AuthDAO{
    private final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """;

    @Override
    public AuthData createAuth(AuthData auth) throws ResponseException {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws ResponseException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {

    }

    @Override
    public void clearAuths() throws ResponseException {

    }
}
