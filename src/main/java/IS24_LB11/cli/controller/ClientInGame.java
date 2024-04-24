package IS24_LB11.cli.controller;

import IS24_LB11.cli.GameStage;
import IS24_LB11.cli.event.ResizeEvent;
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
import java.util.stream.Collectors;

public class ClientInGame extends ClientState {
    private final Player player;
    private GameStage gameStage;
    private DecksPopup decksPopup;
    private HandPopup handPopup;
    private Position pointer;
    private int selectedCard;

    public ClientInGame(ViewHub viewHub, PlayerSetup setup) throws IOException {
        super(viewHub);
        this.player = new Player(username, setup);
        this.decksPopup = null;
        this.handPopup = null;
        this.selectedCard = 0;
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
                if (tokens.length == 2)
                    showPopup(tokens[1]);
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply("show"));
            }
            case "HIDE" -> {
                if (tokens.length == 2)
                    hidePopup(tokens[1]);
                else notificationStack.addUrgent("ERROR", MISSING_ARG.apply("hide"));
            }
            case "HOME" -> {
                gameStage.setPointer(new Position(0, 0));
                gameStage.centerGridBase();
                gameStage.rebuild();
            }
            default -> notificationStack.addUrgent("ERROR", tokens[0]+" is not a valid command");
        }
    }

    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        //TODO: disable board's pointer if a popup is enabled
        if (notificationStack.consumeKeyStroke(keyStroke)) return;
        if (decksPopup.consumeKeyStroke(keyStroke)) return;
        if (handPopup.consumeKeyStroke(keyStroke)) return;

        if (keyStroke.isShiftDown()) {
            switch (keyStroke.getKeyType()) {
                case ArrowUp -> { gameStage.shift(Side.NORD); return; }
                case ArrowDown -> { gameStage.shift(Side.SUD); return; }
                case ArrowLeft -> { gameStage.shift(Side.WEST); return; }
                case ArrowRight -> { gameStage.shift(Side.EAST); return; }
            }
        }
        super.processCommonKeyStrokes(keyStroke);
    }

    @Override
    protected void processResize(TerminalSize size) {
        super.processResize(size);
        handPopup.resize();
        decksPopup.resize();
        if (gameStage.getWidth() < 4 * PlayableCardView.WIDTH && decksPopup.isVisible())
            decksPopup.hide();
    }

    private void drawCardFromDeck() {
        PlayableCard card = decksPopup.getSelectedCard();
        decksPopup.hide();
        //TODO: add "card" to the ones in hand
    }

    private void showPopup(String token) {
        switch (token.toUpperCase()) {
            case "HAND" -> {
                if (gameStage.getWidth() < 4 * PlayableCardView.WIDTH)
                    decksPopup.hide();
                else {
                    decksPopup.disable();
                    viewHub.update();
                    //decksPopup.drawViewInStage();
                }
                handPopup.show();
            }
            case "DECK" -> {
                if (gameStage.getWidth() < 4 * PlayableCardView.WIDTH)
                    handPopup.hide();
                else {
                    handPopup.disable();
                    viewHub.update();
                    //handPopup.drawViewInStage();
                }
                decksPopup.show();
            }
            default -> notificationStack.addUrgent("ERROR", INVALID_ARG.apply(token, "show"));
        }
    }

    private void hidePopup(String token) {
        switch (token.toUpperCase()) {
            case "HAND" -> {
                handPopup.hide();
                if (decksPopup.isVisible()) decksPopup.enable();
            }
            case "DECK" -> {
                decksPopup.hide();
                if (handPopup.isVisible()) handPopup.enable();
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
}
