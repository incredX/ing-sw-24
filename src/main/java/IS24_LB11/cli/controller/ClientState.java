package IS24_LB11.cli.controller;

import IS24_LB11.cli.CommandLine;
import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.event.server.ServerEvent;
import IS24_LB11.cli.event.server.ServerHeartBeatEvent;
import IS24_LB11.cli.event.server.ServerMessageEvent;
import IS24_LB11.cli.event.server.ServerNotificationEvent;
import IS24_LB11.cli.event.*;
import IS24_LB11.cli.notification.NotificationStack;
import IS24_LB11.cli.notification.Priority;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.cli.popup.PopupManager;
import IS24_LB11.game.Result;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class ClientState {
    protected static final Function<String, String> MISSING_ARG =
            (cmd) -> String.format("missing argument(s) for command \"%s\"", cmd);
    protected static final BiFunction<String, String, String> INVALID_CMD =
            (key, state) -> String.format("\"%s\" is not a command in %s", key, state);
    protected static final BiFunction<String, String, String> INVALID_ARG =
            (arg, cmd) -> String.format("\"%s\" is not a valid argument for command \"%s\"", arg, cmd);
    protected static final Function<String, String> EXPECTED_INT =
            key -> String.format("argument \"%s\" expected an integer", key);

    private static final int QUEUE_CAPACITY = 64;

    private ClientState nextState;
    protected String username;
    protected final PopupManager popManager;
    protected final ArrayBlockingQueue<Event> queue;
    protected final NotificationStack notificationStack;
    protected final ViewHub viewHub;
    protected final CommandLine cmdLine;
    protected ServerHandler serverHandler;
    protected boolean keyConsumed;


    public ClientState(ViewHub viewHub, NotificationStack notificationStack) {
        this.nextState = null;
        this.username = "";
        this.popManager = new PopupManager();
        this.queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        this.notificationStack = notificationStack;
        this.viewHub = viewHub;
        this.cmdLine = new CommandLine(viewHub.getCommandLineView());
        this.keyConsumed = false;
//        try {
//            this.serverHandler = new ServerHandler(this, "127.0.0.1", 54321);
//            new Thread(this.serverHandler).start();
//        } catch (IOException e) {
//            notificationStack.addUrgent("ERROR", "Connection to server failed");
//        }
    }

    public ClientState(ClientState state) {
        this.nextState = null;
        this.popManager = state.popManager;
        this.username = state.username;
        this.queue = state.queue;
        this.notificationStack = state.notificationStack;
        this.viewHub = state.viewHub;
        this.cmdLine = state.cmdLine;
        this.serverHandler = state.serverHandler;
    }

    public ClientState(ViewHub viewHub) {
        this(viewHub, new NotificationStack(viewHub, 0));
    }

    public ClientState execute() {
        synchronized (queue) {
            while (true) {
                try { queue.wait(); }
                catch (InterruptedException e) {
                    Debugger.print(e);
                    break;
                }
                while(!queue.isEmpty()) {
                    handleEvent(queue.poll());
                }
                if (nextState != null) { return nextState; }
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
            default -> Debugger.print("Unknown event: " + event.getClass().getName());
        };
    }

    public void quit() {
        setNextState(null);
        Thread.currentThread().interrupt();
        if (serverHandler != null) {
            sendToServer("quit");
            serverHandler.shutdown();
        }
    }

    protected abstract void processServerEvent(ServerEvent event);

    protected abstract void processCommand(String command);

    protected abstract void processKeyStroke(KeyStroke keyStroke);

    protected void processResize(TerminalSize screenSize) {
        cmdLine.resize(screenSize);
        viewHub.resize(screenSize);
    }

    protected void processResult(Result<ServerEvent> result) {
        if (result.isError()) {
            String text;
            text = result.getError();
            if (result.getCause() != null)
                text += " : "+result.getCause();
            notificationStack.addUrgent("ERROR (from server)", text);
            viewHub.update();
            return;
        }
        processServerEvent(result.get());
    }

    protected boolean processServerEventIfCommon(ServerEvent serverEvent) {
        switch (serverEvent) {
            case ServerNotificationEvent notificationEvent -> {
                notificationStack.add(Priority.LOW, "from server", notificationEvent.message());
            }
            case ServerMessageEvent messageEvent -> {
                String text;
                if (messageEvent.to().isEmpty())
                    text = String.format("%s @ all : %s", messageEvent.from(), messageEvent.message());
                else
                    text = String.format("%s : %s", messageEvent.from(), messageEvent.message());
                notificationStack.add(Priority.MEDIUM, text);
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
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply("sendto"));
                return true;
            }
            case "SENDTOALL", "@ALL" -> {
                if (tokens.length == 2) processCommandSendtoall(tokens[1]);
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply("sendtoall"));
                return true;
            }
        };
        return false;
    }

    protected void processCommandSendto(String argument) {
        String[] tokens = argument.split(" ", 2);
        if (tokens.length == 2) {
            String[] fields = new String[] {"from", "to", "message"};
            String[] values = new String[] {username, tokens[0], tokens[1]};
            sendToServer("message", fields, values);
        } else {
            notificationStack.addUrgent("ERROR", "syntax error in given commmand");
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

    protected void sendToServer(String type, String field, int value) {
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

    protected void sendToServer(String type, String[] dataFields, JsonElement[] values) {
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        for (int i = 0; i < Integer.min(dataFields.length, values.length); i++)
            object.add(dataFields[i], values[i]);
        serverHandler.write(object);
    }

    public void togglePopup(String label) {
        popManager.getOptionalPopup(label).ifPresent(popup -> {
            if (popup.isVisible()) popManager.hidePopup(label);
            else popManager.showPopup(label);
        });
    }

    public void keyConsumed() {
        keyConsumed = true;
    }

    protected void setNextState(ClientState nextState) {
        this.nextState = nextState;
    }
    
    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public ViewHub getViewHub() {
        return viewHub;
    }

    public Screen getScreen() {
        return viewHub.getScreen();
    }

    public ServerHandler getServerHandler() { return serverHandler; }
}
