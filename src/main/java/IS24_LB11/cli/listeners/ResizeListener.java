package IS24_LB11.cli.listeners;

import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.event.ResizeEvent;
import com.googlecode.lanterna.TerminalSize;

import java.io.IOException;

/**
 * ResizeListener is a class that listens for screen resize events and handles them accordingly.
 * It extends the Listener class and implements the Runnable interface to run in a separate thread.
 */
public class ResizeListener extends Listener implements Runnable {
    private final ViewHub viewHub;

    /**
     * Constructs a ResizeListener with the given client state.
     *
     * @param state the client state which holds the view hub and event queue
     * @throws IOException if an I/O error occurs
     */
    public ResizeListener(ClientState state) throws IOException {
        super(state);
        this.viewHub = state.getViewHub();
    }

    /**
     * The run method that continuously listens for screen resize events.
     * It sets the thread name to "resize-listener" and processes resize events when the screen size changes.
     */
    @Override
    public void run() {
        Thread.currentThread().setName("resize-listener");
        while (true) {
            TerminalSize newSize = viewHub.screenSizeChanged();
            try {
                synchronized (this) {
                    if (newSize == null) {
                        this.wait(100);
                    } else {
                        state.queueEvent(new ResizeEvent(newSize));
                        this.wait(50);
                    }
                }
            } catch (InterruptedException e) {
                Debugger.print(e);
                break;
            }
        }
        Debugger.print("thread terminated.");
    }
}
