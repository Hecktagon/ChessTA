package server;

import com.google.gson.Gson;
import dataobjects.*;
import errors.ResponseException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collection;

public class ServerFacade {
    // a client side HTTP object for handling HTTP requests/responses.
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    // make a serveFacade with a url, so that we know where to send HTTP requests.
    public ServerFacade(String url) {
        serverUrl = url;
    }


    public void clear() throws ResponseException{
        HttpRequest request = buildRequest("DELETE", "/db", null, null);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }


    public AuthData register(UserData user) throws ResponseException {
       HttpRequest request = buildRequest("POST", "/user", user, null);
       HttpResponse<String> response = sendRequest(request);
       return handleResponse(response, AuthData.class);
    }


    public AuthData login(UserData user) throws ResponseException{
        HttpRequest request = buildRequest("POST", "/session", user, null);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }


    public void logout(String authToken) throws ResponseException {
        HttpRequest request = buildRequest("DELETE", "/session", null, authToken);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException {
        HttpRequest request = buildRequest("GET", "/game", null, authToken);
        HttpResponse<String> response = sendRequest(request);
        ListGamesResponse gameList = handleResponse(response, ListGamesResponse.class);
        if (gameList != null){
            return gameList.games();
        }
        return null;
    }

    public Integer createGame(String authToken, String gameName) throws ResponseException {
        GameData createGameRequest = new GameData(null, null, null, gameName, null);
        HttpRequest request = buildRequest("POST", "/game", createGameRequest, authToken);
        HttpResponse<String> response = sendRequest(request);
        GameData createGameResponse = handleResponse(response, GameData.class);
        if (createGameResponse != null){
            return createGameRequest.gameID();
        }
        return null;
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws ResponseException {
        HttpRequest request = buildRequest("PUT", "/game", joinGameRequest, authToken);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    // make an HTTP request out of a method, endpoint path, and some java object as a body.
    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        Builder request = HttpRequest.newBuilder()
                // makes the full uri, with web address and endpoint path
                .uri(URI.create(serverUrl + path))
                // sets up the request with the appropriate method and body
                .method(method, makeRequestBody(body));

        // add auth header if needed.
        if(authToken != null){
                    request.header("Authorization", authToken);
        }

        // lets the server know that the body is of type JSON by setting the content type header.
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }

        // returns the completed request as a HttpRequest object.
        return request.build();
    }


    // helper function to ensure correct formatting and serialization of request body objects to JSON.
    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }


    // attempts to send an HTTP request, returns an HttpResponse with a String body.
    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (java.io.IOException | java.lang.InterruptedException e) {
            throw new ResponseException(ResponseException.Type.SERVER_ERROR);
        }

    }

    // given a response, coverts it to either an error or whatever object type you expect to get in the response.
    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();

        // if response is an error:
        if ((status / 100) != 2) {
            throw new ResponseException(httpCodeToException(status));
        }

        // if you have a dataType you want to deserialize into:
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        // else, if response body is not needed:
        return null;
    }


    private ResponseException.Type httpCodeToException(int code){
        return switch(code){
            case 401 -> ResponseException.Type.UNAUTHORIZED;
            case 400 -> ResponseException.Type.BAD_REQUEST;
            case 403 -> ResponseException.Type.ALREADY_TAKEN;
            default -> ResponseException.Type.DATA_ACCESS_ERROR;
        };
    }

}
