package IS24_LB11.cli.event;

public class ServerMessageEvent implements ServerEvent {
    private final String message;
    private final String from;

    public ServerMessageEvent(String message, String from) {
        this.message = message;
        this.from = from;
    }

    public String message() {
        return message;
    }
    public String from() {
        return from;
    }
}
