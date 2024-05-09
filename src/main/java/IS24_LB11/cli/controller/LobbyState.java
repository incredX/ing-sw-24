package IS24_LB11.cli.controller;

import IS24_LB11.cli.Table;
import IS24_LB11.cli.event.server.ServerEvent;
import IS24_LB11.cli.event.server.ServerLoginEvent;
import IS24_LB11.cli.event.server.ServerPlayerDisconnectEvent;
import IS24_LB11.cli.event.server.ServerPlayerSetupEvent;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.cli.popup.ChatPopup;
import IS24_LB11.cli.popup.HelpPoup;
import IS24_LB11.cli.view.stage.LobbyStage;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.Result;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;

public class LobbyState extends ClientState {
    private LobbyStage lobbyStage;

    public LobbyState(ViewHub viewHub) {
        super(viewHub);
        this.username = "";
        this.popManager.addPopup(
                new HelpPoup(viewHub, this),
                new ChatPopup(viewHub, this)
        );
    }

    @Override
    public ClientState execute() {
        lobbyStage = viewHub.setLobbyStage(this);
        popManager.updatePopups();
        cmdLine.update();
        viewHub.update();
        return super.execute();
    }

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
                setNextState(new SetupState(this, setup, table));
            }
            case ServerPlayerDisconnectEvent disconnectEvent -> {}
            default -> processResult(Result.Error("received unknown server event"));
        }
    }

    @Override
    protected void processCommand(String command) {
        if (processCommandIfCommon(command)) return;
        String[] tokens = command.split(" ", 2);
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

    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        keyConsumed = notificationStack.consumeKeyStroke(keyStroke);
        if (!keyConsumed) cmdLine.consumeKeyStroke(this, keyStroke);
        if (!keyConsumed) popManager.consumeKeyStroke(keyStroke);
    }

    @Override
    protected void processResize(TerminalSize screenSize) {
        super.processResize(screenSize);
        popManager.resizePopups();
    }

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

    private void processCommandLogin(String username) {
        sendToServer("login", "username", username);
    }

    private void processCommandConnect(String argument) {
        String[] tokens = argument.split(" ", 2);
        if (tokens.length != 2)
            notificationStack.addUrgent("ERROR", MISSING_ARG.apply(tokens[0]));
        else try {
            int serverPort = Integer.parseInt(tokens[1]);
            sendToServer("quit");
//            serverHandler.shutdown();
            serverHandler = new ServerHandler(this, tokens[0], serverPort);
            new Thread(serverHandler).start();
        } catch (NumberFormatException e) {
            notificationStack.addUrgent("ERROR", EXPECTED_INT.apply("port"));
        } catch (IOException e) {
            notificationStack.addUrgent("ERROR", "connection to server failed");
        }
    }
}
