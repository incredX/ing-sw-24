package IS24_LB11.cli.listeners;

import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.event.ResizeEvent;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.SimpleTerminalResizeListener;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class ResizeListener extends Listener implements Runnable {
    private final SimpleTerminalResizeListener listener;

    public ResizeListener(ClientState state) throws IOException {
        super(state);
        Terminal terminal = state.getTerminal();
        this.listener = new SimpleTerminalResizeListener(terminal.getTerminalSize());
        terminal.addResizeListener(this.listener);
    }

    @Override
    public void run() {
        int millis = 25;
        int minMillisBetweenResizes = millis * 8;
        int counter = 0;

        while (true) {
            if (listener.isTerminalResized()) {
                if (counter > minMillisBetweenResizes) {
                    counter = 0;
                    TerminalSize size = listener.getLastKnownSize();
                    try {
                        state.queueEvent(new ResizeEvent(size));
                    } catch (InterruptedException e) { break; }
                }
            } else {
                try { synchronized (this) { this.wait(millis); } }
                catch (InterruptedException e) { break; }
                counter += millis;
            }
        }
    }
}
