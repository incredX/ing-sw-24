package IS24_LB11.cli.event;

public class ServerOkEvent implements ServerEvent {
    private final String message;

    public ServerOkEvent() {
        this.message = "";
    }

    public ServerOkEvent(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
