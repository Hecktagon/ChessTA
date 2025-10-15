package dataaccess;

import dataobjects.GameData;
import dataobjects.UserData;
import errors.ResponseException;

import java.util.HashMap;

public class LocalUser implements UserDAO{
    private final HashMap<String, UserData> userTable = new HashMap<>();

    @Override
    public UserData createUser(UserData user) throws ResponseException {
        userTable.put(user.username(), user);
        return user;
    }

    @Override
    public UserData getUser(String userName) throws ResponseException {
        if(userTable.containsKey(userName)){
            return userTable.get(userName);
        }
        return null;
    }

    @Override
    public void clearUsers() throws ResponseException {
        userTable.clear();
    }
}
