package IS24_LB11.cli.popup;

import IS24_LB11.cli.KeyConsumer;
import IS24_LB11.cli.view.ViewHub;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.PriorityQueue;

public class PopUpQueue implements KeyConsumer {
    private final PriorityQueue<PopUp> queue;
    private final ViewHub viewHub;
    private int priority;

    public PopUpQueue(ViewHub viewHub, int priority) {
        this.queue = new PriorityQueue<>((p1, p2) -> Integer.compare(p1.priority(), p2.priority()));
        this.viewHub = viewHub;
        this.priority = priority;
    }

    @Override
    public boolean consumeKeyStroke(KeyStroke keyStroke) {
        if (queue.isEmpty()) return false;
        boolean keyConsumed = queue.peek().consumeKeyStroke(keyStroke);
        if (queue.peek().isClosed()) {
            queue.poll();
            setNextPopUp();
        }
        return keyConsumed;
    }

    public void addPopUp(PopUp popUp) {
        queue.add(popUp);
        setNextPopUp();
    }

    public void addUrgentPopUp(String title, String text) {
        if (queue.isEmpty())
            queue.add(new PopUp(KeyConsumer.MAX_PRIORITY, title, text));
        else
            queue.add(new PopUp(queue.peek().priority()-1, title, text));
        setNextPopUp();
    }

    private void setNextPopUp() {
        if (queue.size() > 0) {
            PopUp popUp = queue.peek();
            if (popUp.getTitle().isEmpty())
                viewHub.addPopUp(popUp.getText(), String.format("[%d]", queue.size()));
            else
                viewHub.addPopUp(popUp.getText(), String.format("[%d-%s]", queue.size(), popUp.getTitle()));
        } else viewHub.removePopUp();
        viewHub.update();
    }

    public int priority() {
        return priority;
    }
}
