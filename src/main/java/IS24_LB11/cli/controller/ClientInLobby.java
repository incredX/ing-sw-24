package IS24_LB11.cli.controller;

import IS24_LB11.cli.popup.PopUp;
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

    protected void processServerEvent(ServerEvent serverEvent) {
        switch (serverEvent) {
            case ServerOkEvent okEvent -> {
                if (!okEvent.message().isEmpty())
                    popUpQueue.addPopUp(new PopUp(16, okEvent.message()));
            }
            case ServerMessageEvent messageEvent -> {
                String text = "from "+messageEvent.from()+":\n"+messageEvent.message();
                popUpQueue.addPopUp(new PopUp(8, "new message", text));
            }
            case ServerUpdateEvent updateEvent -> {
                popUpQueue.addPopUp(new PopUp(24, "received updated board of"+updateEvent.getUsername()));
            }
            case ServerPlayerSetupEvent playerSetupEvent -> {
                popUpQueue.addPopUp(new PopUp(16, "received player setup"));
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
                if (tokens.length >= 2) {
                    JsonObject object = new JsonObject();
                    JsonObject data = new JsonObject();
                    data.addProperty("username", tokens[1]);
                    object.addProperty("type", "LOGIN");
                    object.add("data", data);
                    if (serverHandler != null)
                        serverHandler.write(object);
                }
            }
            case "POPUP" -> {
                tokens = tokens[1].split(" ", 3);
                System.out.print("POPUP: ");
                for (String token: tokens) System.out.print(token + ", ");
                System.out.print("\n");
                int priority;
                try { priority = Integer.parseInt(tokens[0]); }
                catch (NumberFormatException e) { priority = 10; }
                if (tokens.length >= 3) {
                    popUpQueue.addPopUp(new PopUp(priority, tokens[1], tokens[2]));
                    System.out.println("added popup.");
                } else if (tokens.length == 2) {
                    popUpQueue.addPopUp(new PopUp(priority, tokens[1]));
                    System.out.println("added popup.");
                }
            }
            default -> tryQueueEvent(new ResultServerEvent(Result.Error("invalid input")));
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

    private void quit() {
        setNextState(null);
        Thread.currentThread().interrupt();
    }
}
