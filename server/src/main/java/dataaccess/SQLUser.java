package dataaccess;

import dataobjects.AuthData;
import dataobjects.UserData;
import errors.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUser implements UserDAO {

    public SQLUser() throws ResponseException {
        String createStatement =
                """
                        CREATE TABLE IF NOT EXISTS user (
                          `username` varchar(256) NOT NULL,
                          `password` varchar(256) NOT NULL,
                          `email` varchar(256) NOT NULL,
                          PRIMARY KEY (`username`)
                        )
                        """;
        DatabaseManager.configureDatabase(createStatement);
    }

    @Override
    public UserData createUser(UserData user) throws ResponseException {
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        DatabaseManager.executeUpdate(statement, user.username(), user.password(), user.email());
        return user;
    }

    @Override
    public UserData getUser(String userName) throws ResponseException {
        String statement = "SELECT username, password, email FROM user WHERE username=?";
        return executeSelect(statement, userName);
    }

    @Override
    public void clearUsers() throws ResponseException {
        String statement = "TRUNCATE user";
        DatabaseManager.executeUpdate(statement);
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
