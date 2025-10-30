package dataaccess;

import dataobjects.AuthData;
import dataobjects.GameData;
import errors.ResponseException;
import java.sql.*;

public class SQLAuth implements AuthDAO{

    public SQLAuth() throws ResponseException{
        String createStatement = """
                CREATE TABLE IF NOT EXISTS  auth (
                  `authToken` varchar(256) NOT NULL,
                  `username` varchar(256) NOT NULL,
                  PRIMARY KEY (`authToken`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                """;
        DatabaseManager.configureDatabase(createStatement);
    }

    @Override
    public void createAuth(AuthData auth) throws ResponseException {
        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        DatabaseManager.executeUpdate(statement, auth.authToken(), auth.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws ResponseException {
        String statement = "SELECT authToken, username FROM auth WHERE authToken=?";
        return executeSelect(statement, authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws ResponseException {
        String statement = "DELETE FROM auth WHERE authToken=?";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public void clearAuths() throws ResponseException {
        String statement = "TRUNCATE auth";
        DatabaseManager.executeUpdate(statement);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        return new AuthData(rs.getString("authToken"), rs.getString("username"));
    }

    private AuthData executeSelect(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++){
                    // for each of our params, we set the ?'s to those params
                    preparedStatement.setObject(i + 1, params[i]);
                }
                ResultSet rs = preparedStatement.executeQuery();
                if(rs.next()){
                    return readAuth(rs);
                }
                return null;
            }
        }catch (DataAccessException | SQLException dataEx) {
            throw new ResponseException(ResponseException.Type.DATA_ACCESS_ERROR);
        }
    }
}
