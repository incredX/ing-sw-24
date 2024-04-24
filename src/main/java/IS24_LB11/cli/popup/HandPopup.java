package IS24_LB11.cli.popup;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.ClientInGame;
import IS24_LB11.cli.utils.Side;
import IS24_LB11.cli.view.HandView;
import IS24_LB11.game.components.PlayableCard;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.function.Consumer;

import static IS24_LB11.cli.utils.Side.*;

public class HandPopup extends Popup {
    private ArrayList<PlayableCard> hand;
    private int selectedCard;

    public HandPopup(ViewHub viewHub, ArrayList<PlayableCard> hand) {
        super(viewHub, new HandView(viewHub.getScreenSize(), hand));
        this.hand = hand;
        selectedCard = 0;
    }

    public void loadHand(ArrayList<PlayableCard> hand) {
        this.hand = hand;
        manageView(handView -> {
            handView.loadCards(hand);
            handView.build();
        });
    }

    public void loadHand() {
        manageView(handView -> {
            handView.loadCards(hand);
        });
        update();
    }

    public void shiftPointer(Side side) {
        if (!side.isVertical()) return;
        if (side == Side.NORD)
            selectedCard = (selectedCard == 0) ? hand.size() - 1 : selectedCard - 1;
        else selectedCard = (selectedCard == hand.size() - 1) ? 0 : selectedCard + 1;
        manageView(decksView -> {
            decksView.updatePointerPosition(selectedCard);
            decksView.build();
        });
        //drawViewInStage();
    }

    public void removeSelectedCard() {
        selectedCard = selectedCard % hand.size();
        manageView(handView -> {
            handView.updatePointerPosition(selectedCard);
            handView.loadCards(hand);
            handView.rebuild();
        });
    }

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
        manageView(handView -> {
            handView.updatePointerPosition(selectedCard);
            handView.build();
        });
        super.enable();
    }

    @Override
    public void disable() {
        manageView(handView -> {
            handView.hidePointer();
            handView.build();
        });
        super.disable();
    }

    public PlayableCard getSelectedCard() {
        return hand.get(selectedCard);
    }

    protected void manageView(Consumer<HandView> consumer) {
        consumer.accept((HandView) popView);
    }

    public Consumer<KeyStroke> keyStrokeConsumer(ClientInGame game) {
        return keyStroke -> {
            if (!enabled) return; // pointer is not here
            if (keyStroke.isCtrlDown()) {
                switch (keyStroke.getKeyType()) {
                    case ArrowUp -> shiftPointer(NORD);
                    case ArrowDown -> shiftPointer(SUD);
                    case Enter -> game.placeCardFromHand();
                    case Character -> {
                        if (keyStroke.getCharacter() == 'f') {
                            hand.get(selectedCard).flip();
                            manageView(handView -> {
                                handView.loadCards(hand);
                                handView.build();
                            });
                        }
                    }
                    default -> {
                        return;
                    }
                }
                if (visible) update();
                game.setStrokeConsumed(true);
            }
        };
    }
}
