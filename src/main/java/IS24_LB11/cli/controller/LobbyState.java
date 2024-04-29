package IS24_LB11.cli.controller;

import IS24_LB11.cli.Table;
import IS24_LB11.cli.event.server.ServerEvent;
import IS24_LB11.cli.event.server.ServerLoginEvent;
import IS24_LB11.cli.event.server.ServerPlayerSetupEvent;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.cli.view.stage.LobbyStage;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.Result;
import IS24_LB11.cli.Scoreboard;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;

import static IS24_LB11.cli.Client.defaultTable;
import static IS24_LB11.cli.Client.getDefaultSetup;

public class LobbyState extends ClientState {
    private LobbyStage lobbyStage;

    public LobbyState(ViewHub viewHub) {
        super(viewHub);
        this.username = "";
    }

    @Override
    public ClientState execute() {
        lobbyStage = viewHub.setLobbyStage(this);
        cmdLine.update();
        viewHub.update();
        return super.execute();
    }

    protected void processServerEvent(ServerEvent serverEvent) {
        if (processServerEventIfCommon(serverEvent)) {
            viewHub.update();
            return;
        }
        switch (serverEvent) {
            case ServerLoginEvent loginEvent -> {
                username = loginEvent.username();
            }
            case ServerPlayerSetupEvent setupEvent -> {
                PlayerSetup setup = setupEvent.setup();
                Scoreboard scoreboard = new Scoreboard(setupEvent.playersList(), setupEvent.colorList());
                Table table = new Table(scoreboard, setupEvent.publicGoals());
                setNextState(new SetupState(this, setup, table));
            }
            default -> processResult(Result.Error("received unknown server event"));
        }
        viewHub.update();
    }

    protected void processCommand(String command) {
        if (processCommandIfCommon(command)) {
            viewHub.update();
            return;
        }
        String[] tokens = command.split(" ", 2);
        switch (tokens[0].toUpperCase()) {
            case "LOGIN" -> {
                if (tokens.length == 2) processCommandLogin(tokens[1]);
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply(tokens[0]));
            }
            case "CONNECT" -> {
                if (tokens.length == 2) {
                    tokens = tokens[1].split(" ", 2);
                    if (tokens.length != 2)
                        notificationStack.addUrgent("ERROR", MISSING_ARG.apply(tokens[0]));
                    else try {
                        int serverPort = Integer.parseInt(tokens[1]);
                        sendToServer("quit");
                        serverHandler.shutdown();
                        serverHandler = new ServerHandler(this, tokens[0], serverPort);
                        new Thread(serverHandler).start();
                    } catch (NumberFormatException e) {
                        notificationStack.addUrgent("ERROR", EXPECTED_INT.apply("port"));
                    } catch (IOException e) {
                        notificationStack.addUrgent("ERROR", "connection to server failed");
                    }
                }
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply(tokens[0]));
            }
            case "SET" -> {
                if (tokens.length == 2) setProperty(tokens[1]);
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply(tokens[0]));
            }
            case "SETUP" -> {
                setNextState(new SetupState(this, getDefaultSetup(), defaultTable()));
            }
            default -> notificationStack.addUrgent("ERROR", INVALID_CMD.apply(tokens[0], "lobby"));
        };
        viewHub.update();
    }

    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        if (!notificationStack.consumeKeyStroke(keyStroke)) {
            cmdLine.consumeKeyStroke(this, keyStroke);
        }
        viewHub.update();
    }

    @Override
    protected void processResize(TerminalSize screenSize) {
        super.processResize(screenSize);
        //popManager.resizePopups();
        viewHub.update();
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
}
