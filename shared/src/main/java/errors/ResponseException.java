package errors;

// a class for handling 400 errors

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
        DATA_ACCESS_ERROR;
    }


    private String typeToMessage() {
        return switch (type){
            case UNAUTHORIZED -> "Unauthorized";
            case BAD_REQUEST -> "Bad request";
            case ALREADY_TAKEN -> "Already taken";
            case DATA_ACCESS_ERROR -> "Data access error";
        };
    }


    public Type getType() {
        return type;
    }
}
