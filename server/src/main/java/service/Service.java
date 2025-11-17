package service;

import chess.ChessGame;
import dataaccess.*;
import dataobjects.*;
import errors.ResponseException;
import org.mindrot.jbcrypt.BCrypt;
import dataobjects.JoinGameRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class Service {
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;
    HashSet<Integer> gameIDs;

    public Service(){
        // try to open SQL database, if fails, open local storage.
        try {
            DatabaseManager.createDatabase();
            authDAO = new SQLAuth();
            userDAO = new SQLUser();
            gameDAO = new SQLGame();
        } catch(DataAccessException | ResponseException e){
            System.out.printf("""
                    
                    ### WARNING ###
                    SQL Server failed:
                    Error:
                    %s
                    
                    Switching to local storage...
                    
                    """, e);
            authDAO = new LocalAuth();
            userDAO = new LocalUser();
            gameDAO = new LocalGame();
        }

        gameIDs = new HashSet<>();
    }

//  ### PUBLIC SERVICE METHODS ###
    public AuthData register(UserData registerRequest) throws ResponseException {
        boolean userTaken = userDAO.getUser(registerRequest.username()) != null;
        if (userTaken){
            throw new ResponseException(ResponseException.Type.ALREADY_TAKEN);
        }

        userDAO.createUser(new UserData(registerRequest.username(),
                passHash(registerRequest.password()), registerRequest.email()));

        AuthData authData = new AuthData(generateToken(), registerRequest.username());
        authDAO.createAuth(authData);

        return authData;
    }

    public AuthData login(UserData loginRequest) throws ResponseException {
        UserData userData = userDAO.getUser(loginRequest.username());
        if (userData == null || !BCrypt.checkpw(loginRequest.password(), userData.password())){
            throw new ResponseException(ResponseException.Type.UNAUTHORIZED);
        }

        AuthData authData = new AuthData(generateToken(), loginRequest.username());
        authDAO.createAuth(authData);

        return authData;
    }

    public void logout(String authToken) throws ResponseException {
        checkAuth(authToken);
        authDAO.deleteAuth(authToken);
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException {
        AuthData authData = checkAuth(authToken);
        Collection<GameData> gameList = gameDAO.readGames();
        for(GameData game : gameList){
            gameIDs.add(game.gameID());
        }
        return gameList;
    }

    public GameData createGame(String authToken, String gameName) throws ResponseException {
        AuthData authData = checkAuth(authToken);
        listGames(authToken); // populate gameIDs to prevent duplicate IDs
        GameData gameData = new GameData(generateGameID(), null, null, gameName, new ChessGame());
        gameDAO.createGame(gameData);
        return new GameData(gameData.gameID(), null, null, null, null);
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws ResponseException {
        AuthData authData = checkAuth(authToken);
        GameData gameData = gameDAO.getGame(joinGameRequest.gameID());

        if(gameData == null){
            throw new ResponseException(ResponseException.Type.UNAUTHORIZED);
        }

        GameData updatedGameData;
        // Joining as BLACK:
        if (joinGameRequest.playerColor().equals(ChessGame.TeamColor.BLACK)){
            if(gameData.blackUsername() != null){
                throw new ResponseException(ResponseException.Type.ALREADY_TAKEN);
            }
            updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(),
                    gameData.gameName(), gameData.game());
        }
        // Joining as WHITE:
        else {
            if(gameData.whiteUsername() != null){
                throw new ResponseException(ResponseException.Type.ALREADY_TAKEN);
            }
            updatedGameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(),
                    gameData.gameName(), gameData.game());
        }

        gameDAO.updateGame(updatedGameData);
    }

    public void clearAll() throws ResponseException {
        authDAO.clearAuths();
        gameDAO.clearGames();
        userDAO.clearUsers();
    }

//  ### PRIVATE HELPER METHODS ###
    private AuthData checkAuth(String authToken) throws ResponseException {
        AuthData authData = authDAO.getAuth(authToken);
        if ((authData) == null){
            throw new ResponseException(ResponseException.Type.UNAUTHORIZED);
        }
        return authData;
    }

    private Integer generateGameID(){
        int i = 0;
        while(true){
            i++;
            if(!gameIDs.contains(i)) {
                gameIDs.add(i);
                return i;
            }
        }
    }

    private String generateToken(){
        return UUID.randomUUID().toString();
    }

    private String passHash(String password) {return BCrypt.hashpw(password, BCrypt.gensalt());}
}
