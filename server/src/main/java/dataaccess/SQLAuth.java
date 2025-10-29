package dataaccess;

import dataobjects.AuthData;
import errors.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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


    // params is the queries to be inserted into the statement
    private AuthData executeStatement(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++){
                    // for each of our params, we set the ?'s to those params
                    preparedStatement.setObject(i + 1, params[i]);
                }
                // execute the statement with queries now in place
                ResultSet rs = preparedStatement.executeQuery();
                // get the first table row from the result from the query
                if(rs.next()){
                    // if query returned a row, return authData made from row
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
                // else, return null
                return null;
            }
        } catch (DataAccessException | SQLException dataEx) {
            throw new ResponseException(ResponseException.Type.DATA_ACCESS_ERROR);
        }
    }
}
