package IS24_LB11.cli.popup;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.GameState;
import IS24_LB11.cli.controller.PlayerStateInterface;
import IS24_LB11.cli.utils.Side;
import IS24_LB11.cli.view.popup.HandView;
import IS24_LB11.game.components.PlayableCard;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.function.Consumer;

import static IS24_LB11.cli.utils.Side.*;

public class HandPopup extends Popup {
    private PlayerStateInterface playerState;
    private int selectedCard;

    public HandPopup(ViewHub viewHub, PlayerStateInterface playerState) {
        super(viewHub, new HandView(viewHub.getScreenSize(), playerState.getPlayerHand()));
        this.playerState = playerState;
        this.selectedCard = 0;
    }

    @Override
    public String label() { return "hand"; }

    @Override
    public void update() {
        selectedCard = selectedCard % playerState.getPlayerHand().size();
        castView(handView -> {
            handView.updatePointerPosition(selectedCard);
            handView.loadCards(playerState.getPlayerHand());
            handView.redraw();
        });
    }

    @Override
    public void show() {
        enable(); // at start, when shown, the popup is enabled
        super.show();
    }

    @Override
    public void hide() {
        disable(); // an invisible popup is also disabled (invisible => disabled)
        super.hide();
    }

    @Override
    public void enable() {
        castView(handView -> {
            handView.updatePointerPosition(selectedCard);
            handView.drawAll();
        });
        super.enable();
    }

    @Override
    public void disable() {
        castView(handView -> {
            handView.hidePointer();
            handView.drawAll();
        });
        super.disable();
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
            case ArrowUp -> {
                if (!keyStroke.isShiftDown()) shiftPointer(NORD);
                else { return; }
            }
            case ArrowDown -> {
                if (!keyStroke.isShiftDown()) shiftPointer(SUD);
                else { return; }
            }
            case ArrowLeft,ArrowRight -> {
                if (keyStroke.isShiftDown()) return;
            }
            case Enter -> gameState.placeCardFromHand();
            case Character -> {
                if (keyStroke.getCharacter() == 'F' || keyStroke.getCharacter() == 'f')
                    gameState.flipHandCard(selectedCard);
                else { return; }
            }
            default -> {
                return;
            }
        }
        update();
        //if (visible) castView(HandView::redraw);
        gameState.consumeKey();
    }

    private void shiftPointer(Side side) {
        if (!side.isVertical()) return;
        if (side == Side.NORD)
            selectedCard = (selectedCard == 0) ? playerState.getPlayerHand().size() - 1 : selectedCard - 1;
        else selectedCard = (selectedCard == playerState.getPlayerHand().size() - 1) ? 0 : selectedCard + 1;
        castView(decksView -> {
            decksView.updatePointerPosition(selectedCard);
            decksView.drawAll();
        });
        //drawViewInStage();
    }

    public void setPlayerState(PlayerStateInterface playerState) {
        this.playerState = playerState;
    }

    public PlayableCard getSelectedCard() {
        return playerState.getPlayerHand().get(selectedCard);
    }

    protected void castView(Consumer<HandView> consumer) {
        consumer.accept((HandView) popView);
    }
}
