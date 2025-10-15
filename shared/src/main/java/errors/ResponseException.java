package errors;

// a class for handling 400 errors

public class ResponseException extends Exception {
    Type type = Type.DATA_ACCESS_ERROR;

    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(String message, Type exceptionType) {
        super(message);
        type = exceptionType;
    }

    public enum Type {
        UNAUTHORIZED,
        BAD_REQUEST,
        ALREADY_TAKEN,
        DATA_ACCESS_ERROR
    }
}
