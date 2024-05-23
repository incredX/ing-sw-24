package IS24_LB11.cli.controller;

import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.Scoreboard;
import IS24_LB11.cli.Table;
import IS24_LB11.cli.event.server.ServerEvent;
import IS24_LB11.cli.event.server.ServerPlayerDisconnectEvent;
import IS24_LB11.cli.event.server.ServerPlayerSetupEvent;
import IS24_LB11.cli.popup.ChatPopup;
import IS24_LB11.cli.popup.DecksPopup;
import IS24_LB11.cli.popup.HandPopup;
import IS24_LB11.cli.popup.TablePopup;
import IS24_LB11.cli.view.stage.SetupStage;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.Result;
import IS24_LB11.game.components.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The SetupState class represents the state of the client during the game setup phase.
 * It handles server events, user commands, and various input methods.
 */
public class SetupState extends ClientState implements PlayerStateInterface {
    private final PlayerSetup setup;
    private Table table;
    private SetupStage setupStage;

    /**
     * Constructs a new SetupState with the specified LobbyState, PlayerSetup, and Table.
     *
     * @param lobbyState the previous lobby state.
     * @param setup the player setup details.
     * @param table the table instance for the game.
     */
    public SetupState(LobbyState lobbyState, PlayerSetup setup, Table table) {
        super(lobbyState);
        this.setup = setup;
        this.table = table;
        popManager.addPopup(
                new ChatPopup(getViewHub(), this),
                new TablePopup(getViewHub(), this),
                new HandPopup(getViewHub(), this),
                new DecksPopup(getViewHub(), this));
    }

    /**
     * Constructs a new SetupState with the specified ViewHub, PlayerSetup, and Table.
     *
     * @param viewHub the view hub used for managing views and stages.
     * @param setup the player setup details.
     * @param table the table instance for the game.
     * @throws IOException if an I/O error occurs.
     */
    public SetupState(ViewHub viewHub, PlayerSetup setup, Table table) throws IOException {
        super(viewHub);
        this.setup = setup;
        this.table = table;
    }

    /**
     * Executes the setup state after setting up the setup stage and updating necessary components.
     *
     * @return the next client state to be executed.
     */
    @Override
    public ClientState execute() {
        setupStage = viewHub.setSetupStage(this);
        popManager.updatePopups();
        viewHub.update();
        return super.execute();
    }

    /**
     * Processes the event when the server goes down.
     */
    @Override
    protected void processServerDown() {
        notificationStack.removeAllNotifications();
        popManager.hideAllPopups();
        serverHandler.shutdown();
        super.processServerDown();
        setNextState(new LobbyState(viewHub));
    }

    /**
     * Processes the received server event.
     *
     * @param serverEvent the server event to be processed.
     */
    @Override
    protected void processServerEvent(ServerEvent serverEvent) {
        if (processServerEventIfCommon(serverEvent)) return;
        switch (serverEvent) {
            case ServerPlayerSetupEvent playerSetupEvent -> {
                processResult(Result.Error("Invalid server event", "can't accept a new player setup"));
            }
            case ServerPlayerDisconnectEvent disconnectEvent -> {
                table.getScoreboard().removePlayer(disconnectEvent.player());
            }
            default -> processResult(Result.Error("received unknown server event"));
        }
    }

    /**
     * Processes the received command.
     *
     * @param command the command to be processed.
     */
    @Override
    protected void processCommand(String command) {
        if (processCommandIfCommon(command)) return;
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
                //notificationStack.removeNotifications(LOW);
                setupStage.clear();
                setNextState(new GameState(this));
            }
            case "LOGOUT" -> logout();
            default -> notificationStack.addUrgent("ERROR", INVALID_CMD.apply(tokens[0], "game setup"));
        };
    }

    /**
     * Processes the received keystroke.
     *
     * @param keyStroke the keystroke to be processed.
     */
    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        keyConsumed = notificationStack.consumeKeyStroke(keyStroke);
        if (!keyConsumed) cmdLine.consumeKeyStroke(this, keyStroke);
        if (!keyConsumed) popManager.consumeKeyStroke(keyStroke);
        if (!keyConsumed && (!cmdLine.isEnabled() || keyStroke.isCtrlDown() || keyStroke.isAltDown())) {
            switch (keyStroke.getKeyType()) {
                case ArrowLeft -> setChosenGoal(0);
                case ArrowRight -> setChosenGoal(1);
                case Character -> {
                    if (keyStroke.getCharacter() == 'f') {
                        setup.getStarterCard().flip();
                        setupStage.loadStarterCard();
                        setupStage.placeStarterCard();
                        setupStage.drawAll();
                    }

                }
            }
        }
    }

    /**
     * Processes the screen resize event.
     *
     * @param screenSize the new size of the screen.
     */
    @Override
    protected void processResize(TerminalSize screenSize) {
        super.processResize(screenSize);
        popManager.resizePopups();
    }

    /**
     * Close connection with the server (if still open) and return to the lobby.
     */
    public void logout() {
        Debugger.print("logging out");
        sendToServer("quit");
        popManager.hideAllPopups();
        notificationStack.removeAllNotifications();
        serverHandler.shutdown();
        setNextState(new LobbyState(viewHub));
    }

    /**
     * Sets the chosen goal based on the specified index.
     *
     * @param index the index of the chosen goal.
     */
    private void setChosenGoal(int index) {
        setup.chooseGoal(index);
        setupStage.setChosenGoal(index);
        viewHub.update();
    }

    /**
     * Gets the player setup.
     *
     * @return the player setup.
     */
    public PlayerSetup getSetup() {
        return setup;
    }

    /**
     * Gets the table.
     *
     * @return the table.
     */
    public Table getTable() {
        return table;
    }

    /**
     * Gets the starter card.
     *
     * @return the starter card.
     */
    public StarterCard getStarterCard() {
        return setup.getStarterCard();
    }

    /**
     * Gets the possible private goals.
     *
     * @return an array of possible private goal cards.
     */
    public GoalCard[] getPossiblePrivateGoals() {
        return setup.getGoals();
    }

    /**
     * Gets the player's hand.
     *
     * @return an array list of playable cards in the player's hand.
     */
    public ArrayList<PlayableCard> getPlayerHand() {
        return setup.hand();
    }

    /**
     * Gets the scoreboard.
     *
     * @return the scoreboard.
     */
    public Scoreboard getScoreboard() {
        return table.getScoreboard();
    }

    /**
     * Gets the public goals.
     *
     * @return an array list of public goal cards.
     */
    public ArrayList<GoalCard> getGoals() {
        return table.getPublicGoals();
    }

    /**
     * Gets the normal deck.
     *
     * @return an array list of normal cards.
     */
    public ArrayList<NormalCard> getNormalDeck() {
        return table.getNormalDeck();
    }

    /**
     * Gets the golden deck.
     *
     * @return an array list of golden cards.
     */
    public ArrayList<GoldenCard> getGoldenDeck() {
        return table.getGoldenDeck();
    }
}
