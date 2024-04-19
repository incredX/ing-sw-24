package IS24_LB11.cli.controller;

import IS24_LB11.cli.CommandLine;
import IS24_LB11.cli.Stage;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.event.Event;
import IS24_LB11.game.Board;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class ClientState {
    private static final int QUEUE_CAPACITY = 64;

    //private Chat chat;
    private Optional<ClientState> nextState;
    protected final ArrayBlockingQueue<Event> queue;
    protected final ViewHub hub;
    protected final CommandLine cmdLine;
    protected Board board;
    protected Stage stage;
    protected boolean popUpOn;

    public ClientState(Board board, ViewHub hub) throws IOException {
        this.nextState = Optional.empty();
        this.queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        this.hub = hub;
        this.cmdLine = new CommandLine(hub.getTerminal().getTerminalSize().getColumns());
        this.stage = hub.getStage();
        this.board = board;
        this.popUpOn = false;
    }

    public ClientState execute() {
        synchronized (queue) {
            while (true) {
                try { queue.wait(); }
                catch (InterruptedException e) { break; }
                while(!queue.isEmpty()) {
                    handleEvent(queue.poll());
                    if (nextState.isPresent()) { return nextState.get(); }
                }
            }
        }
        return null;
    }

    protected abstract void handleEvent(Event event);

    public void queueEvent(Event event) throws InterruptedException {
        synchronized (queue) {
            queue.offer(event, 100, TimeUnit.MILLISECONDS);
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
        this.nextState = Optional.of(nextState);
    }

    public Board getBoard() {
        return board;
    }

    public Terminal getTerminal() {
        return hub.getTerminal();
    }
}
