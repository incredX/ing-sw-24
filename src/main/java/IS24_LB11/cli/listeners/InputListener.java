package IS24_LB11.cli.listeners;

import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.event.KeyboardEvent;
import IS24_LB11.cli.event.MessageEvent;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class InputListener extends Listener implements Runnable {
    private final Terminal terminal;

    public InputListener(ClientState state) {
        super(state);
        this.terminal = state.getTerminal();
    }

    public void run() {
        while (true) {
            try {
                KeyStroke keyStroke = terminal.readInput();
                if (keyStroke.getKeyType() == KeyType.EOF) break;
                state.queueEvent(new KeyboardEvent(keyStroke));
            } catch (IOException e) {
                state.tryQueueEvent(new MessageEvent(e.getMessage()));
            } catch (InterruptedException e) {
                System.out.println("Thread of InputListener interrupted.");
                break;
            }
        }
    }
}