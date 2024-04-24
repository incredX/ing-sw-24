package IS24_LB11.cli.controller;

import IS24_LB11.cli.notification.Priority;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.event.*;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.Result;
import IS24_LB11.game.components.*;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.SyntaxException;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientInLobby extends ClientState {

    public ClientInLobby(ViewHub hub) throws IOException {
        super(hub);
        this.username = "";
    }

    @Override
    public ClientState execute() {
        viewHub.setLobbyStage();
        return super.execute();
    }

    protected void processServerEvent(ServerEvent serverEvent) {
        if (processServerEventIfCommon(serverEvent)) return;
        switch (serverEvent) {
            case ServerLoginEvent loginEvent -> {
                username = loginEvent.username();
            }
            case ServerUpdateEvent updateEvent -> {
                notificationStack.add(Priority.LOW, "received updated board of"+updateEvent.getUsername());
            }
            case ServerPlayerSetupEvent setupEvent -> {
                notificationStack.add(Priority.LOW, "received player setup");
                try { setNextState(new ClientInSetup(viewHub, setupEvent.getPlayerSetup())); }
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
            case "POPUP" -> {
                if (tokens.length == 2) processCommandPopup(tokens[1]);
                else notificationStack.addUrgent("ERROR", "missing argument");
            }
            case "SETUP" -> {
                stage.clear();
                try { setNextState(new ClientInSetup(viewHub, getDefaultSetup())); } // wait server response to switch
                catch (IOException e) {
                    e.printStackTrace();
                    quit();
                }
            }
            default -> notificationStack.addUrgent("ERROR", tokens[0]+" is not a valid command");
        };
    }

    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        if (notificationStack.consumeKeyStroke(keyStroke)) return;
        super.processCommonKeyStrokes(keyStroke);
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

    private static PlayerSetup getDefaultSetup() {
        try {
            PlayableCard[] hand = new PlayableCard[] {
                    new GoldenCard("_EEQFF1QFFA__"), new NormalCard("FEF_FF0"), new NormalCard("EA_AAF0")
            };
            return new PlayerSetup(new StarterCard("EPIE_F0I__FPIA"),
                    new GoalCard[]{new GoalSymbol("2KK_"), new GoalPattern("3IPPL2")},
                    new ArrayList<>(List.of(hand)),
                    Color.RED);
        } catch (SyntaxException e) { return null; }
    }
}
