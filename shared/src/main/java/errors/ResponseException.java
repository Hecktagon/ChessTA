package errors;

// a class for handling 400 errors

import com.google.gson.Gson;

import java.util.Map;

public class ResponseException extends Exception {
    private Type type = Type.DATA_ACCESS_ERROR;
    private final String message;

    public ResponseException() {
        message = typeToMessage();
    }

    public ResponseException(Type exceptionType) {
        type = exceptionType;
        message = typeToMessage();
    }


    public enum Type {
        UNAUTHORIZED,
        BAD_REQUEST,
        ALREADY_TAKEN,
        DATA_ACCESS_ERROR,
        SERVER_ERROR;
    }


    private String typeToMessage() {
        return switch (type){
            case UNAUTHORIZED -> "Error: Unauthorized";
            case BAD_REQUEST -> "Error: Bad request";
            case ALREADY_TAKEN -> "Error: Already taken";
            case DATA_ACCESS_ERROR -> "Error: Data access error";
            case SERVER_ERROR -> "Error: Server Error";
        };
    }

    public String toJson(int status){
        return new Gson().toJson(Map.of("message", message, "status", status));
    }

    public Type getType() {
        return type;
    }
}
