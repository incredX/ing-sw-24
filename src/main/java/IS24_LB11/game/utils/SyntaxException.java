package IS24_LB11.game.utils;

public class SyntaxException extends Exception {
    private String message;
    private String context;

    public SyntaxException() { super(); }

    public SyntaxException(String msg) {
        message = msg;
        context = "";
    }

    public SyntaxException(String msg, String context) {
        this.message = msg;
        this.context = context;
    }

    public SyntaxException addContext(String context) {
        this.context += " " + context;
        return this;
    }

    public String getMessage() {
        return "[Syntax error] " + message + " " + context;
    }
}
