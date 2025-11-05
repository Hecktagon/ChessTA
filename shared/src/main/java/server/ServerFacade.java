package server;

import com.google.gson.Gson;
import errors.ResponseException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    // a client side HTTP object for handling HTTP requests/responses.
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    // make a serveFacade with a url, so that we know where to send HTTP requests.
    public ServerFacade(String url) {
        serverUrl = url;
    }

    // make an HTTP request out of a method, endpoint path, and some java object as a body.
    private HttpRequest buildRequest(String method, String path, Object body) {
        Builder request = HttpRequest.newBuilder()
                // makes the full uri, with web address and endpoint path
                .uri(URI.create(serverUrl + path))
                // sets up the request with the appropriate method and body
                .method(method, makeRequestBody(body));

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

    // attempts to send an HTTP request
    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (java.io.IOException | java.lang.InterruptedException e) {
            throw new ResponseException(ResponseException.Type.SERVER_ERROR);
        }
    }

    // TODO: Fix this to work with my code:
    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

}
