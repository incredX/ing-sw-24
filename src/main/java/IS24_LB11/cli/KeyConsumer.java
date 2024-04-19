package IS24_LB11.cli;

import com.googlecode.lanterna.input.KeyStroke;

public interface KeyConsumer {
    int MIN_PRIORITY = 0;
    int MAX_PRIORITY = 31;
    boolean consumeKeyStroke(KeyStroke keyStroke);
    int priority();
}