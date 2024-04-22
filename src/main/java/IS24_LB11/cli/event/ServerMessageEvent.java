package IS24_LB11.cli.event;

public class ServerMessageEvent implements ServerEvent {
    private final String message;
    private final String from;
    private final String to;

    public ServerMessageEvent(String message, String from, String to) {
        this.message = message;
        this.from = from;
        this.to = to;
    }

    public ServerMessageEvent(String message, String from) {
        this(message, from, "");
    }

    public String message() {
        return message;
    }
    public String from() {
        return from;
    }
    public String to() { return to; }
}
