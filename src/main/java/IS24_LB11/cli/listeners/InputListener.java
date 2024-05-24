package IS24_LB11.cli.listeners;

import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.event.KeyboardEvent;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * InputListener is a class that listens for keyboard input events and handles them accordingly.
 * It extends the Listener class and implements the Runnable interface to run in a separate thread.
 */
public class InputListener extends Listener implements Runnable {
    private final Screen screen;

    /**
     * Constructs an InputListener with the given client state.
     *
     * @param state the client state which holds the screen and event queue
     */
    public InputListener(ClientState state) {
        super(state);
        this.screen = state.getScreen();
    }

    /**
     * The run method that continuously listens for keyboard input events.
     * It sets the thread name to "input-listener" and processes input events until an EOF key event is received
     * or an exception occurs.
     */
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
        if (state != null) state.shutdown();
        Debugger.print("thread terminated.");
    }

    /**
     * Shuts down the input listener by closing the System.in stream to stop input reading gracefully.
     */
    public void shutdown() {
        try {
            System.in.close();
        } catch (IOException e) {
            Debugger.print(e);
        }
    }
}
