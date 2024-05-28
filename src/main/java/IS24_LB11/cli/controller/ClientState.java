package IS24_LB11.cli.controller;

import IS24_LB11.cli.CommandLine;
import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.event.server.*;
import IS24_LB11.cli.event.*;
import IS24_LB11.cli.notification.NotificationStack;
import IS24_LB11.cli.notification.Priority;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.cli.popup.ChatPopup;
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

/**
 * Abstract class representing the state of a client in the application.
 */
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

    /**
     * Constructs a new ClientState with the specified view hub and notification stack.
     *
     * @param viewHub the view hub
     * @param notificationStack the notification stack
     */
    public ClientState(ViewHub viewHub, NotificationStack notificationStack) {
        this.nextState = null;
        this.username = "";
        this.popManager = new PopupManager();
        this.queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        this.notificationStack = notificationStack;
        this.viewHub = viewHub;
        this.cmdLine = new CommandLine(viewHub.getCommandLineView());
        this.keyConsumed = false;
    }

    /**
     * Constructs a new ClientState based on an existing state.
     *
     * @param state the existing state
     */
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

    /**
     * Constructs a new ClientState with the specified view hub.
     *
     * @param viewHub the view hub
     */
    public ClientState(ViewHub viewHub) {
        this(viewHub, new NotificationStack(viewHub, 0));
    }

    /**
     * Executes the client state, handling queued events.
     *
     * @return the next state
     */
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

    /**
     * Handles an event by dispatching it to the appropriate handler method.
     *
     * @param event the event to handle
     */
    protected void handleEvent(Event event) {
        switch (event) {
            case CommandEvent commandEvent -> processCommand(commandEvent.command());
            case KeyboardEvent keyboardEvent -> processKeyStroke(keyboardEvent.keyStroke());
            case ResizeEvent resizeEvent -> processResize(resizeEvent.size());
            case NotificationEvent notificationEvent -> processNotification(notificationEvent);
            case ResultServerEvent resultServerEvent -> processResult(resultServerEvent.result());
            case ServerDownEvent serverDownEvent -> processServerDown();
            default -> Debugger.print("Unknown event: " + event.getClass().getName());
        };
        viewHub.update();
    }

    /**
     * Quits the client state, interrupting the current thread.
     */
    public void quit() {
        setNextState(null);
        Thread.currentThread().interrupt();
        if (serverHandler != null) {
            sendToServer("quit");
            serverHandler.shutdown();
        }
    }

    /**
     * Shuts down the client state by queuing a quit event.
     */
    public void shutdown() {
        tryQueueEvent(new CommandEvent("quit"));
    }

    /**
     * Processes a server event.
     *
     * @param event the server event to process
     */
    protected abstract void processServerEvent(ServerEvent event);

    /**
     * Processes a command.
     *
     * @param command the command to process
     */
    protected abstract void processCommand(String command);

    /**
     * Processes a key stroke.
     *
     * @param keyStroke the keystroke to process
     */
    protected abstract void processKeyStroke(KeyStroke keyStroke);

    /**
     * Processes the event of the server going down.
     */
    protected void processServerDown() {
        popManager.hideAllPopups();
        notificationStack.removeAllNotifications();
        serverHandler.shutdown();
        tryQueueEvent(NotificationEvent.urgent("WARNING", "lost connection to server"));
    }

    /**
     * Processes a resize event.
     *
     * @param screenSize the new screen size
     */
    protected void processResize(TerminalSize screenSize) {
        cmdLine.resize(screenSize);
        viewHub.resize(screenSize);
    }

    /**
     * Processes a notification event issued by a listener or other client-side component.
     *
     * @param event {@code NotificationEvent} containing the data to display
     */
    protected void processNotification(NotificationEvent event) {
        notificationStack.add(event.getPriority(), event.getTitle(), event.getMessage());
    }

    /**
     * Processes a result received from the server.
     *
     * @param result the result to process
     */
    protected void processResult(Result<ServerEvent> result) {
        if (result.isError()) {
            String text = result.getError();
            if (result.getCause() != null)
                text += " : " + result.getCause();
            Debugger.print("ERROR (from server) : " + text);
            notificationStack.addUrgent("ERROR (from server)", text);
            viewHub.update();
            return;
        }
        processServerEvent(result.get());
    }

    /**
     * Processes common server events.
     *
     * @param serverEvent the server event to process
     * @return true if the event was processed, false otherwise
     */
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
                popManager.getOptionalPopup("chat")
                        .ifPresent(popup -> ((ChatPopup)popup).newMessage(messageEvent));
                notificationStack.add(Priority.MEDIUM, text);
            }
            case ServerHeartBeatEvent heartBeatEvent -> {
                sendToServer("heartbeat");
            }
            default -> {
                return false; // event not processed
            }
        }
        return true; // event processed
    }

    /**
     * Processes common commands.
     *
     * @param command the command to process
     * @return true if the command was processed, false otherwise
     */
    protected boolean processCommandIfCommon(String command) {
        String[] tokens = command.split(" ", 2);
        if (tokens.length == 0) return true;
        switch (tokens[0].toUpperCase()) {
            case "QUIT" -> quit();
            case "SENDTO", "@" -> {
                if (tokens.length == 2) processCommandSendto(tokens[1]);
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply("sendto"));
            }
            case "SENDTOALL", "@ALL" -> {
                if (tokens.length == 2) processCommandSendtoall(tokens[1]);
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply("sendtoall"));
            }
            case "SHOW" -> {
                if (tokens.length == 2) {
                    if (popManager.hasPopup(tokens[1])) {
                        popManager.showPopup(tokens[1]);
                        cmdLine.disable();
                    } else notificationStack.addUrgent("ERROR", "unknown pop-up");
                } else notificationStack.addUrgent("ERROR", MISSING_ARG.apply("show"));
            }
            case "HIDE" -> {
                if (tokens.length == 2) {
                    if (popManager.hasPopup(tokens[1])) popManager.hidePopup(tokens[1]);
                    else notificationStack.addUrgent("ERROR", "unknown pop-up");
                } else popManager.hideFocusedPopup();
            }
            case "CHAT" -> {
                String endUser;
                if (!popManager.hasPopup(tokens[0])) break;
                if (tokens.length == 1) endUser = "";
                else endUser = tokens[1];
                popManager.getOptionalPopup("chat")
                        .ifPresent(popup -> ((ChatPopup)popup).setCurrentEndUser(endUser));
                popManager.showPopup("chat");
            }
            default -> {
                if (popManager.hasPopup(tokens[0])) {
                    popManager.showPopup(tokens[0]);
                    cmdLine.disable();
                } else return false;
            }
        };
        return true;
    }

    /**
     * Processes the send-to command.
     *
     * @param argument the command argument
     */
    protected void processCommandSendto(String argument) {
        String[] tokens = argument.split(" ", 2);
        if (tokens.length == 2) {
            String[] fields = new String[]{"from", "to", "message"};
            String[] values = new String[]{username, tokens[0], tokens[1]};
            JsonObject message = putTogether(fields, values);

            popManager.getOptionalPopup("chat")
                    .ifPresent(popup -> ((ChatPopup)popup).newMessage(message));
            sendToServer("message", message);
        } else {
            notificationStack.addUrgent("ERROR", "syntax error in given command");
        }
    }

    /**
     * Processes the send-to-all command.
     *
     * @param text the message text
     */
    protected void processCommandSendtoall(String text) {
        String[] fields = new String[]{"from", "to", "message"};
        String[] values = new String[]{username, "", text};
        JsonObject message = putTogether(fields, values);

        popManager.getOptionalPopup("chat")
                .ifPresent(popup -> ((ChatPopup)popup).newMessage(message));
        sendToServer("message", message);
    }

    /**
     * Queues an event.
     *
     * @param event the event to queue
     * @throws InterruptedException if the current thread is interrupted
     */
    public void queueEvent(Event event) throws InterruptedException {
        synchronized (queue) {
            if (queue.offer(event, 100, TimeUnit.MILLISECONDS))
                queue.notifyAll();
            else {
                Debugger.print("lost event " + event.getClass().getSimpleName());
            }
        }
    }

    /**
     * Tries to queue an event.
     *
     * @param event the event to queue
     */
    public void tryQueueEvent(Event event) {
        synchronized (queue) {
            if (queue.offer(event)) queue.notifyAll();
        }
    }

    /**
     * Combines data fields and values into a JSON object.
     *
     * @param dataFields the data fields
     * @param values the values
     * @return the combined JSON object
     */
    protected JsonObject putTogether(String[] dataFields, String[] values) {
        JsonObject object = new JsonObject();
        for (int i = 0; i < Math.min(dataFields.length, values.length); i++)
            object.addProperty(dataFields[i], values[i]);
        return object;
    }

    /**
     * Sends a {@code JsonObject} representing an event to the server.
     *
     * @param type the type of event
     * @param field the field name
     * @param value the field value
     */
    protected void sendToServer(String type, String field, String value) {
        if (serverHandler == null) {
            notificationStack.addUrgent("WARNING", "disconnected from server");
            return;
        }
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        object.addProperty(field, value);
        serverHandler.write(object);
    }

    /**
     * Sends a {@code JsonObject} representing an event to the server.
     *
     * @param type the type of event
     * @param field the field name
     * @param value the field value
     */
    protected void sendToServer(String type, String field, int value) {
        if (serverHandler == null) return;
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        object.addProperty(field, value);
        serverHandler.write(object);
    }

    /**
     * Sends a {@code JsonObject} representing an event to the server.
     *
     * @param type the type of event
     * @param field the field name to be added to the {@code JsonObject}
     * @param value the field's value
     */
    protected void sendToServer(String type, String field, JsonObject value) {
        if (serverHandler == null) return;
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        object.add(field, value);
        serverHandler.write(object);
    }

    /**
     * Sends a {@code JsonObject} with only the type of event to the server.
     *
     * @param type the type of event
     */
    public void sendToServer(String type) {
        if (serverHandler == null) return;
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        serverHandler.write(object);
    }

    /**
     * Sends a {@code JsonObject} representing an event to the server.
     *
     * @param type the type of event
     * @param object the JSON object to which add the type of event and send
     */
    protected void sendToServer(String type, JsonObject object) {
        object.addProperty("type", type);
        serverHandler.write(object);
    }

    /**
     * Sends a {@code JsonObject} representing an event to the server.
     *
     * @param type the type of event
     * @param dataFields the data fields
     * @param values the values
     */
    protected void sendToServer(String type, String[] dataFields, String[] values) {
        sendToServer(type, putTogether(dataFields, values));
    }

    /**
     * Sends a {@code JsonObject} representing an event to the server.
     *
     * @param type the type of event
     * @param dataFields the data fields
     * @param values the values
     */
    protected void sendToServer(String type, String[] dataFields, JsonElement[] values) {
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        for (int i = 0; i < Math.min(dataFields.length, values.length); i++)
            object.add(dataFields[i], values[i]);
        serverHandler.write(object);
    }

    /**
     * Toggles the visibility of a popup.
     *
     * @param label the label of the popup
     */
    public void togglePopup(String label) {
        popManager.getOptionalPopup(label).ifPresent(popup -> {
            if (popup.isVisible()) popManager.hidePopup(label);
            else popManager.showPopup(label);
        });
    }

    /**
     * Hides all popups.
     */
    public void hideAllPopups(){
        popManager.hideAllPopups();
    }

    /**
     * Consumes the key, indicating it has been handled.
     */
    public void consumeKey() {
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

    public ServerHandler getServerHandler() {
        return serverHandler;
    }

    public String getUsername() {
        return username;
    }
}