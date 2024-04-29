package IS24_LB11.cli.controller;

import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.Scoreboard;
import IS24_LB11.cli.Table;
import IS24_LB11.cli.event.server.ServerNewTurnEvent;
import IS24_LB11.cli.notification.NotificationStack;
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
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.Position;
import com.google.gson.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;
import java.util.ArrayList;

//NOTE : HIGH PRIORITY
//TODO : synchronize stage.buildAreas
//TODO : close everything if the input listener is closed
//TODO : automatization of game flow
//NOTE : MEDIUM PRIORITY
//TODO : notify succesfull connection to server
//TODO : organize popups with a priorityQueue
//TODO : error popup
//TODO : chatBox (new popup)
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
    private boolean cardPlaced;
    private boolean cardPicked;
    private boolean playerTurn;

    public GameState(SetupState setupState) {
        super(setupState);
        this.player = new Player(username, setupState.getSetup());
        this.table = setupState.getTable();
        this.boardPointer = new Position(0, 0);
        this.placedCard = null;
        this.cardPlaced = false;
        this.cardPicked = false;
        this.playerTurn = false;
        popManager.forEachPopup(popup -> popup.setPlayerState(this));
    }

    public GameState(ViewHub viewHub, NotificationStack stack, PlayerSetup setup, Table table) throws IOException {
        super(viewHub, stack);
        this.player = new Player(username, setup);
        this.table = table;
        this.boardPointer = new Position(0, 0);
        this.placedCard = null;
        this.cardPlaced = false;
        this.cardPicked = false;
        this.playerTurn = false;
    }

    @Override
    public ClientState execute() {
        player.applySetup();
        gameStage = viewHub.setGameStage(this);
        updateBoardPointerImage();
        popManager.updatePopups();
        gameStage.drawAll();
        cmdLine.update();
        viewHub.update();
        return super.execute();
    }

    @Override
    protected void processServerEvent(ServerEvent serverEvent) {
        if (processServerEventIfCommon(serverEvent)) {
            viewHub.update();
            return;
        }
        switch (serverEvent) {
            case ServerNewTurnEvent newTurnEvent -> {
                Debugger.print("turn of "+newTurnEvent.player()+" (I'm "+username+")");
                if (newTurnEvent.player().equals(username)) {
                    cardPlaced = false;
                    cardPicked = false;
                    playerTurn = true;
                }
                updateBoardPointerImage();
                table.update(newTurnEvent);
                popManager.getPopup("decks").update();
                popManager.getPopup("table").update();
            }
            default -> processResult(Result.Error("received unknown server event"));
        }
        viewHub.update();
    }

    @Override
    protected void processCommand(String command) {
        if (processCommandIfCommon(command)) {
            viewHub.update();
            return;
        }
        Debugger.print("command: "+command);
        String[] tokens = command.split(" ", 2);
        switch (tokens[0].toUpperCase()) {
            case "SHOW" -> {
                if (tokens.length == 2) {
                    popManager.showPopup(tokens[1]);
                }
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply("show"));
            }
            case "HIDE" -> {
                if (tokens.length == 2) popManager.hidePopup(tokens[1]);
                else popManager.hideFocusedPopup();
            }
            case "HOME" -> {
                centerBoardPointer();
                gameStage.centerBoard();
                gameStage.updateBoard();
            }
            case "HAND", "DECKS", "TABLE" -> popManager.showPopup(tokens[0]);
            default -> {
                notificationStack.addUrgent("ERROR", INVALID_CMD.apply(tokens[0], "game"));
            }
        }
        viewHub.update();
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
            }
        }
        viewHub.update();
    }

    @Override
    protected void processResize(TerminalSize screenSize) {
        centerBoardPointer();
        super.processResize(screenSize);
        popManager.resizePopups();
        viewHub.update();
    }

    public void drawCardFromDeck() {
        JsonConverter converter = new JsonConverter();
        if (!cardPicked && cardPlaced) {
            DecksPopup decksPopup = (DecksPopup) popManager.getPopup("decks");
            HandPopup handPopup = (HandPopup) popManager.getPopup("hand");
            player.addCardToHand(decksPopup.getSelectedCard());
            handPopup.update();
            popManager.hidePopup("decks");
            cardPicked = true;
            playerTurn = true;
            try {
                JsonObject jsonPlacedCard = (JsonObject) new JsonParser().parse(converter.objectToJSON(placedCard));
                JsonElement jsonDeckType = new JsonPrimitive(!decksPopup.selectedNormalDeck());
                JsonElement jsonCardIndex = new JsonPrimitive(decksPopup.getCardIndex()+1);
                sendToServer("turnActions", new String[]{"placedCard", "deckType", "indexVisibleCards"},
                        new JsonElement[]{jsonPlacedCard, jsonDeckType, jsonCardIndex});
            } catch (JsonException e) {
                e.printStackTrace();
            }
        }
    }

    public void placeCardFromHand() {
        HandPopup handPopup = (HandPopup) popManager.getPopup("hand");
        if (!playerTurn) {
            notificationStack.addUrgent("WANING", "can't place cards outside of turn");
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
        }
        else notificationStack.addUrgent("WARNING", "cannot place card");
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
        if (!playerTurn) gameStage.setPointerColor(TextColor.ANSI.BLACK_BRIGHT);
        else if (cardPlaced) gameStage.setPointerColor(TextColor.ANSI.BLACK_BRIGHT);
        else if (player.getBoard().spotAvailable(boardPointer)) gameStage.setPointerColor(TextColor.ANSI.GREEN_BRIGHT);
        else gameStage.setPointerColor(TextColor.ANSI.RED_BRIGHT);
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

    public ArrayList<PlacedCard> getPlacedCardsInBoard() {
        return player.getBoard().getPlacedCards();
    }

    public ArrayList<PlayableCard> getPlayerHand() {
        return player.getHand();
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
