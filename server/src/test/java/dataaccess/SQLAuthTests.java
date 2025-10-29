package dataaccess;

import dataobjects.AuthData;
import errors.ResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SQLAuthTests {

    static AuthDAO sqlAuth;

    @BeforeAll
    public static void prep(){
        sqlAuth = new SQLAuth();
    }

    @Test
    @DisplayName("Create Auth Success")
    public void createAuthSuccess() throws ResponseException {
        AuthData inputData = new AuthData("verycoolauthtoken", "username");
        AuthData authData = sqlAuth.createAuth(new AuthData("verycoolauthtoken", "username"));
        Assertions.assertEquals(inputData, authData);
    }
}
