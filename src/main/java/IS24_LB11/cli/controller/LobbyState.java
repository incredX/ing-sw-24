package IS24_LB11.cli.controller;

import IS24_LB11.cli.Table;
import IS24_LB11.cli.event.server.ServerEvent;
import IS24_LB11.cli.event.server.ServerLoginEvent;
import IS24_LB11.cli.event.server.ServerPlayerDisconnectEvent;
import IS24_LB11.cli.event.server.ServerPlayerSetupEvent;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.cli.popup.HelpPoup;
import IS24_LB11.cli.view.stage.LobbyStage;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.Result;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;

import static IS24_LB11.cli.notification.Priority.LOW;

/**
 * The LobbyState class represents the state of the client when in the lobby.
 * It handles server events, user commands, and various input methods.
 */
public class LobbyState extends ClientState {
    private LobbyStage lobbyStage;

    /**
     * Constructs a new LobbyState with the specified ViewHub.
     *
     * @param viewHub the view hub used for managing views and stages.
     */
    public LobbyState(ViewHub viewHub) {
        super(viewHub);
        this.username = "";
        this.popManager.addPopup(
                new HelpPoup(viewHub, this)
        );
    }

    /**
     * Executes the lobby state by setting up the lobby stage and updating necessary components.
     *
     * @return the next client state to be executed.
     */
    @Override
    public ClientState execute() {
        lobbyStage = viewHub.setLobbyStage(this);
        popManager.updatePopups();
        cmdLine.update();
        viewHub.update();
        return super.execute();
    }

    /**
     * Processes the received server event.
     *
     * @param serverEvent the server event to be processed.
     */
    @Override
    protected void processServerEvent(ServerEvent serverEvent) {
        if (processServerEventIfCommon(serverEvent)) return;
        switch (serverEvent) {
            case ServerLoginEvent loginEvent -> {
                username = loginEvent.username();
            }
            case ServerPlayerSetupEvent setupEvent -> {
                PlayerSetup setup = setupEvent.setup();
                Table table = new Table(setupEvent);
                notificationStack.removeNotifications(LOW);
                setNextState(new SetupState(this, setup, table));
            }
            case ServerPlayerDisconnectEvent disconnectEvent -> {
                notificationStack.add(LOW, "Players "+disconnectEvent.player()+" disconnected");
            }
            default -> processResult(Result.Error("received unknown server event"));
        }
    }

    /**
     * Processes the received command.
     *
     * @param command the command to be processed.
     */
    @Override
    protected void processCommand(String command) {
        if (processCommandIfCommon(command)) return;
        String[] tokens = command.split(" +", 2);
        switch (tokens[0].toUpperCase()) {
            case "LOGIN" -> {
                if (tokens.length == 2) processCommandLogin(tokens[1]);
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply(tokens[0]));
            }
            case "CONNECT" -> {
                if (tokens.length == 2) {
                    processCommandConnect(tokens[1]);
                }
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply(tokens[0]));
            }
            case "SET" -> {
                if (tokens.length == 2) setProperty(tokens[1]);
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply(tokens[0]));
            }
            default -> notificationStack.addUrgent("ERROR", INVALID_CMD.apply(tokens[0], "lobby"));
        };
    }

    /**
     * Processes the received keystroke.
     *
     * @param keyStroke the keystroke to be processed.
     */
    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        keyConsumed = notificationStack.consumeKeyStroke(keyStroke);
        if (!keyConsumed) cmdLine.consumeKeyStroke(this, keyStroke);
        if (!keyConsumed) popManager.consumeKeyStroke(keyStroke);
    }

    /**
     * Processes the screen resize event.
     *
     * @param screenSize the new size of the screen.
     */
    @Override
    protected void processResize(TerminalSize screenSize) {
        super.processResize(screenSize);
        popManager.resizePopups();
    }

    /**
     * Sets a property based on the provided string.
     *
     * @param property the property to be set.
     */
    private void setProperty(String property) {
        String[] tokens = property.split(" *={1} *", 2);
        if (tokens.length < 2) {
            notificationStack.addUrgent("ERROR", MISSING_ARG.apply(tokens[0]));
            return;
        }
        switch (tokens[0].toUpperCase()) {
            case "PLAYERS" -> {
                try {
                    int numPlayers = Integer.parseInt(tokens[1]);
                    sendToServer("numOfPlayers", "numOfPlayers", numPlayers);
                } catch (NumberFormatException e) {
                    notificationStack.addUrgent("ERROR", EXPECTED_INT.apply("players"));
                }
            }
        }
    }

    /**
     * Processes the login command with the specified username.
     *
     * @param username the username to login with.
     */
    private void processCommandLogin(String username) {
        if (username.contains(" ")) {
            notificationStack.addUrgent("ERROR", INVALID_ARG.apply(username, "login"));
        } else if (username.length() >= 11) {
            notificationStack.addUrgent("ERROR", INVALID_ARG.apply(username, "login") + "(max accepted length is 10)");
        } else {
            sendToServer("login", "username", username);
        }
    }

    /**
     * Processes the connect command with the specified argument.
     *
     * @param argument the argument for the connect command.
     */
    private void processCommandConnect(String argument) {
        String[] tokens = argument.split(" +", 2);
        if (tokens.length != 2)
            notificationStack.addUrgent("ERROR", MISSING_ARG.apply(tokens[0]));
        else try {
            int serverPort = Integer.parseInt(tokens[1]);
            sendToServer("quit");
            // serverHandler.shutdown(); // Commented out line from original code
            serverHandler = new ServerHandler(this, tokens[0], serverPort);
            new Thread(serverHandler).start();
        } catch (NumberFormatException e) {
            notificationStack.addUrgent("ERROR", EXPECTED_INT.apply("port"));
        } catch (IOException e) {
            notificationStack.addUrgent("ERROR", "connection to server failed");
        }
    }
}
