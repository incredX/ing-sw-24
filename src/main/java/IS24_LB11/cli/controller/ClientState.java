package IS24_LB11.cli.controller;

import IS24_LB11.cli.CommandLine;
import IS24_LB11.cli.Stage;
import IS24_LB11.cli.event.*;
import IS24_LB11.cli.popup.PopUpQueue;
import IS24_LB11.cli.view.ViewHub;
import IS24_LB11.cli.KeyConsumer;
import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.game.Board;
import IS24_LB11.game.Result;
import com.google.gson.JsonObject;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
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

    protected void handleEvent(Event event) {
        switch (event) {
            case CommandEvent commandEvent -> processCommand(commandEvent.command());
            case KeyboardEvent keyboardEvent -> processKeyStroke(keyboardEvent.keyStroke());
            case ResizeEvent resizeEvent -> processResize(resizeEvent.size());
            case ResultServerEvent resultServerEvent -> processResult(resultServerEvent.result());
            default -> System.out.println("Unknown event: " + event.getClass().getName());
        };
    }

    protected void processResult(Result<ServerEvent> result) {
        if (result.isError()) {
            String text;
            text = result.getError();
            if (result.getCause() != null)
                text += "\ncause:"+result.getCause();
            popUpQueue.addUrgentPopUp("ERROR", text);
            return;
        }
        processServerEvent(result.get());
    }

    protected void processResize(TerminalSize size) {
        cmdLine.setWidth(size.getColumns());
        hub.resize(size, cmdLine);
    }

    protected abstract void processKeyStroke(KeyStroke keyStroke);

    protected abstract void processCommand(String command);

    protected abstract void processServerEvent(ServerEvent event);

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

    protected void sendToServer(String type, JsonObject data) {
        if (serverHandler == null) return;
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        object.add("data", data);
        serverHandler.write(object);
    }

    protected void sendToServer(String type, String field, String value) {
        JsonObject data = new JsonObject();
        data.addProperty(field, value);
        sendToServer(type, data);
    }

    protected void sendToServer(String type, String[] dataFields, String[] values) {
        JsonObject object = new JsonObject();
        JsonObject data = new JsonObject();
        object.addProperty("type", type);
        for (int i = 0; i < Integer.min(dataFields.length, values.length); i++)
            data.addProperty(dataFields[i], values[i]);
        object.add("data", data);
        serverHandler.write(object);
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
