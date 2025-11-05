package service;

import dataobjects.AuthData;
import errors.ResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class VeryCoolTest {
    @Test
    @DisplayName("Very Cool Test")
    public void veryCoolTest() throws ResponseException {
        Assertions.assertTrue(true);
    }
}
