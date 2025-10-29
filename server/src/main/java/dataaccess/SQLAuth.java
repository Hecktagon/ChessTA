package dataaccess;

import dataobjects.AuthData;
import errors.ResponseException;

import java.sql.SQLException;

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

    public void executeStatement(String statement) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                var rs = preparedStatement.executeQuery();
                // get the first table row from the result from the query
                rs.next();
                System.out.println(rs.getInt(1));
            }
        } catch (DataAccessException | SQLException dataEx) {
            throw new ResponseException(ResponseException.Type.DATA_ACCESS_ERROR);
        }
    }
}
