package IS24_LB11.cli.controller;

import IS24_LB11.cli.Table;
import IS24_LB11.cli.event.server.ServerEvent;
import IS24_LB11.cli.event.server.ServerLoginEvent;
import IS24_LB11.cli.event.server.ServerPlayerSetupEvent;
import IS24_LB11.cli.ViewHub;
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

    public LobbyState(ViewHub hub) throws IOException {
        super(hub);
        this.username = "";
    }

    @Override
    public ClientState execute() {
        lobbyStage = viewHub.setLobbyStage(this);
        processResize(viewHub.getScreenSize());
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
                try {
                    PlayerSetup setup = setupEvent.setup();
                    Scoreboard scoreboard = new Scoreboard(setupEvent.playersList(), setupEvent.colorList());
                    Table table = new Table(scoreboard, setupEvent.publicGoals());
                    setNextState(new SetupState(this, setup, table));
                }
                catch (IOException e) {
                    e.printStackTrace();
                    quit();
                }
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
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply("login"));
            }
            case "SET" -> {
                if (tokens.length == 2) setProperty(tokens[1]);
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply("set"));
            }
            case "SETUP" -> {
                try { setNextState(new SetupState(this, getDefaultSetup(), defaultTable())); } // wait server response to switch
                catch (IOException e) {
                    e.printStackTrace();
                    quit();
                }
            }
            default -> notificationStack.addUrgent("ERROR", INVALID_CMD.apply(tokens[0], "lobby"));
        };
        viewHub.update();
    }

    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        if (notificationStack.consumeKeyStroke(keyStroke)) {
            viewHub.update();
            return;
        }
        super.processCommonKeyStrokes(keyStroke);
        viewHub.updateCommandLine(cmdLine);
    }

    @Override
    protected void processResize(TerminalSize size) {
        super.processResize(size);
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
