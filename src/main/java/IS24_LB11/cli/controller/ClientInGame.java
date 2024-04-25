package IS24_LB11.cli.controller;

import IS24_LB11.cli.view.stage.GameStage;
import IS24_LB11.cli.event.server.ServerEvent;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.popup.DecksPopup;
import IS24_LB11.cli.popup.HandPopup;
import IS24_LB11.cli.utils.Side;
import IS24_LB11.cli.view.game.PlayableCardView;
import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.Player;
import IS24_LB11.game.PlayerSetup;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

//TODO : move consumers inside popups + add new method to switch focus
//       es: focusedConsumer = handPopup.enableConsumer()
//TODO : reforme the interface keyconsumer (?)
//TODO : close everything if the input listener is closed

public class ClientInGame extends ClientState {
    private final Player player;
    private GameStage gameStage;
    private DecksPopup decksPopup;
    private HandPopup handPopup;
    private Position boardPointer;
    private PlacedCard placedCard;
    private Consumer<KeyStroke> boardStrokeConsumer;
    private Consumer<KeyStroke> decksStrokeConsumer;
    private Consumer<KeyStroke> handStrokeConsumer;
    private Consumer<KeyStroke> focusedStrokeConsumer;
    private boolean strokeConsumed;
    private boolean cardPlaced;
    private boolean cardPicked;
    private boolean readOnly;

    public ClientInGame(ViewHub viewHub, PlayerSetup setup) throws IOException {
        super(viewHub);
        this.player = new Player(username, setup);
        this.boardPointer = new Position(0, 0);
        this.placedCard = null;
        this.decksPopup = null;
        this.handPopup = null;
        this.cardPlaced = false;
        this.cardPicked = false;
        this.readOnly = false;
    }

    @Override
    public ClientState execute() {
        player.applySetup();
        gameStage = viewHub.setGameStage(this);
        loadDecks(defaultNormalDeck(), defaultGoldenDeck());
        loadHand(player.getHand());
        defineBoardConsumer();
        defineDecksConsumer();
        defineHandConsumer();
        processResize(viewHub.getScreenSize());
        return super.execute();
    }

    @Override
    protected void processServerEvent(ServerEvent event) {
        if (processServerEventIfCommon(event)) return;
    }

    @Override
    protected void processCommand(String command) {
        if (processCommandIfCommon(command)) return;
        System.out.println(command);
        String[] tokens = command.split(" ", 2);
        switch (tokens[0].toUpperCase()) {
            case "SHOW" -> {
                if (tokens.length == 2) {
                    showPopup(tokens[1]);
                }
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply("show"));
            }
            case "HIDE" -> {
                if (tokens.length == 2) {
                    hidePopup(tokens[1]);
                }
                else {
                    decksPopup.hide();
                    handPopup.hide();
                    focusedStrokeConsumer = null;
                }
            }
            case "HOME" -> {
                centerBoardPointer();
                gameStage.centerBoard();
                gameStage.updateBoard();
            }
            case "HAND", "DECK", "DECKS" -> showPopup(tokens[0]);
            default -> notificationStack.addUrgent("ERROR", tokens[0]+" is not a valid command");
        }
    }

    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        strokeConsumed = false;
        if (notificationStack.consumeKeyStroke(keyStroke)) return;
        if (focusedStrokeConsumer != null) focusedStrokeConsumer.accept(keyStroke);
        boardStrokeConsumer.accept(keyStroke);
        if (!strokeConsumed) super.processCommonKeyStrokes(keyStroke);
    }

    @Override
    protected void processResize(TerminalSize size) {
        centerBoardPointer();
        cmdLine.setWidth(size.getColumns());
        viewHub.resize(size, cmdLine);
        gameStage.resize();
        handPopup.resize();
        decksPopup.resize();
        if (gameStage.getWidth() < 4 * PlayableCardView.WIDTH && decksPopup.isVisible() && handPopup.isVisible())
            decksPopup.hide();
        viewHub.updateStage();
    }

    public void drawCardFromDeck() {
        JsonConverter converter = new JsonConverter();
        if (!cardPicked && cardPlaced) {
            player.addCardToHand(decksPopup.getSelectedCard());
            handPopup.loadHand();
            decksPopup.hide();
            if (handPopup.isVisible()) {
                handPopup.enable();
                focusedStrokeConsumer = handStrokeConsumer;
            };
            cardPicked = true;
            try {
                JsonObject jsonPlacedCard = (JsonObject) new JsonParser().parse(converter.objectToJSON(placedCard));
                JsonElement jsonDeckType = new JsonPrimitive(!decksPopup.selectedNormalDeck());
                JsonElement jsonCardIndex = new JsonPrimitive(decksPopup.getCardIndex()+1);
                sendToServer("actions", new String[]{"placedCard", "deckType", "indexVisibleCards"},
                        new JsonElement[]{jsonPlacedCard, jsonDeckType, jsonCardIndex});
            } catch (JsonException e) {
                e.printStackTrace();
            }
        }
    }

    public void placeCardFromHand() {
        if (!cardPlaced && player.placeCard(handPopup.getSelectedCard(), boardPointer)) {
            placedCard = new PlacedCard(handPopup.getSelectedCard(), boardPointer);
            cardPlaced = true;
            updateBoardPointerImage();
            gameStage.updateBoard();
            handPopup.removeSelectedCard();
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
        if (cardPlaced)
            gameStage.setPointerColor(TextColor.ANSI.BLACK_BRIGHT);
        else if (player.getBoard().spotAvailable(boardPointer))
            gameStage.setPointerColor(TextColor.ANSI.GREEN_BRIGHT);
        else gameStage.setPointerColor(TextColor.ANSI.RED_BRIGHT);
    }

    private void showPopup(String token) {
        switch (token.toUpperCase()) {
            case "HAND" -> {
                if (gameStage.getWidth() < 4 * PlayableCardView.WIDTH)
                    decksPopup.hide();
                else {
                    decksPopup.disable();
                    viewHub.update();
                }
                handPopup.show();
                focusedStrokeConsumer = handStrokeConsumer;
            }
            case "DECK", "DECKS" -> {
                if (gameStage.getWidth() < 4 * PlayableCardView.WIDTH)
                    handPopup.hide();
                else {
                    handPopup.disable();
                    viewHub.update();
                }
                decksPopup.show();
                focusedStrokeConsumer = decksStrokeConsumer;
            }
            default -> notificationStack.addUrgent("ERROR", INVALID_ARG.apply(token, "show"));
        }
    }

    private void hidePopup(String token) {
        switch (token.toUpperCase()) {
            case "HAND" -> {
                handPopup.hide();
                if (decksPopup.isVisible()) {
                    decksPopup.enable();
                    focusedStrokeConsumer = decksStrokeConsumer;
                } else {
                    focusedStrokeConsumer = null;
                }
            }
            case "DECK", "DECKS" -> {
                decksPopup.hide();
                if (handPopup.isVisible()) {
                    handPopup.enable();
                    focusedStrokeConsumer = handStrokeConsumer;
                } else {
                    focusedStrokeConsumer = null;
                }
            }
            default -> notificationStack.addUrgent("ERROR", INVALID_ARG.apply(token, "hide"));
        }
    }

    private void loadDecks(ArrayList<NormalCard> normalCards, ArrayList<GoldenCard> goldenCards) {
        if (decksPopup == null) {
            decksPopup = new DecksPopup(viewHub, normalCards, goldenCards);
        } else {
            decksPopup.loadDecks(normalCards, goldenCards);
        }
    }

    private void loadHand(ArrayList<PlayableCard> hand) {
        if (handPopup == null) {
            handPopup = new HandPopup(viewHub, hand);
        } else {
            handPopup.loadHand(hand);
        }
    }

    private void defineBoardConsumer() {
        boardStrokeConsumer = (keyStroke) -> {
            if (keyStroke.isShiftDown()) {
                switch (keyStroke.getKeyType()) {
                    case ArrowUp -> shiftBoardPointer(Side.NORD);
                    case ArrowDown -> shiftBoardPointer(Side.SUD);
                    case ArrowLeft -> shiftBoardPointer(Side.WEST);
                    case ArrowRight -> shiftBoardPointer(Side.EAST);
                }
            }
        };
    }

    private void defineDecksConsumer() {
        decksStrokeConsumer = decksPopup.keyStrokeConsumer(this);
    }

    private void defineHandConsumer() {
        handStrokeConsumer = handPopup.keyStrokeConsumer(this);
    }

    public void setStrokeConsumed(boolean consumed) {
        strokeConsumed = consumed;
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

    public Position getBoardPointer() {
        return boardPointer;
    }


    private static ArrayList<NormalCard> defaultNormalDeck() {
        try {
            return (ArrayList<NormalCard>) Arrays.stream(new NormalCard[] {
                    new NormalCard("Q_AFAF0"), new NormalCard("F_EEFF1"), new NormalCard("FP_KPF0")
            }).collect(Collectors.toList());
        } catch (SyntaxException e) { return null; }
    }

    private static ArrayList<GoldenCard> defaultGoldenDeck() {
        ArrayList<GoldenCard> goldenCards;
        try {
            return  (ArrayList<GoldenCard>) Arrays.stream(new GoldenCard[] {
                    new GoldenCard("_EEKIF1KIIF__"), new GoldenCard("EE_EIF2EIIIA_"), new GoldenCard("EEE_PF2EPPPA_")
            }).collect(Collectors.toList());
        } catch (SyntaxException e) { return null; }
    }
}
