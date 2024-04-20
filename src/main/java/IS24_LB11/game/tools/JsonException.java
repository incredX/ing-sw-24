package IS24_LB11.game.tools;

public class JsonException extends Exception {

    public static final String INVALID_INPUT = "Json input not valid";
    public static final String PLACEDCARDS_NOT_FOUND = "Placed Card not found";
    private String message;
    private String context;

    public JsonException() { super(); }

    public JsonException(String msg) {
        message = msg;
        context = "";
    }

    public JsonException(String msg, String context) {
        this.message = msg;
        this.context = context;
    }

    public JsonException addContext(String context) {
        this.context += " " + context;
        return this;
    }

    public String getMessage() {
        return "[Syntax error] " + message + " " + context;
    }
}
