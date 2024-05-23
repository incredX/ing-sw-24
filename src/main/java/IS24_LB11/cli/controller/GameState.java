package IS24_LB11.cli.controller;

import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.Scoreboard;
import IS24_LB11.cli.Table;
import IS24_LB11.cli.event.server.ServerNewTurnEvent;
import IS24_LB11.cli.event.server.ServerPlayerDisconnectEvent;
import IS24_LB11.cli.notification.NotificationStack;
import IS24_LB11.cli.notification.Priority;
import IS24_LB11.cli.popup.*;
import IS24_LB11.cli.view.stage.GameStage;
import IS24_LB11.cli.event.server.ServerEvent;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.utils.Side;
import IS24_LB11.game.*;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.Position;
import com.google.gson.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static IS24_LB11.cli.notification.Priority.LOW;
import static IS24_LB11.cli.notification.Priority.MEDIUM;

/**
 * The {@code GameState} class represents the state of the game from the client's perspective.
 * It handles the game's logic, player interactions, and communication with the server.
 */
public class GameState extends ClientState implements PlayerStateInterface {
    private final Player player;
    private Table table;
    private GameStage gameStage;
    private Position boardPointer;
    private PlacedCard placedCard;
    private boolean cardPlaced = false;
    private boolean cardPicked = false;
    private boolean playerTurn = false;
    private boolean gameOver = false;

    /**
     * Constructs a new {@code GameState} object from a setup state.
     *
     * @param setupState the setup state
     */
    public GameState(SetupState setupState) {
        super(setupState);
        this.player = new Player(username, setupState.getSetup());
        this.table = setupState.getTable();
        this.boardPointer = new Position(0, 0);
        this.placedCard = null;
        popManager.forEachPopup(popup -> popup.setPlayerState(this));
        popManager.addPopup(new SymbolsPopup(getViewHub(), this));
    }

    /**
     * Constructs a new {@code GameState} object from an automated state.
     *
     * @param automatedState the automated state
     */
    public GameState(AutomatedState automatedState) {
        super(automatedState);
        this.player = automatedState.getPlayer();
        this.table = automatedState.getTable();
        this.boardPointer = new Position(0, 0);
        this.placedCard = null;
        this.popManager.addPopup(
                new HelpPoup(getViewHub(), this),
                new ChatPopup(getViewHub(), this),
                new TablePopup(getViewHub(), this),
                new HandPopup(getViewHub(), this),
                new DecksPopup(getViewHub(), this),
                new SymbolsPopup(getViewHub(), this)
        );
    }

    /**
     * Constructs a new {@code GameState} object with the specified parameters.
     *
     * @param viewHub the view hub
     * @param stack the notification stack
     * @param setup the player setup
     * @param table the game table
     * @throws IOException if an I/O error occurs
     */
    public GameState(ViewHub viewHub, NotificationStack stack, PlayerSetup setup, Table table) {
        super(viewHub, stack);
        this.player = new Player(username, setup);
        this.table = table;
        this.boardPointer = new Position(0, 0);
        this.placedCard = null;
    }

    /**
     * Executes the game state after settig up the stage and popups.
     *
     * @return the next client state to be executed.
     */
    @Override
    public ClientState execute() {
        if (getPlacedCardsInBoard().isEmpty()) player.applySetup();
        gameStage = viewHub.setGameStage(this);
        updateBoardPointerImage();
        popManager.updatePopups();
        popManager.getPopup("table").redrawView();
        if (table.getScoreboard().getNumPlayers() == 1) gameOver = true;
        cmdLine.update();
        viewHub.update();
        return super.execute();
    }

    @Override
    protected void processServerDown() {
        notificationStack.removeAllNotifications();
        popManager.hideAllPopups();
        serverHandler.shutdown();
        super.processServerDown();
        setNextState(new LobbyState(viewHub));
    }

    /**
     * Processes the received server event based one the type of server event.
     *
     * @param serverEvent the server event to be processed.
     */
    @Override
    protected void processServerEvent(ServerEvent serverEvent) {
        if (processServerEventIfCommon(serverEvent)) return;
        switch (serverEvent) {
            case ServerNewTurnEvent newTurnEvent -> {
                Debugger.print("turn of " + newTurnEvent.player() + " (I'm " + username + ")");
                if (newTurnEvent.player().isEmpty()) {
                    if (newTurnEvent.endOfGame()) {
                        gameOver = true;
                    }
                    popManager.hideAllPopups();
                    popManager.showPopup("table");
                    popManager.getPopup("table").enable();
                    notificationStack.add(MEDIUM, "GAME ENDED", "press [ENTER] to go back to the lobby");
                }
                if (newTurnEvent.player().equals(username)) {
                    cardPlaced = false;
                    cardPicked = false;
                    playerTurn = true;
                }
                updateBoardPointerImage();
                gameStage.updatePointer();
                table.update(newTurnEvent);
                popManager.getOptionalPopup("decks").ifPresent(Popup::update);
                popManager.getOptionalPopup("table").ifPresent(Popup::update);
            }
            case ServerPlayerDisconnectEvent disconnectEvent -> {
                if (gameOver) break;
                table.getScoreboard().removePlayer(disconnectEvent.player());
                popManager.getPopup("table").update();
                popManager.getPopup("table").redrawView();
                if (table.getScoreboard().getNumPlayers() == 1) gameOver = true;
            }
            default -> processResult(Result.Error("received unknown server event"));
        }
    }

    /**
     * Processes .
     *
     * @param serverEvent the server event to be processed.
     */
    @Override
    protected void processCommand(String command) {
        if (processCommandIfCommon(command)) return;
        Debugger.print("command: " + command);
        String[] tokens = command.split(" ", 2);
        switch (tokens[0].toUpperCase()) {
            case "HOME" -> {
                centerBoardPointer();
                gameStage.centerBoard();
                gameStage.updateBoard();
            }
            case "LOGOUT" -> logout();
            default -> {
                notificationStack.addUrgent("ERROR", INVALID_CMD.apply(tokens[0], "game"));
            }
        }
    }

    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        keyConsumed = notificationStack.consumeKeyStroke(keyStroke);
        if (!keyConsumed) cmdLine.consumeKeyStroke(this, keyStroke);
        if (!keyConsumed) popManager.consumeKeyStroke(keyStroke);
        if (!(cmdLine.isEnabled() || keyConsumed)) {
            switch (keyStroke.getKeyType()) {
                case ArrowUp -> shiftBoardPointer(Side.NORD);
                case ArrowDown -> shiftBoardPointer(Side.SUD);
                case ArrowLeft -> shiftBoardPointer(Side.WEST);
                case ArrowRight -> shiftBoardPointer(Side.EAST);
                case Enter -> {
                    System.out.println("game over: " + gameOver);
                    if (gameOver) logout();
                }
            }
        }
    }

    @Override
    protected void processResize(TerminalSize screenSize) {
        centerBoardPointer();
        super.processResize(screenSize);
        popManager.resizePopups();
    }

    /**
     * Draws a card from the deck.
     */
    public void drawCardFromDeck() {
        JsonConverter converter = new JsonConverter();
        if (cardPicked || !cardPlaced || !playerTurn)
            return;
        DecksPopup decksPopup = (DecksPopup) popManager.getPopup("decks");
        HandPopup handPopup = (HandPopup) popManager.getPopup("hand");
        player.addCardToHand(decksPopup.getSelectedCard());
        handPopup.update();
        popManager.hidePopup("decks");
        cardPicked = true;
        playerTurn = false;
        try {
            JsonObject jsonPlacedCard = (JsonObject) new JsonParser().parse(converter.objectToJSON(placedCard));
            JsonElement jsonDeckType = new JsonPrimitive(!decksPopup.selectedNormalDeck());
            JsonElement jsonCardIndex = new JsonPrimitive(decksPopup.getCardIndex() + 1);
            sendToServer("turnActions", new String[]{"placedCard", "deckType", "indexVisibleCards"},
                    new JsonElement[]{jsonPlacedCard, jsonDeckType, jsonCardIndex});
        } catch (JsonException e) {
            Debugger.print(e);
        }
        notificationStack.removeNotifications(Priority.LOW);
    }

    /**
     * Places a card from the hand onto the board.
     */
    public void placeCardFromHand() {
        HandPopup handPopup = (HandPopup) popManager.getPopup("hand");
        if (!playerTurn) {
            notificationStack.addUrgent("WARNING", "can't place cards outside of turn");
            return;
        }
        if (cardPlaced) {
            notificationStack.addUrgent("WARNING", "a card has already been placed in this turn");
            return;
        }
        PlayableCard selectedCard = handPopup.getSelectedCard();
        if (player.placeCard(selectedCard, boardPointer)) {
            placedCard = new PlacedCard(selectedCard, boardPointer);
            cardPlaced = true;
            updateBoardPointerImage();
            gameStage.updateBoard();
            popManager.getPopup("symbols").update();
        } else {
            notificationStack.addUrgent("WARNING", "cannot place card");
        }
    }

    /**
     * Close connection with the server (if still open) and return to the lobby.
     */
    public void logout() {
        Debugger.print("logging out");
        sendToServer("quit");
        popManager.hideAllPopups();
        notificationStack.removeNotifications(LOW);
        serverHandler.shutdown();
        setNextState(new LobbyState(viewHub));
    }

    /**
     * Centers the board pointer to the starter card position.
     */
    private void centerBoardPointer() {
        boardPointer = new Position(0, 0);
        updateBoardPointerImage();
    }

    /**
     * Shifts the board pointer in the specified direction.
     *
     * @param side the direction to shift the pointer
     */
    private void shiftBoardPointer(Side side) {
        gameStage.clearPointer();
        boardPointer = boardPointer.withRelative(side.asRelativePosition());
        updateBoardPointerImage();
        gameStage.updatePointer();
    }

    /**
     * Updates the board pointer image.
     */
    private void updateBoardPointerImage() {
        if (!playerTurn || (cardPlaced && cardPlaced)) gameStage.setPointerColor(TextColor.ANSI.BLACK_BRIGHT);
        else {
            if (player.getBoard().spotAvailable(boardPointer)) gameStage.setPointerColor(TextColor.ANSI.GREEN_BRIGHT);
            else gameStage.setPointerColor(TextColor.ANSI.RED_BRIGHT);
        }
    }

    /**
     * Flips the card in the hand at the specified index.
     *
     * @param cardIndex the index of the card to flip
     */
    public void flipHandCard(int cardIndex) {
        player.getHand().get(cardIndex).flip();
    }

    /**
     * Checks if the board pointer is in a valid spot.
     *
     * @return {@code true} if the pointer is in a valid spot; {@code false} otherwise
     */
    public boolean pointerInValidSpot() {
        return player.getBoard().spotAvailable(boardPointer);
    }

    /**
     * Checks if the specified position on the board is valid.
     *
     * @param position the position to check
     * @return {@code true} if the position is valid; {@code false} otherwise
     */
    public boolean boardValidSpot(Position position) {
        return player.getBoard().spotAvailable(position);
    }

    /**
     * Checks if the game is over.
     *
     * @return {@code true} if the game is over; {@code false} otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Gets the list of placed cards on the board.
     *
     * @return the list of placed cards
     */
    public ArrayList<PlacedCard> getPlacedCardsInBoard() {
        return player.getBoard().getPlacedCards();
    }

    /**
     * Gets the symbols counter on the board.
     *
     * @return the symbols counter
     */
    public HashMap<Symbol, Integer> getSymbolsCounter() {
        return player.getBoard().getSymbolCounter();
    }

    /**
     * Gets the player's hand.
     *
     * @return the player's hand
     */
    public ArrayList<PlayableCard> getPlayerHand() {
        return player.getHand();
    }

    /**
     * Gets the game table.
     *
     * @return the game table
     */
    public Table getTable() {
        return table;
    }

    /**
     * Gets the scoreboard.
     *
     * @return the scoreboard
     */
    public Scoreboard getScoreboard() {
        return table.getScoreboard();
    }

    /**
     * Gets the list of goal cards.
     *
     * @return the list of goal cards
     */
    public ArrayList<GoalCard> getGoals() {
        ArrayList<GoalCard> goals = new ArrayList<>();
        goals.add(player.getPersonalGoal());
        goals.addAll(table.getPublicGoals());
        return goals;
    }

    /**
     * Gets the normal deck.
     *
     * @return the normal deck
     */
    public ArrayList<NormalCard> getNormalDeck() {
        return table.getNormalDeck();
    }

    /**
     * Gets the golden deck.
     *
     * @return the golden deck
     */
    public ArrayList<GoldenCard> getGoldenDeck() {
        return table.getGoldenDeck();
    }

    /**
     * Gets the position of the board pointer.
     *
     * @return the position of the board pointer
     */
    public Position getBoardPointer() {
        return boardPointer;
    }
}
