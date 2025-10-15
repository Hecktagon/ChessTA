package dataaccess;

import dataobjects.UserData;
import errors.ResponseException;

public interface UserDAO {
    UserData createUser(UserData user) throws ResponseException;

    UserData getUser(String userName) throws ResponseException;

    void clearUsers() throws ResponseException;
}