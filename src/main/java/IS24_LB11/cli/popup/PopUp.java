package IS24_LB11.cli.popup;

import IS24_LB11.cli.KeyConsumer;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class PopUp implements KeyConsumer {
    private int priority;
    private final String title;
    private final String text;
    private boolean closed;

    public PopUp(int priority, String title, String text) {
        this.priority = priority;
        this.title = title;
        this.text = text;
        this.closed = false;
    }

    public PopUp(int priority, String text) {
        this(priority, "", text);
    }

    @Override
    public boolean consumeKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Escape) {
            closed = true;
        }
        return closed;
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public int priority() {
        return priority;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
