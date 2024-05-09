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

import static IS24_LB11.cli.notification.Priority.MEDIUM;

//NOTE : URGENT PRIORITY
//NOTE : HIGH PRIORITY
//NOTE : MEDIUM PRIORITY
//NOTE : LOW PRIORITY
//TODO : sowly remove resize from viewhub and assign to notification their views to resize
//TODO : refactor viewhub as a cliBox's queue consumer. (maybe?)
//TODO : add boolean edited in cliBox (on in drawAll & set to off in print)

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

    public GameState(SetupState setupState) {
        super(setupState);
        this.player = new Player(username, setupState.getSetup());
        this.table = setupState.getTable();
        this.boardPointer = new Position(0, 0);
        this.placedCard = null;
        popManager.forEachPopup(popup -> popup.setPlayerState(this));
        popManager.addPopup(new SymbolsPopup(getViewHub(), this));
    }

    public GameState(AutomatedState automatedState) {
        super(automatedState);
        this.player = automatedState.getPlayer();
        this.table = automatedState.getTable();
        this.boardPointer = new Position(0, 0);
        this.placedCard = null;
        this.popManager.addPopup(
                new HelpPoup(getViewHub(), this),
                new TablePopup(getViewHub(), this),
                new HandPopup(getViewHub(), this),
                new DecksPopup(getViewHub(), this),
                new SymbolsPopup(getViewHub(), this)
                );
    }

    public GameState(ViewHub viewHub, NotificationStack stack, PlayerSetup setup, Table table) throws IOException {
        super(viewHub, stack);
        this.player = new Player(username, setup);
        this.table = table;
        this.boardPointer = new Position(0, 0);
        this.placedCard = null;
    }

    @Override
    public ClientState execute() {
        if (getPlacedCardsInBoard().isEmpty()) player.applySetup();
        gameStage = viewHub.setGameStage(this);
        updateBoardPointerImage();
        popManager.updatePopups();
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

    @Override
    protected void processServerEvent(ServerEvent serverEvent) {
        if (processServerEventIfCommon(serverEvent)) return;
        switch (serverEvent) {
            case ServerNewTurnEvent newTurnEvent -> {
                Debugger.print("turn of "+newTurnEvent.player()+" (I'm "+username+")");
                if (newTurnEvent.player().isEmpty()) {
                    gameOver = true;
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
                popManager.getPopup("decks").update();
                popManager.getPopup("table").update();
            }
            case ServerPlayerDisconnectEvent disconnectEvent -> {
                if (gameOver) break;
                table.getScoreboard().removePlayer(disconnectEvent.player());
                popManager.getPopup("table").redrawView();
            }
            default -> processResult(Result.Error("received unknown server event"));
        }
    }

    @Override
    protected void processCommand(String command) {
        if (processCommandIfCommon(command)) return;
        Debugger.print("command: "+command);
        String[] tokens = command.split(" ", 2);
        switch (tokens[0].toUpperCase()) {
            case "HOME" -> {
                centerBoardPointer();
                gameStage.centerBoard();
                gameStage.updateBoard();
            }
            case "LOGOUT" -> logout();
            //case "HELP", "TABLE", "HAND", "DECKS" -> popManager.showPopup(tokens[0]);
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
            JsonElement jsonCardIndex = new JsonPrimitive(decksPopup.getCardIndex()+1);
            sendToServer("turnActions", new String[]{"placedCard", "deckType", "indexVisibleCards"},
                    new JsonElement[]{jsonPlacedCard, jsonDeckType, jsonCardIndex});
        } catch (JsonException e) {
            Debugger.print(e);
        }
        notificationStack.removeNotifications(Priority.LOW);
    }

    public void placeCardFromHand() {
        HandPopup handPopup = (HandPopup) popManager.getPopup("hand");
        if (!playerTurn) {
            notificationStack.addUrgent("WARNING", "can't place cards outside of turn");
            return;
        }
        if (cardPlaced) {
            notificationStack.addUrgent("WARNING", "a card has alredy been placed in this turn");
            return;
        }
        PlayableCard selectedCard = handPopup.getSelectedCard();
        if (player.placeCard(selectedCard, boardPointer)) {
            placedCard = new PlacedCard(selectedCard, boardPointer);
            cardPlaced = true;
            updateBoardPointerImage();
            gameStage.updateBoard();
            popManager.getPopup("symbols").update();
        }
        else notificationStack.addUrgent("WARNING", "cannot place card");
    }

    public void logout() {
        Debugger.print("loggin out");
        sendToServer("quit");
        popManager.hideAllPopups();
        notificationStack.removeAllNotifications();
        serverHandler.shutdown();
        setNextState(new LobbyState(viewHub));
    }

    private void centerBoardPointer() {
        boardPointer = new Position(0,0);
        updateBoardPointerImage();
    }

    private void shiftBoardPointer(Side side) {
        gameStage.clearPointer();
        boardPointer = boardPointer.withRelative(side.asRelativePosition());
        updateBoardPointerImage();
        gameStage.updatePointer();
    }

    private void updateBoardPointerImage() {
        if (!playerTurn || (cardPlaced && cardPlaced)) gameStage.setPointerColor(TextColor.ANSI.BLACK_BRIGHT);
        else {
            if (player.getBoard().spotAvailable(boardPointer)) gameStage.setPointerColor(TextColor.ANSI.GREEN_BRIGHT);
            else gameStage.setPointerColor(TextColor.ANSI.RED_BRIGHT);
        }
    }
    
    public void flipHandCard(int cardIndex) {
        player.getHand().get(cardIndex).flip();
    }

    public boolean pointerInValidSpot() {
        return player.getBoard().spotAvailable(boardPointer);
    }

    public boolean boardValidSpot(Position position) {
        return player.getBoard().spotAvailable(position);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public ArrayList<PlacedCard> getPlacedCardsInBoard() {
        return player.getBoard().getPlacedCards();
    }

    public HashMap<Symbol, Integer> getSymbolsCounter() { return player.getBoard().getSymbolCounter(); }

    public ArrayList<PlayableCard> getPlayerHand() {
        return player.getHand();
    }

    public Table getTable() {
        return table;
    }

    public Scoreboard getScoreboard() {
        return table.getScoreboard();
    }
    
    public ArrayList<GoalCard> getGoals() {
        ArrayList<GoalCard> goals = new ArrayList<>();
        goals.add(player.getPersonalGoal());
        goals.addAll(table.getPublicGoals());
        return goals;
    }
    
    public ArrayList<NormalCard> getNormalDeck() {
        return table.getNormalDeck();
    }
    
    public ArrayList<GoldenCard> getGoldenDeck() {
        return table.getGoldenDeck();
    }

    public Position getBoardPointer() {
        return boardPointer;
    }
}
