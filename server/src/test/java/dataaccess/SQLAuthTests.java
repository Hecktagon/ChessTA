package dataaccess;

import dataobjects.AuthData;
import errors.ResponseException;
import org.junit.jupiter.api.*;

public class SQLAuthTests {

    static AuthDAO sqlAuth;

    @BeforeAll
    public static void prep() throws ResponseException {
        sqlAuth = new SQLAuth();
    }

    @BeforeEach
    public void clearAuthTable() throws ResponseException {
        sqlAuth.clearAuths();
    }

    @Test
    @DisplayName("Create Auth Success")
    public void createAuthSuccess() throws ResponseException {
        AuthData inputData = new AuthData("verycoolauthtoken", "username");
        sqlAuth.createAuth(new AuthData("verycoolauthtoken", "username"));
        AuthData authData = sqlAuth.getAuth(inputData.authToken());
        Assertions.assertEquals(inputData, authData);
    }
}
