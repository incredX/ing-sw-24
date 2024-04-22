package IS24_LB11.cli.controller;

import IS24_LB11.cli.popup.Priority;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.event.*;
import IS24_LB11.game.Result;

import java.io.IOException;

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
                popUpStack.addPopUp(Priority.LOW, "received updated board of"+updateEvent.getUsername());
            }
            case ServerPlayerSetupEvent setupEvent -> {
                popUpStack.addPopUp(Priority.LOW, "received player setup");
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
                else popUpStack.addUrgentPopUp("ERROR", "missing argument");
            }
            case "POPUP" -> {
                if (tokens.length == 2) processCommandPopup(tokens[1]);
                else popUpStack.addUrgentPopUp("ERROR", "missing argument");
            }
            default -> popUpStack.addUrgentPopUp("ERROR", tokens[0]+" is not a valid command");
        };
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
            popUpStack.addUrgentPopUp("ERROR", tokens[0]+" is not a valid priority");
            return;
        }
        if (tokens.length >= 3) {
            popUpStack.addPopUp(priority, tokens[1], tokens[2]);
            System.out.println("added popup.");
        } else if (tokens.length == 2) {
            popUpStack.addPopUp(priority, tokens[1]);
            System.out.println("added popup.");
        }
    }
}
