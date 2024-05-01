package IS24_LB11.cli.listeners;

import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.event.ResizeEvent;
import com.googlecode.lanterna.TerminalSize;

import java.io.IOException;

public class ResizeListener extends Listener implements Runnable {
    private final ViewHub viewHub;

    public ResizeListener(ClientState state) throws IOException {
        super(state);
        viewHub = state.getViewHub();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("resize-listener");
        while (true) {
            TerminalSize newSize = viewHub.screenSizeChanged();
            try {
                synchronized (this) {
                    if (newSize == null) { this.wait(100); }
                    else {
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
