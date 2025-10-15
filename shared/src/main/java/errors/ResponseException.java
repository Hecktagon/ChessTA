package errors;

// a class for handling 400 errors

public class ResponseException extends Exception {
    Type type = Type.DATA_ACCESS_ERROR;
    String message;

    public ResponseException() {
        message = typeToMessage();
    }

    public ResponseException(Type exceptionType) {
        message = typeToMessage();
        type = exceptionType;
    }

    public enum Type {
        UNAUTHORIZED,
        BAD_REQUEST,
        ALREADY_TAKEN,
        DATA_ACCESS_ERROR;
    }

    public String typeToMessage() {
        return switch (type){
            case UNAUTHORIZED -> "Unauthorized";
            case BAD_REQUEST -> "Bad request";
            case ALREADY_TAKEN -> "Already taken";
            case DATA_ACCESS_ERROR -> "Data access error";
        };
    }
}
