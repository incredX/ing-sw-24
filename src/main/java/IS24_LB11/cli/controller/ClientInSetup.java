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
        switch (serverEvent) {
            case ServerNotificationEvent notificationEvent -> {
                popUpStack.addPopUp(Priority.LOW, "from server", notificationEvent.message());
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
                popUpStack.addPopUp(Priority.LOW, "received updated players' state");
            }
            case ServerHeartBeatEvent heartBeatEvent -> {
                sendToServer("heartbeat");
            }
            case ServerPlayerSetupEvent playerSetupEvent -> {
                processResult(Result.Error("Invalid server event", "can't accept a new player setup"));
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
            case "SENDTO", "@" -> {
                if (tokens.length == 2) processCommandSendto(tokens[1]);
                else popUpStack.addUrgentPopUp("ERROR", "missing argument");
            }
            case "SENDTOALL", "@ALL" -> {
                if (tokens.length == 2) processCommandSendtoall(tokens[1]);
                else popUpStack.addUrgentPopUp("ERROR", "missing argument");
            }
            case "GOAL", "G" -> {
                try {
                    int index = Integer.parseInt(tokens[1]);
                    setChosenGoal(index);
                }
                catch (NumberFormatException e) {
                    popUpStack.addUrgentPopUp("ERROR", "invalid index (expected an integer)");
                }
            }
            case "READY" -> {
                sendToServer("setup",
                        new String[]{"startercard","goal"},
                        new String[]{setup.starterCard().asString(), setup.chosenGoal().asString()});
                //switch to CLientInGame & inform the server
            }
            default -> popUpStack.addUrgentPopUp("ERROR", tokens[0]+" is not a valid command");
        };
    }

    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.isCtrlDown()) {
            if (keyStroke.getKeyType() == KeyType.ArrowUp) {
                setChosenGoal(0);
            } else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
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
