package dataaccess;

import dataobjects.UserData;
import errors.ResponseException;

public class SQLUser implements UserDAO {
    private final String createStatement =
        """
        CREATE TABLE IF NOT EXISTS  user (
          `username` varchar(256) NOT NULL,
          `password` varchar(256) NOT NULL,
          `email` varchar(256) NOT NULL,
          PRIMARY KEY (`username`),
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """;

    @Override
    public UserData createUser(UserData user) throws ResponseException {
        return null;
    }

    @Override
    public UserData getUser(String userName) throws ResponseException {
        return null;
    }

    @Override
    public void clearUsers() throws ResponseException {

    }
}
