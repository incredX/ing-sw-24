package IS24_LB11.cli.controller;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.event.*;
import IS24_LB11.game.Board;
import IS24_LB11.game.Result;
import com.google.gson.JsonElement;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;

public class ClientInLobby extends ClientState {
    private String name;

    public ClientInLobby(Board board, ViewHub hub) throws IOException {
        super(board, hub);
        this.name = "";
    }

    protected void handleEvent(Event event) {
        switch (event) {
            case CommandEvent commandEvent -> processCommand(commandEvent.command());
            case KeyboardEvent keyboardEvent -> processKeyStroke(keyboardEvent.keyStroke());
            case ResizeEvent resizeEvent -> processResize(resizeEvent.size());
            case ResultEvent resultEvent -> processResult(resultEvent.result());
            default -> System.out.println("Unknown event: " + event.getClass().getName());
        };
    }

    private void processResult(Result<JsonElement> result) {
        if (result.isError()) {
            System.out.println("ERROR: " + result.getError());
            if (result.getCause() != null) System.out.println("CAUSE: " + result.getCause());
        } else
            System.out.println("OK: " + result.toString());
    }

    private void processCommand(String command) {
        if (command.equalsIgnoreCase("quit")) {
            quit();
            return;
        }
        System.out.println("command: \"" + command + "\"");
        String[] tokens = command.split(" ", 3);
        if (tokens[0].equalsIgnoreCase("popup")) {
            hub.addPopUp(tokens[2], tokens[1]);
            popUpOn = true;
        }
    }

    private void processKeyStroke(KeyStroke keyStroke) {
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
                if (popUpOn) {
                    popUpOn = false;
                    hub.removePopUp();
                    hub.update();
                } else quit();
            default:
                break;
        }
        hub.updateCommandLine(cmdLine);
    }

    private void quit() {
        setNextState(null);
    }
}
