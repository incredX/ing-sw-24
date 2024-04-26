package IS24_LB11.cli.controller;

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
import IS24_LB11.game.utils.SyntaxException;
import com.google.gson.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

//TODO : rename PlayerSetupEvent to GameReadyEvent
//TODO : add scoreboard event
//TODO : reconnection to server in lobby
//TODO : close everything if the input listener is closed

public class GameState extends ClientState {
    private final Player player;
    private Table table;
    private ArrayList<NormalCard> normalDeck;
    private ArrayList<GoldenCard> goldenDeck;
    private GameStage gameStage;
    private PopupManager popManager;
    private Position boardPointer;
    private PlacedCard placedCard;
    private boolean strokeConsumed;
    private boolean cardPlaced;
    private boolean cardPicked;
    private boolean readOnly;

    public GameState(ViewHub viewHub, NotificationStack stack, PlayerSetup setup, Table table) throws IOException {
        super(viewHub, stack);
        this.player = new Player(username, setup);
        this.table = table;
        this.normalDeck = new ArrayList<>();
        this.goldenDeck = new ArrayList<>();
        this.popManager = new PopupManager(new Popup[]{
                new TablePopup(viewHub, this),
                new HandPopup(viewHub, this), 
                new DecksPopup(viewHub, this)}
        );
        this.boardPointer = new Position(0, 0);
        this.placedCard = null;
        this.cardPlaced = false;
        this.cardPicked = false;
        this.readOnly = false;
    }

    public GameState(ViewHub viewHub, PlayerSetup setup, Table table) throws IOException {
        this(viewHub, new NotificationStack(viewHub, 0), setup, table);
    }

    @Override
    public ClientState execute() {
        player.applySetup();
        System.out.println(table.toString());
        normalDeck = defaultNormalDeck();
        goldenDeck = defaultGoldenDeck();
        gameStage = viewHub.setGameStage(this);
        popManager.updatePopups();
        processResize(viewHub.getScreenSize());
        return super.execute();
    }

    @Override
    protected void processServerEvent(ServerEvent event) {
        if (processServerEventIfCommon(event)) return;
        switch (event) {
            case ServerNewTurnEvent newTurnEvent -> {
                if (newTurnEvent.player().equals(username)) {
                    cardPlaced = false;
                    cardPicked = false;
                    updateBoardPointerImage();
                }
                table.getScoreboard().setNextPlayer();
                normalDeck = newTurnEvent.normalDeck();
                goldenDeck = newTurnEvent.goldenDeck();
                if (!normalDeck.getLast().isFaceDown()) normalDeck.getLast().flip();
                if (!goldenDeck.getLast().isFaceDown()) goldenDeck.getLast().flip();
                popManager.getPopup("decks").update();
            }
            default -> processResult(Result.Error("received unknown server event"));
        }
    }

    @Override
    protected void processCommand(String command) {
        if (processCommandIfCommon(command)) return;
        System.out.println(command);
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
            default -> notificationStack.addUrgent("ERROR", INVALID_CMD.apply(tokens[0], "game"));
        }
    }

    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        strokeConsumed = false;
        if (notificationStack.consumeKeyStroke(keyStroke)) return;
        popManager.consumeKeyStroke(this, keyStroke);
        if (keyStroke.isShiftDown()) {
            switch (keyStroke.getKeyType()) {
                case ArrowUp -> shiftBoardPointer(Side.NORD);
                case ArrowDown -> shiftBoardPointer(Side.SUD);
                case ArrowLeft -> shiftBoardPointer(Side.WEST);
                case ArrowRight -> shiftBoardPointer(Side.EAST);
            }
        }
        if (!strokeConsumed) super.processCommonKeyStrokes(keyStroke);
    }

    @Override
    protected void processResize(TerminalSize size) {
        centerBoardPointer();
        super.processResize(size);
        popManager.resizePopups();
        //viewHub.updateStage();
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
        if (!cardPlaced && player.placeCard(handPopup.getSelectedCard(), boardPointer)) {
            placedCard = new PlacedCard(handPopup.getSelectedCard(), boardPointer);
            cardPlaced = true;
            updateBoardPointerImage();
            gameStage.updateBoard();
            handPopup.update();
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
        if (cardPlaced || readOnly)
            gameStage.setPointerColor(TextColor.ANSI.BLACK_BRIGHT);
        else if (player.getBoard().spotAvailable(boardPointer))
            gameStage.setPointerColor(TextColor.ANSI.GREEN_BRIGHT);
        else gameStage.setPointerColor(TextColor.ANSI.RED_BRIGHT);
    }

    public void setStrokeConsumed(boolean consumed) {
        strokeConsumed = consumed;
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

    public ArrayList<PlayableCard> getPlayerHand() {
        return player.getHand();
    }

    public ArrayList<PlacedCard> getPlacedCardsInBoard() {
        return player.getBoard().getPlacedCards();
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
        return normalDeck;
    }
    
    public ArrayList<GoldenCard> getGoldenDeck() {
        return goldenDeck;
    }

    public Position getBoardPointer() {
        return boardPointer;
    }

    private static ArrayList<NormalCard> defaultNormalDeck() {
        try {
            return (ArrayList<NormalCard>) Arrays.stream(new NormalCard[] {
                    new NormalCard("Q_AFAF0"), new NormalCard("F_EEFF1"), new NormalCard("FP_KPB0")
            }).collect(Collectors.toList());
        } catch (SyntaxException e) { return null; }
    }

    private static ArrayList<GoldenCard> defaultGoldenDeck() {
        ArrayList<GoldenCard> goldenCards;
        try {
            return  (ArrayList<GoldenCard>) Arrays.stream(new GoldenCard[] {
                    new GoldenCard("_EEKIF1KIIF__"), new GoldenCard("EE_EIF2EIIIA_"), new GoldenCard("EEE_PB2EPPPA_")
            }).collect(Collectors.toList());
        } catch (SyntaxException e) { return null; }
    }
}
