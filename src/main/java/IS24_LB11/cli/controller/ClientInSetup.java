package IS24_LB11.cli.controller;

import IS24_LB11.cli.SetupStage;
import IS24_LB11.cli.event.*;
import IS24_LB11.cli.popup.Priority;
import IS24_LB11.cli.view.ViewHub;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.Result;
import com.google.gson.JsonObject;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.awt.event.KeyEvent;
import java.io.IOException;

public class ClientInSetup extends ClientState {
    private final PlayerSetup setup;
    private SetupStage stage;

    public ClientInSetup(ViewHub viewHub, PlayerSetup setup) throws IOException {
        super(viewHub);
        this.setup = setup;
    }

    @Override
    public ClientState execute() {
        stage = viewHub.setSetupStage(setup);
        return super.execute();
    }

    protected void processServerEvent(ServerEvent serverEvent) {
        if (processCommonServerEvent(serverEvent)) return;
        switch (serverEvent) {
            case ServerUpdateEvent updateEvent -> {
                popUpStack.addPopUp(Priority.LOW, "received updated players' state");
            }
            case ServerPlayerSetupEvent playerSetupEvent -> {
                processResult(Result.Error("Invalid server event", "can't accept a new player setup"));
            }
            default -> processResult(Result.Error("received unknown server event"));
        }
    }

    protected void processCommand(String command) {
        if (processCommonCommand(command)) return;
        String[] tokens = command.split(" ", 2);
        switch (tokens[0].toUpperCase()) {
            case "GOAL", "G" -> {
                if (tokens.length == 2) {
                    popUpStack.addUrgentPopUp("ERROR", "missing argument");
                    return;
                }
                int index = 'a' - tokens[1].charAt(0);
                if (index < 0 || index > 1 || tokens[1].length() > 1)
                    popUpStack.addUrgentPopUp("ERROR",
                            "command \"GOAL\" expects 'a' or 'b' as argument, "+tokens[1]+"was given");
                else
                    setChosenGoal(index);
            }
            case "READY" -> {
                sendToServer("setup",
                        new String[]{"startercard","chosengoal"},
                        new String[]{setup.starterCard().asString(), setup.chosenGoal().asString()});
                try { setNextState(new ClientInGame(viewHub, setup)); }
                catch (IOException e) {
                    e.printStackTrace();
                    quit();
                }
            }
            default -> popUpStack.addUrgentPopUp("ERROR", tokens[0]+" is not a valid command");
        };
    }

    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.isCtrlDown()) {
            if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
                setChosenGoal(0);
            } else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
                setChosenGoal(1);
            } else if (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == 'f') {
                setup.starterCard().flip();
                stage.buildStarterCard(setup.starterCard());
            }
        } else {
            super.processKeyStroke(keyStroke);
        }
    }

    private void setChosenGoal(int index) {
        setup.choseGoal(index);
        stage.setChosenGoal(index);
        viewHub.update();
    }
}
