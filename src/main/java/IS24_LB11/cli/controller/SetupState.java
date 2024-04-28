package IS24_LB11.cli.controller;

import IS24_LB11.cli.Scoreboard;
import IS24_LB11.cli.Table;
import IS24_LB11.cli.event.server.ServerEvent;
import IS24_LB11.cli.event.server.ServerPlayerSetupEvent;
import IS24_LB11.cli.popup.DecksPopup;
import IS24_LB11.cli.popup.HandPopup;
import IS24_LB11.cli.popup.Popup;
import IS24_LB11.cli.popup.TablePopup;
import IS24_LB11.cli.view.stage.SetupStage;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.Result;
import IS24_LB11.game.components.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.ArrayList;

public class SetupState extends ClientState implements PlayerStateInterface {
    private final PlayerSetup setup;
    private Table table;
    private SetupStage setupStage;

    public SetupState(LobbyState lobbyState, PlayerSetup setup, Table table) {
        super(lobbyState);
        this.setup = setup;
        this.table = table;
        popManager.addPopup(new Popup[]{
                new TablePopup(getViewHub(), this),
                new HandPopup(getViewHub(), this),
                new DecksPopup(getViewHub(), this)
        });
    }

    public SetupState(ViewHub viewHub, PlayerSetup setup, Table table) {
        super(viewHub);
        this.setup = setup;
        this.table = table;
    }

    @Override
    public ClientState execute() {
        setupStage = viewHub.setSetupStage(this);
        popManager.updatePopups();
        viewHub.update();
        return super.execute();
    }

    protected void processServerEvent(ServerEvent serverEvent) {
        if (processServerEventIfCommon(serverEvent)) {
            viewHub.update();
            return;
        }
        switch (serverEvent) {
            case ServerPlayerSetupEvent playerSetupEvent -> {
                processResult(Result.Error("Invalid server event", "can't accept a new player setup"));
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
            case "GOAL", "G" -> {
                if (tokens.length < 2) {
                    notificationStack.addUrgent("ERROR", MISSING_ARG.apply("goal"));
                    return;
                }
                int index = 'a' - tokens[1].charAt(0);
                if (index < 0 || index > 1 || tokens[1].length() > 1)
                    notificationStack.addUrgent("ERROR",
                            "command \"GOAL\" expects 'a' or 'b' as argument, "+tokens[1]+"was given");
                else
                    setChosenGoal(index);
            }
            case "READY" -> {
                sendToServer("setup",
                        new String[]{"starterCard","goalCard"},
                        new String[]{setup.getStarterCard().asString(), setup.chosenGoal().asString()});
                setupStage.clear();
                setNextState(new GameState(this));
            }
            case "SHOW" -> {
                if (tokens.length == 2) popManager.showPopup(tokens[1]);
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply("show"));
            }
            case "HIDE" ->  {
                if (tokens.length == 2) popManager.hidePopup(tokens[1]);
                else popManager.hideFocusedPopup();
            }
            case "TABLE" -> popManager.showPopup(tokens[0]);
            default -> notificationStack.addUrgent("ERROR", INVALID_CMD.apply(tokens[0], "game setup"));
        };
        viewHub.update();
    }

    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        if (notificationStack.consumeKeyStroke(keyStroke)) {
            viewHub.update();
            return;
        }
        popManager.consumeKeyStroke(keyStroke);

        if (keyStroke.isCtrlDown()) {
            if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
                setChosenGoal(0);
            } else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
                setChosenGoal(1);
            } else if (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == 'f') {
                setup.getStarterCard().flip();
                setupStage.loadStarterCard();
                setupStage.placeStarterCard();
                setupStage.drawAll();
            }
        } else {
            super.processCommonKeyStrokes(keyStroke);
        }
        viewHub.updateCommandLine(cmdLine);
    }

    @Override
    protected void processResize(TerminalSize size) {
        super.processResize(size);
        popManager.resizePopups();
        viewHub.updateStage();
    }

    private void setChosenGoal(int index) {
        setup.chooseGoal(index);
        setupStage.setChosenGoal(index);
        viewHub.update();
    }

    public PlayerSetup getSetup() {
        return setup;
    }

    public Table getTable() {
        return table;
    }

    public StarterCard getStarterCard() {
        return setup.getStarterCard();
    }

    public GoalCard[] getPossiblePrivateGoals() {
        return setup.getGoals();
    }

    public ArrayList<PlayableCard> getPlayerHand() {
        return setup.hand();
    }

    public Scoreboard getScoreboard() {
        return table.getScoreboard();
    }

    public ArrayList<GoalCard> getGoals() {
        return table.getPublicGoals();
    }

    public ArrayList<NormalCard> getNormalDeck() {
        return table.getNormalDeck();
    }

    public ArrayList<GoldenCard> getGoldenDeck() {
        return table.getGoldenDeck();
    }
}
