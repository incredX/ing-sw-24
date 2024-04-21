package IS24_LB11.cli.controller;

import IS24_LB11.cli.popup.PopUp;
import IS24_LB11.cli.popup.Priority;
import IS24_LB11.cli.view.ViewHub;
import IS24_LB11.cli.event.*;
import IS24_LB11.cli.KeyConsumer;
import IS24_LB11.game.Board;
import IS24_LB11.game.Result;
import com.google.gson.JsonObject;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;

public class ClientInLobby extends ClientState {
    private String name;

    public ClientInLobby(Board board, ViewHub hub) throws IOException {
        super(board, hub);
        this.name = "";
    }

    @Override
    public ClientState execute() {
        hub.setLobbyStage();
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
            case ServerPlayerSetupEvent playerSetupEvent -> {
                popUpStack.addPopUp(Priority.LOW, "received player setup");
                //TODO: switch state to CLientInSetup
            }
            case ServerHeartBeatEvent heartBeatEvent -> {
                JsonObject response = new JsonObject();
                response.addProperty("type", "heartbeat");
                serverHandler.write(response);
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

    protected void processKeyStroke(KeyStroke keyStroke) {
        for (KeyConsumer listener: keyConsumers) {
            // if a listener use the keyStroke then no one with less priority will
            if (listener.consumeKeyStroke(keyStroke)) return;
        }
        switch (keyStroke.getKeyType()) {
            case Character:
                cmdLine.insertChar(keyStroke.getCharacter());
                break;
            case Backspace:
                cmdLine.deleteChar();
                break;
            case Enter:
                tryQueueEvent(new CommandEvent(cmdLine.getFullLine()));
                cmdLine.clearLine();
                break;
            case ArrowUp:
                break;
            case ArrowDown:
                break;
            case ArrowLeft:
                cmdLine.moveCursor(-1);
                break;
            case ArrowRight:
                cmdLine.moveCursor(1);
                break;
            case Escape:
                quit();
            default:
                break;
        }
        hub.updateCommandLine(cmdLine);
    }

    private void processCommandLogin(String username) {
        sendToServer("login", "username", username);
    }

    private void processCommandSendto(String argument) {
        String[] tokens = argument.split(" ", 2);
        if (tokens.length == 2) {
            String[] fields = new String[] {"from", "to", "message"};
            String[] values = new String[] {"", tokens[0], tokens[1]};
            sendToServer("message", fields, values);
        } else {
            popUpStack.addUrgentPopUp("ERROR", "syntax error in given commmand");
        }
    }

    private void processCommandSendtoall(String message) {
        String[] fields = new String[] {"from", "to", "message"};
        String[] values = new String[] {"", "", message};
        sendToServer("message", fields, values);
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

    private void quit() {
        setNextState(null);
        Thread.currentThread().interrupt();
    }
}
