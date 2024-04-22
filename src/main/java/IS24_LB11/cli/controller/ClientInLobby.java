package IS24_LB11.cli.controller;

import IS24_LB11.cli.popup.Priority;
import IS24_LB11.cli.view.ViewHub;
import IS24_LB11.cli.event.*;
import IS24_LB11.game.Result;
import com.google.gson.JsonObject;

import java.io.IOException;

public class ClientInLobby extends ClientState {
    private String name;

    public ClientInLobby(ViewHub hub) throws IOException {
        super(hub);
        this.name = "";
    }

    @Override
    public ClientState execute() {
        viewHub.setLobbyStage();
        return super.execute();
    }

    protected void processServerEvent(ServerEvent serverEvent) {
        switch (serverEvent) {
            case ServerOkEvent okEvent -> {
                if (!okEvent.message().isEmpty())
                    popUpStack.addPopUp(Priority.LOW, okEvent.message());
            }
            case ServerMessageEvent messageEvent -> {
                String text;
                if (messageEvent.to().isEmpty())
                    text = String.format("%s @ all : %s", messageEvent.from(), messageEvent.message());
                else
                    text = String.format("%s : %s", messageEvent.from(), messageEvent.message());
                popUpStack.addPopUp(Priority.MEDIUM, text);
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
            case ServerHeartBeatEvent heartBeatEvent -> {
                sendToServer("heartbeat");
            }
            default -> processResult(Result.Error("received unknown server event"));
        }
    }

    protected void processCommand(String command) {
        String[] tokens = command.split(" ", 2);
        if (tokens.length == 0) return;
        //TODO: (inGame) command center set pointer in (0,0)
        switch (tokens[0].toUpperCase()) {
            case "QUIT" -> quit();
            case "LOGIN" -> {
                if (tokens.length == 2) processCommandLogin(tokens[1]);
                else popUpStack.addUrgentPopUp("ERROR", "missing argument");
            }
            case "SENDTO", "@" -> {
                if (tokens.length == 2) processCommandSendto(tokens[1]);
                else popUpStack.addUrgentPopUp("ERROR", "missing argument");
            }
            case "SENDTOALL", "@ALL" -> {
                if (tokens.length == 2) processCommandSendtoall(tokens[1]);
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
