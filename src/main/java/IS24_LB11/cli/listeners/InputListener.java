package IS24_LB11.cli.listeners;

import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.event.KeyboardEvent;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

public class InputListener extends Listener implements Runnable {
    private final Screen screen;

    public InputListener(ClientState state) {
        super(state);
        this.screen = state.getScreen();
    }

    public void run() {
        Thread.currentThread().setName("input-listener");
        while (true) {
            try {
                KeyStroke keyStroke = screen.readInput();
                if (keyStroke.getKeyType() == KeyType.EOF) break;
                state.queueEvent(new KeyboardEvent(keyStroke));
            } catch (IOException | InterruptedException e) {
                Debugger.print(e);
                break;
            }
        }
        Debugger.print("thread terminated.");
    }

    public void shutdown() {
        try { System.in.close(); } // <- to close gracefuly the input listener
        catch (IOException e) { Debugger.print(e); }
    }
}
