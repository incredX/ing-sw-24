package IS24_LB11.cli;

import IS24_LB11.cli.event.Event;
import IS24_LB11.cli.event.KeyboardEvent;
import IS24_LB11.cli.event.MessageEvent;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class InputListener implements Runnable {
    private final Terminal terminal;
    private final ArrayBlockingQueue<Event> queue;

    public InputListener(Terminal terminal, ArrayBlockingQueue<Event> queue) {
        this.terminal = terminal;
        this.queue = queue;
    }

    public void run() {
        while (true) {
            try {
                KeyStroke keyStroke = terminal.readInput();
                if (keyStroke.getKeyType() == KeyType.EOF) break;
                synchronized (queue) {
                    queue.offer(new KeyboardEvent(keyStroke), 100, TimeUnit.MILLISECONDS);
                    queue.notify();
                }
                //if (Thread.interrupted()) throw new InterruptedException();
            } catch (IOException e) {
                synchronized (queue) { queue.offer(new MessageEvent(e.getMessage())); }
            } catch (InterruptedException e) {
                System.out.println("Thread of InputListener interrupted.");
                break;
            }
        }
    }
}
