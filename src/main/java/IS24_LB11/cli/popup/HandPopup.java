package IS24_LB11.cli.popup;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.controller.GameState;
import IS24_LB11.cli.utils.Side;
import IS24_LB11.cli.view.HandView;
import IS24_LB11.game.components.PlayableCard;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.function.Consumer;

import static IS24_LB11.cli.utils.Side.*;

public class HandPopup extends Popup {
    private final GameState gameState;
    private int selectedCard;

    public HandPopup(ViewHub viewHub, GameState gameState) {
        super(viewHub, new HandView(viewHub.getScreenSize(), gameState.getPlayerHand()));
        this.gameState = gameState;
        this.selectedCard = 0;
    }

    @Override
    public String label() { return "hand"; }

    @Override
    public void update() {
        selectedCard = selectedCard % gameState.getPlayerHand().size();
        castView(handView -> {
            handView.updatePointerPosition(selectedCard);
            handView.loadCards(gameState.getPlayerHand());
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
    public void consumeKeyStroke(ClientState state, KeyStroke keyStroke) {
        if (!enabled) return; // pointer is not here
        switch (state) {
            case GameState gameState -> consumeKeyStrokeInGame(gameState, keyStroke);
            default -> {
                return;
            }
        }
    }

    private void consumeKeyStrokeInGame(GameState gameState, KeyStroke keyStroke) {
        if (keyStroke.isCtrlDown()) {
            switch (keyStroke.getKeyType()) {
                case ArrowUp -> shiftPointer(NORD);
                case ArrowDown -> shiftPointer(SUD);
                case Enter -> gameState.placeCardFromHand();
                case Character -> {
                    if (keyStroke.getCharacter() == 'f') {
                        gameState.flipHandCard(selectedCard);
                    } else if (keyStroke.getCharacter() == ' ') {
                        gameState.placeCardFromHand();
                    }
                }
                default -> {
                    return;
                }
            }
            update();
            //if (visible) castView(HandView::redraw);
            gameState.setKeyConsumed(true);
        }
    }

    private void shiftPointer(Side side) {
        if (!side.isVertical()) return;
        if (side == Side.NORD)
            selectedCard = (selectedCard == 0) ? gameState.getPlayerHand().size() - 1 : selectedCard - 1;
        else selectedCard = (selectedCard == gameState.getPlayerHand().size() - 1) ? 0 : selectedCard + 1;
        castView(decksView -> {
            decksView.updatePointerPosition(selectedCard);
            decksView.drawAll();
        });
        //drawViewInStage();
    }

    public PlayableCard getSelectedCard() {
        return gameState.getPlayerHand().get(selectedCard);
    }

    protected void castView(Consumer<HandView> consumer) {
        consumer.accept((HandView) popView);
    }
}
