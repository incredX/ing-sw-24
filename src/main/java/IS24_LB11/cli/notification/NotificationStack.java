package IS24_LB11.cli.notification;

import IS24_LB11.cli.KeyConsumer;
import IS24_LB11.cli.ViewHub;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.HashMap;

import static IS24_LB11.cli.notification.Priority.HIGH;

public class NotificationStack implements KeyConsumer {
    private final HashMap<Priority, ArrayList<Notification>> notifications;
    private final ViewHub viewHub;
    private int priority;
    private int numNotifications;

    public NotificationStack(ViewHub viewHub, int priority) {
        this.notifications = new HashMap<>();
        this.viewHub = viewHub;
        this.priority = priority;
        this.numNotifications = 0;
        for (Priority p: Priority.values()) notifications.put(p, new ArrayList<>());
    }

    @Override
    public boolean consumeKeyStroke(KeyStroke keyStroke) {
        if (isEmpty()) return false;
        Notification notification = topNotification();
        boolean keyConsumed = notification.consumeKeyStroke(keyStroke);
        if (notification.isClosed()) {
            removeTopNotification();
            setNextNotification();
        }
        return keyConsumed;
    }

    public void add(Priority priority, String title, String text) {
        notifications.get(priority).add(new Notification(0, title, text));
        numNotifications++;
        setNextNotification();
    }

    public void add(Priority priority, String text) {
        notifications.get(priority).add(new Notification(0, text));
        numNotifications++;
        setNextNotification();
    }

    public void addUrgent(String title, String text) {
        add(HIGH, title, text);
    }

    private void setNextNotification() {
        if (!isEmpty()) {
            Notification notification = topNotification();
            if (notification.getTitle().isEmpty())
                viewHub.addNotification(notification.getText(), String.format(" [%d]", numNotifications));
            else
                viewHub.addNotification(notification.getText(), String.format(" [%d] %s", numNotifications, notification.getTitle()));
        } else viewHub.removeNotification();
    }

    private Notification topNotification() {
        for (Priority p: Priority.values()) {
            if (notifications.get(p).isEmpty()) continue;
            return notifications.get(p).getLast();
        }
        return null;
    }

    private void removeTopNotification() {
        for (Priority p: Priority.values()) {
            if (!notifications.get(p).isEmpty()) {
                notifications.get(p).removeLast();
                numNotifications--;
                break;
            }
        }
    }

    public void removeNotifications(Priority priority) {
        numNotifications -= notifications.get(priority).size();
        notifications.get(priority).clear();
        setNextNotification();
    }

    public void removeAllNotifications() {
        for (Priority p: Priority.values())
            notifications.get(p).clear();
        viewHub.removeNotification();
    }

    private boolean isEmpty() {
        return notifications.values().stream().allMatch(ArrayList::isEmpty);
    }

    public int priority() {
        return priority;
    }
}
