package IS24_LB11.cli.popup;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.GameState;
import IS24_LB11.cli.controller.PlayerStateInterface;
import IS24_LB11.cli.utils.Side;
import IS24_LB11.cli.view.DecksView;
import IS24_LB11.game.components.PlayableCard;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.function.Consumer;

import static IS24_LB11.cli.utils.Side.*;

public class DecksPopup extends Popup {
    private PlayerStateInterface playerState;
    private boolean deckIsNormal;
    private int cardIndex;

    public DecksPopup(ViewHub viewHub, PlayerStateInterface playerState) {
        super(viewHub, new DecksView(viewHub.getScreenSize(), new ArrayList<>(), new ArrayList<>()));
        this.playerState = playerState;
        this.deckIsNormal = true; //true = normal, false = golden
        this.cardIndex = 0;
    }

    @Override
    public String label() { return "decks"; }

    @Override
    public void update() {
        castView(decksView -> {
            decksView.loadGoldenDeck(playerState.getGoldenDeck());
            decksView.loadNormalDeck(playerState.getNormalDeck());
            decksView.drawAll();
        });
    }

    @Override
    public void show() {
        super.show();
        enable(); // at start, when shown, the popup is enabled
    }

    @Override
    public void hide() {
        super.hide();
        disable(); // an invisible popup is also disabled (invisible => disabled)
    }

    @Override
    public void enable() {
        castView(decksView -> {
            decksView.updatePointerPosition(deckIsNormal, cardIndex);
            decksView.drawAll();
        });
        super.enable();
    }

    @Override
    public void disable() {
        castView(decksView -> {
            decksView.hidePointer();
            decksView.drawAll();
        });
        super.disable();
    }

    public PlayableCard getSelectedCard() {
        if (deckIsNormal) return playerState.getNormalDeck().get(cardIndex);
        else return playerState.getGoldenDeck().get(cardIndex);
    }

    private int getSelectedDeckSize() {
        if (deckIsNormal) return playerState.getNormalDeck().size();
        else return playerState.getGoldenDeck().size();
    }

    @Override
    public void consumeKeyStroke(KeyStroke keyStroke) {
        if (!enabled) return; // pointer is not here
        switch (playerState) {
            case GameState gameState -> consumeKeyStrokeInGame(gameState, keyStroke);
            default -> {
                return;
            }
        }
    }

    private void consumeKeyStrokeInGame(GameState gameState, KeyStroke keyStroke) {
        switch (keyStroke.getKeyType()) {
            case Enter -> gameState.drawCardFromDeck();
            case ArrowUp -> {
                if (!keyStroke.isShiftDown()) shiftPointer(NORD);
                else { return; }
            }
            case ArrowDown -> {
                if (!keyStroke.isShiftDown()) shiftPointer(SUD);
                else { return; }
            }
            case ArrowLeft -> {
                if (!keyStroke.isShiftDown()) shiftPointer(EAST);
                else { return; }
            }
            case ArrowRight -> {
                if (!keyStroke.isShiftDown()) shiftPointer(WEST);
                else { return; }
            }
            default -> { return; }
        }
        castView(DecksView::drawAll);
        gameState.keyConsumed();
    }

    private void shiftPointer(Side side) {
        int size = getSelectedDeckSize();
        if (side.isVertical()) {
            if (side == SUD) cardIndex = (cardIndex+1) % size;
            else cardIndex = cardIndex == 0 ? size - 1 : cardIndex - 1;
        } else deckIsNormal = !deckIsNormal;
        castView(decksView -> {
            decksView.updatePointerPosition(deckIsNormal, cardIndex);
            decksView.drawAll();
        });
    }

    public void setPlayerState(PlayerStateInterface playerState) {
        this.playerState = playerState;
    }

    protected void castView(Consumer<DecksView> consumer) {
        consumer.accept((DecksView) popView);
    }

    public boolean selectedNormalDeck() {
        return deckIsNormal;
    }

    public int getCardIndex() {
        return cardIndex;
    }
}
