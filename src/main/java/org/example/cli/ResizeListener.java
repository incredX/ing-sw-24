package org.example.cli;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.SimpleTerminalResizeListener;
import com.googlecode.lanterna.terminal.Terminal;
import org.example.cli.event.Event;
import org.example.cli.event.ResizeEvent;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ResizeListener implements Runnable {
    private final SimpleTerminalResizeListener listener;
    private final ArrayBlockingQueue<Event> queue;

    public ResizeListener(Terminal terminal, ArrayBlockingQueue<Event> queue) throws IOException {
        this.listener = new SimpleTerminalResizeListener(terminal.getTerminalSize());
        this.queue = queue;
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
                        synchronized (queue) {
                            queue.offer(new ResizeEvent(size), 100, TimeUnit.MILLISECONDS);
                            queue.notify();
                        }
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
