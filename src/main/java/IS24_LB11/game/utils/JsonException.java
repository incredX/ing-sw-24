package IS24_LB11.game.utils;

public class JsonException extends Exception {
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
