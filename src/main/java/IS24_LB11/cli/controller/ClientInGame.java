package IS24_LB11.cli.controller;

import IS24_LB11.cli.GameStage;
import IS24_LB11.cli.event.ServerEvent;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.popup.DecksPopup;
import IS24_LB11.cli.popup.HandPopup;
import IS24_LB11.cli.utils.Side;
import IS24_LB11.cli.view.PlayableCardView;
import IS24_LB11.game.Player;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.utils.SyntaxException;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

//TODO : manage pointers color here
//TODO : move pointer to clientState
//TODO : send action to server
//TODO : move consumers inside popups + add new method to switch focus
//       es: focusedConsumer = handPopup.enableConsumer()
//TODO : close everything if the input listener is closed

public class ClientInGame extends ClientState {
    private final Player player;
    private GameStage gameStage;
    private DecksPopup decksPopup;
    private HandPopup handPopup;
    private Position pointer;
    private Consumer<KeyStroke> boardStrokeConsumer;
    private Consumer<KeyStroke> decksStrokeConsumer;
    private Consumer<KeyStroke> handStrokeConsumer;
    private Consumer<KeyStroke> focusedStrokeConsumer;
    private boolean strokeConsumed;
    private boolean cardPlaced;
    private boolean cardPicked;

    public ClientInGame(ViewHub viewHub, PlayerSetup setup) throws IOException {
        super(viewHub);
        this.player = new Player(username, setup);
        this.decksPopup = null;
        this.handPopup = null;
        this.cardPlaced = false;
        this.cardPicked = false;
    }

    @Override
    public ClientState execute() {
        ArrayList<NormalCard> normalCards;
        ArrayList<GoldenCard> goldenCards;
        try {
            normalCards = (ArrayList<NormalCard>) Arrays.stream(new NormalCard[] {
                    new NormalCard("Q_AFAF0"), new NormalCard("F_EEFF1"), new NormalCard("FP_KPF0")
            }).collect(Collectors.toList());
            goldenCards = (ArrayList<GoldenCard>) Arrays.stream(new GoldenCard[] {
                    new GoldenCard("_EEKIF1KIIF__"), new GoldenCard("EE_EIF2EIIIA_"), new GoldenCard("EEE_PF2EPPPA_")
            }).collect(Collectors.toList());
        } catch (SyntaxException e) { return null; }
        player.applySetup();
        gameStage = viewHub.setGameStage(player);
        loadDecks(normalCards, goldenCards);
        loadHand(player.getHand());
        defineBoardConsumer();
        defineDecksConsumer();
        defineHandConsumer();
        viewHub.resize(viewHub.getScreenSize(), cmdLine);
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
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply("hide"));
            }
            case "HOME" -> {
                gameStage.setPointer(new Position(0, 0));
                gameStage.centerGridBase();
                gameStage.rebuild();
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
        super.processResize(size);
        handPopup.resize();
        decksPopup.resize();
        if (gameStage.getWidth() < 4 * PlayableCardView.WIDTH && decksPopup.isVisible() && handPopup.isVisible())
            decksPopup.hide();
        viewHub.updateStage();
    }

    public void drawCardFromDeck() {
        if (!cardPicked && cardPlaced) {
            player.addCardToHand(decksPopup.getSelectedCard());
            handPopup.loadHand();
            decksPopup.hide();
            if (handPopup.isVisible()) {
                handPopup.enable();
                focusedStrokeConsumer = handStrokeConsumer;
            };
            cardPicked = true;
        }
    }

    public void placeCardFromHand() {
        if (!cardPlaced && player.placeCard(handPopup.getSelectedCard(), gameStage.getPointer())) {
            gameStage.updateBoard();
            handPopup.removeSelectedCard();
            handPopup.update();
            cardPlaced = true;
        }
        else notificationStack.addUrgent("WARNING", "cannot place card");
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
                    case ArrowUp -> { gameStage.shift(Side.NORD); return; }
                    case ArrowDown -> { gameStage.shift(Side.SUD); return; }
                    case ArrowLeft -> { gameStage.shift(Side.WEST); return; }
                    case ArrowRight -> { gameStage.shift(Side.EAST); return; }
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
}
