package IS24_LB11.cli.event;

import com.googlecode.lanterna.input.KeyStroke;

public class KeyboardEvent implements Event {
    KeyStroke keyStroke;

    public KeyboardEvent(KeyStroke keyStroke) {
        this.keyStroke = keyStroke;
    }

    public KeyStroke getKeyStroke() {
        return keyStroke;
    }
}