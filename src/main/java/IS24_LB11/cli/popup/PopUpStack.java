package IS24_LB11.cli.popup;

import IS24_LB11.cli.KeyConsumer;
import IS24_LB11.cli.ViewHub;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.HashMap;

import static IS24_LB11.cli.popup.Priority.HIGH;

public class PopUpStack implements KeyConsumer {
    private final HashMap<Priority, ArrayList<PopUp>>  popUps;
    private final ViewHub viewHub;
    private int priority;
    private int numPopUps;

    public PopUpStack(ViewHub viewHub, int priority) {
        this.popUps = new HashMap<>();
        this.viewHub = viewHub;
        this.priority = priority;
        this.numPopUps = 0;
        for (Priority p: Priority.values()) popUps.put(p, new ArrayList<>());
    }

    @Override
    public boolean consumeKeyStroke(KeyStroke keyStroke) {
        if (isEmpty()) return false;
        PopUp popup = topPopUp();
        boolean keyConsumed = popup.consumeKeyStroke(keyStroke);
        if (popup.isClosed()) {
            removeTopPopUp();
            setNextPopUp();
        }
        return keyConsumed;
    }

    public void addPopUp(Priority priority, String title, String text) {
        popUps.get(priority).add(new PopUp(0, title, text));
        numPopUps++;
        setNextPopUp();
    }

    public void addPopUp(Priority priority, String text) {
        popUps.get(priority).add(new PopUp(0, text));
        numPopUps++;
        setNextPopUp();
    }

    public void addUrgentPopUp(String title, String text) {
        addPopUp(HIGH, title, text);
    }

    private void setNextPopUp() {
        if (!isEmpty()) {
            PopUp popUp = topPopUp();
            if (popUp.getTitle().isEmpty())
                viewHub.addPopUp(popUp.getText(), String.format(" [%d]", numPopUps));
            else
                viewHub.addPopUp(popUp.getText(), String.format("[%d] %s", numPopUps, popUp.getTitle()));
        } else viewHub.removePopUp();
        viewHub.update();
    }

    private PopUp topPopUp() {
        for (Priority p: Priority.values()) {
            if (popUps.get(p).isEmpty()) continue;
            return popUps.get(p).getLast();
        }
        return null;
    }

    private void removeTopPopUp() {
        for (Priority p: Priority.values()) {
            if (!popUps.get(p).isEmpty()) {
                popUps.get(p).removeLast();
                numPopUps--;
                break;
            }
        }
    }

    private boolean isEmpty() {
        return popUps.values().stream().allMatch(ArrayList::isEmpty);
    }

    public int priority() {
        return priority;
    }
}
