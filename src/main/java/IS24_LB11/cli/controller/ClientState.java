package IS24_LB11.cli.controller;

import IS24_LB11.cli.CommandLine;
import IS24_LB11.cli.Stage;
import IS24_LB11.cli.event.*;
import IS24_LB11.cli.popup.PopUpStack;
import IS24_LB11.cli.popup.Priority;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.KeyConsumer;
import IS24_LB11.cli.listeners.ServerHandler;
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

    private ClientState nextState;
    protected String username;
    protected final ArrayBlockingQueue<Event> queue;
    protected final PriorityQueue<KeyConsumer> keyConsumers;
    protected final PopUpStack popUpStack;
    protected final ViewHub viewHub;
    protected final CommandLine cmdLine;
    protected ServerHandler serverHandler;
    protected Stage stage;

    public ClientState(ViewHub viewHub) throws IOException {
        this.nextState = null;
        this.queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        this.popUpStack = new PopUpStack(viewHub, 0);
        this.keyConsumers = new PriorityQueue<>((k1, k2) -> Integer.compare(k1.priority(), k2.priority()));
        this.viewHub = viewHub;
        this.cmdLine = new CommandLine(viewHub.getTerminal().getTerminalSize().getColumns());
        this.stage = viewHub.getStage();
        viewHub.updateCommandLine(cmdLine);
    }

    public ClientState execute() {
        keyConsumers.add(popUpStack);
        synchronized (queue) {
            while (true) {
                try { queue.wait(); }
                catch (InterruptedException e) {
                    System.err.println("caught exception: "+e.getMessage());
                    break;
                }
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

    protected void quit() {
        setNextState(null);
        Thread.currentThread().interrupt();
    }

    protected abstract void processServerEvent(ServerEvent event);

    protected abstract void processCommand(String command);

    protected boolean processServerEventIfCommon(ServerEvent serverEvent) {
        switch (serverEvent) {
            case ServerNotificationEvent notificationEvent -> {
                popUpStack.addPopUp(Priority.LOW, "from server", notificationEvent.message());
            }
            case ServerMessageEvent messageEvent -> {
                String text;
                if (messageEvent.to().isEmpty())
                    text = String.format("%s @ all : %s", messageEvent.from(), messageEvent.message());
                else
                    text = String.format("%s : %s", messageEvent.from(), messageEvent.message());
                popUpStack.addPopUp(Priority.MEDIUM, text);
            }
            case ServerHeartBeatEvent heartBeatEvent -> {
                sendToServer("heartbeat");
            }
            default -> {
                return false; //event not processed
            }
        }
        return true; //event processed
    }

    protected boolean processCommandIfCommon(String command) {
        String[] tokens = command.split(" ", 2);
        if (tokens.length == 0) return true;
        switch (tokens[0].toUpperCase()) {
            case "QUIT" -> {
                quit();
                return true;
            }
            case "SENDTO", "@" -> {
                if (tokens.length == 2) processCommandSendto(tokens[1]);
                else popUpStack.addUrgentPopUp("ERROR", "missing argument");
                return true;
            }
            case "SENDTOALL", "@ALL" -> {
                if (tokens.length == 2) processCommandSendtoall(tokens[1]);
                else popUpStack.addUrgentPopUp("ERROR", "missing argument");
                return true;
            }
        };
        return false;
    }

    protected void processResult(Result<ServerEvent> result) {
        if (result.isError()) {
            String text;
            text = result.getError();
            if (result.getCause() != null)
                text += " : "+result.getCause();
            popUpStack.addUrgentPopUp("ERROR", text);
            return;
        }
        processServerEvent(result.get());
    }

    protected void processResize(TerminalSize size) {
        cmdLine.setWidth(size.getColumns());
        viewHub.resize(size, cmdLine);
    }

    protected void processKeyStroke(KeyStroke keyStroke) {
        for (KeyConsumer listener: keyConsumers) {
            // if a listener use the keyStroke then no one with less priority will
            if (listener.consumeKeyStroke(keyStroke)) return;
        }
        switch (keyStroke.getKeyType()) {
            case Character:
                cmdLine.insertChar(keyStroke.getCharacter());
                break;
            case Backspace:
                cmdLine.deleteChar();
                break;
            case Enter:
                tryQueueEvent(new CommandEvent(cmdLine.getFullLine()));
                cmdLine.clearLine();
                break;
            case ArrowUp:
                break;
            case ArrowDown:
                break;
            case ArrowLeft:
                cmdLine.moveCursor(-1);
                break;
            case ArrowRight:
                cmdLine.moveCursor(1);
                break;
            case Escape:
                quit();
            default:
                break;
        }
        viewHub.updateCommandLine(cmdLine);
    }

    protected void processCommandSendto(String argument) {
        String[] tokens = argument.split(" ", 2);
        if (tokens.length == 2) {
            String[] fields = new String[] {"from", "to", "message"};
            String[] values = new String[] {username, tokens[0], tokens[1]};
            sendToServer("message", fields, values);
        } else {
            popUpStack.addUrgentPopUp("ERROR", "syntax error in given commmand");
        }
    }

    protected void processCommandSendtoall(String message) {
        String[] fields = new String[] {"from", "to", "message"};
        String[] values = new String[] {username, "", message};
        sendToServer("message", fields, values);
    }

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

    protected void sendToServer(String type, String field, String value) {
        if (serverHandler == null) return;
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        object.addProperty(field, value);
        serverHandler.write(object);
    }

    protected void sendToServer(String type, String field, JsonObject value) {
        if (serverHandler == null) return;
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        object.add(field, value);
        serverHandler.write(object);
    }

    protected void sendToServer(String type) {
        if (serverHandler == null) return;
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        serverHandler.write(object);
    }

    protected void sendToServer(String type, String[] dataFields, String[] values) {
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        for (int i = 0; i < Integer.min(dataFields.length, values.length); i++)
            object.addProperty(dataFields[i], values[i]);
        serverHandler.write(object);
    }

    protected void setNextState(ClientState nextState) {
        this.nextState = nextState;
    }
    
    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public Terminal getTerminal() {
        return viewHub.getTerminal();
    }
}
