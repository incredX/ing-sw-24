package IS24_LB11.cli.controller;

import IS24_LB11.cli.Scoreboard;
import IS24_LB11.cli.Table;
import IS24_LB11.cli.event.server.ServerEvent;
import IS24_LB11.cli.event.server.ServerPlayerSetupEvent;
import IS24_LB11.cli.event.server.ServerUpdatePlayerBoardEvent;
import IS24_LB11.cli.popup.Popup;
import IS24_LB11.cli.popup.PopupManager;
import IS24_LB11.cli.popup.TablePopup;
import IS24_LB11.cli.view.stage.SetupStage;
import IS24_LB11.cli.notification.NotificationStack;
import IS24_LB11.cli.notification.Priority;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.Result;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;
import java.util.ArrayList;

public class SetupState extends ClientState {
    private final PlayerSetup setup;
    private Table table;
    private PopupManager popManager;
    private SetupStage setupStage;

    public SetupState(LobbyState lobbyState, PlayerSetup setup, Table table) throws IOException {
        super(lobbyState);
        this.popManager = new PopupManager(new Popup[]{new TablePopup(getViewHub(), this)});
        this.setup = setup;
        this.table = table;
    }

    public SetupState(ViewHub viewHub, NotificationStack stack, PlayerSetup setup, Table table) throws IOException {
        super(viewHub, stack);
        this.setup = setup;
        this.table = table;
    }

    public SetupState(ViewHub viewHub, PlayerSetup setup, Table table) throws IOException {
        super(viewHub);
        this.setup = setup;
        this.table = table;
    }

    @Override
    public ClientState execute() {
        setupStage = viewHub.setSetupStage(this);
        popManager.updatePopups();
        viewHub.resize(viewHub.getScreenSize(), cmdLine);
        return super.execute();
    }

    protected void processServerEvent(ServerEvent serverEvent) {
        if (processServerEventIfCommon(serverEvent)) {
            viewHub.update();
            return;
        }
        switch (serverEvent) {
            case ServerUpdatePlayerBoardEvent updateEvent -> {
                notificationStack.add(Priority.LOW, "received updated players' state");
            }
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
                try { setNextState(new GameState(this)); }
                catch (IOException e) {
                    e.printStackTrace();
                    quit();
                }
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

    public ArrayList<PlayableCard> getHand() {
        return setup.hand();
    }

    public GoalCard[] getPossiblePrivateGoals() {
        return setup.getGoals();
    }

    public Scoreboard getScoreboard() {
        return table.getScoreboard();
    }

    public ArrayList<GoalCard> getGoals() {
        return table.getPublicGoals();
    }
}
