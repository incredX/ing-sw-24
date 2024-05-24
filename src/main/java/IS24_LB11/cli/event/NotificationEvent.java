package IS24_LB11.cli.event;

import IS24_LB11.cli.notification.Priority;

import static IS24_LB11.cli.notification.Priority.HIGH;
import static IS24_LB11.cli.notification.Priority.LOW;

public class NotificationEvent implements Event {
    private final String title, message;
    private final Priority priority;

    public static NotificationEvent urgent(String title, String message) {
        return new NotificationEvent(HIGH, "", message);
    }

    public static NotificationEvent error(String message) {
        return urgent("ERROR", message);
    }

    public NotificationEvent(Priority priority, String title, String message) {
        this.priority = priority;
        this.title = title;
        this.message = message;
    }

    public NotificationEvent(String title, String message) {
        this(LOW, title, message);
    }

    public NotificationEvent(String message) {
        this(LOW, "", message);
    }

    public Priority getPriority() {
        return priority;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
