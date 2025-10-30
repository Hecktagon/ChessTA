package dataaccess;

import dataobjects.AuthData;
import dataobjects.UserData;
import errors.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    private UserData readUser(ResultSet rs) throws SQLException {
        return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
    }

    private UserData executeSelect(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++){
                    // for each of our params, we set the ?'s to those params
                    preparedStatement.setObject(i + 1, params[i]);
                }
                ResultSet rs = preparedStatement.executeQuery();
                if(rs.next()){
                    return readUser(rs);
                }
                return null;
            }
        }catch (DataAccessException | SQLException dataEx) {
            throw new ResponseException(ResponseException.Type.DATA_ACCESS_ERROR);
        }
    }
}
