package IS24_LB11.cli.event;

public class MessageEvent implements Event {
    private String message;

    public MessageEvent(String message) {
        this.message = message;
    }
}
