package IS24_LB11.game.utils;

public class JsonStringException extends Exception {
    private String message;
    private String context;

    public JsonStringException() { super(); }

    public JsonStringException(String msg) {
        message = msg;
        context = "";
    }

    public JsonStringException(String msg, String context) {
        this.message = msg;
        this.context = context;
    }

    public JsonStringException addContext(String context) {
        this.context += " " + context;
        return this;
    }

    public String getMessage() {
        return "[Syntax error] " + message + " " + context;
    }
}
