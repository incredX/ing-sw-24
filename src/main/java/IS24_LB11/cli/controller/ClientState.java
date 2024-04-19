package IS24_LB11.cli.controller;

import IS24_LB11.cli.CommandLine;
import IS24_LB11.cli.Stage;
import IS24_LB11.cli.popup.PopUpQueue;
import IS24_LB11.cli.view.ViewHub;
import IS24_LB11.cli.event.Event;
import IS24_LB11.cli.KeyConsumer;
import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.game.Board;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class ClientState {
    private static final int QUEUE_CAPACITY = 64;

    //private Chat chat;
    private ClientState nextState;
    protected final ArrayBlockingQueue<Event> queue;
    protected final PriorityQueue<KeyConsumer> keyConsumers;
    protected final PopUpQueue popUpQueue;
    protected final ViewHub hub;
    protected final CommandLine cmdLine;
    protected ServerHandler serverHandler;
    protected Board board;
    protected Stage stage;

    public ClientState(Board board, ViewHub hub) throws IOException {
        this.nextState = null;
        this.queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        this.popUpQueue = new PopUpQueue(hub, 0);
        this.keyConsumers = new PriorityQueue<>((k1, k2) -> Integer.compare(k1.priority(), k2.priority()));
        this.hub = hub;
        this.cmdLine = new CommandLine(hub.getTerminal().getTerminalSize().getColumns());
        this.stage = hub.getStage();
        this.board = board;
    }

    public ClientState execute() {
        keyConsumers.add(popUpQueue);
        synchronized (queue) {
            while (true) {
                try { queue.wait(); }
                catch (InterruptedException e) { break; }
                while(!queue.isEmpty()) {
                    handleEvent(queue.poll());
                    if (nextState != null) { return nextState; }
                }
            }
        }
        return null;
    }

    protected abstract void handleEvent(Event event);

    public void queueEvent(Event event) throws InterruptedException {
        synchronized (queue) {
            if (queue.offer(event, 20, TimeUnit.MILLISECONDS))
                queue.notify();
        }
    }

    public void tryQueueEvent(Event event) {
        synchronized (queue) {
            if (queue.offer(event)) queue.notify();
        }
    }

    protected void processResize(TerminalSize size) {
        cmdLine.setWidth(size.getColumns());
        hub.resize(size, cmdLine);
    }

    protected void setNextState(ClientState nextState) {
        this.nextState = nextState;
    }
    
    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public Board getBoard() {
        return board;
    }

    public Terminal getTerminal() {
        return hub.getTerminal();
    }
}
