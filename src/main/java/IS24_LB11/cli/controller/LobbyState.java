package IS24_LB11.cli.controller;

import IS24_LB11.cli.Table;
import IS24_LB11.cli.event.server.ServerEvent;
import IS24_LB11.cli.event.server.ServerLoginEvent;
import IS24_LB11.cli.event.server.ServerPlayerSetupEvent;
import IS24_LB11.cli.notification.Priority;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.view.stage.LobbyStage;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.Result;
import IS24_LB11.cli.Scoreboard;
import IS24_LB11.game.components.*;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.SyntaxException;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        lobbyStage = viewHub.setLobbyStage();
        processResize(viewHub.getScreenSize());
        return super.execute();
    }

    protected void processServerEvent(ServerEvent serverEvent) {
        if (processServerEventIfCommon(serverEvent)) return;
        switch (serverEvent) {
            case ServerLoginEvent loginEvent -> {
                username = loginEvent.username();
            }
            case ServerPlayerSetupEvent setupEvent -> {
                notificationStack.add(Priority.LOW, "received player setup");
                try {
                    PlayerSetup setup = setupEvent.setup();
                    Scoreboard scoreboard = new Scoreboard(setupEvent.playersList(), new ArrayList<>(), setupEvent.colorList());
                    Table table = new Table(scoreboard, setupEvent.publicGoals());
                    setNextState(new SetupState(viewHub, notificationStack, setup, table));
                }
                catch (IOException e) {
                    e.printStackTrace();
                    quit();
                }
            }
            default -> processResult(Result.Error("received unknown server event"));
        }
    }

    protected void processCommand(String command) {
        if (processCommandIfCommon(command)) return;
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
                try { setNextState(new SetupState(viewHub, notificationStack, getDefaultSetup(), defaultTable())); } // wait server response to switch
                catch (IOException e) {
                    e.printStackTrace();
                    quit();
                }
            }
            case "POPUP" -> {
                if (tokens.length == 2) processCommandPopup(tokens[1]);
                else notificationStack.addUrgent("ERROR", "missing argument");
            }
            default -> notificationStack.addUrgent("ERROR", INVALID_CMD.apply(tokens[0], "lobby"));
        };
    }

    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        if (notificationStack.consumeKeyStroke(keyStroke)) return;
        super.processCommonKeyStrokes(keyStroke);
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

    private void processCommandPopup(String argument) {
        String[] tokens = argument.split(" ", 3);
        System.out.print("POPUP: ");
        for (String token: tokens) System.out.print(token + ", ");
        System.out.print("\n");
        Priority priority;
        try { priority = Priority.valueOf(tokens[0].toUpperCase()); }
        catch (IllegalArgumentException e) {
            notificationStack.addUrgent("ERROR", tokens[0]+" is not a valid priority");
            return;
        }
        if (tokens.length >= 3) {
            notificationStack.add(priority, tokens[1], tokens[2]);
            System.out.println("added popup.");
        } else if (tokens.length == 2) {
            notificationStack.add(priority, tokens[1]);
            System.out.println("added popup.");
        } else {
            notificationStack.addUrgent("ERROR", MISSING_ARG.apply("popup"));
        }
    }
}
