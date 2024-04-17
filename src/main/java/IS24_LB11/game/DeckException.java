package IS24_LB11.game;

/**
 * the EmptyDeckException shows that there aren't any cards left
 */
//public class EmptyDeckException extends Exception {
//    public EmptyDeckException(String message) {
//        super (message);
//    }
//}
public class DeckException extends Exception {
    private String message;
    private String context;

    public DeckException() { super(); }

    public DeckException(String msg) {
        message = msg;
        context = "";
    }

    public DeckException(String msg, String context) {
        this.message = msg;
        this.context = context;
    }

    public DeckException addContext(String context) {
        this.context += " " + context;
        return this;
    }

    public String getMessage() {
        return "[Syntax error] " + message + " " + context;
    }
}