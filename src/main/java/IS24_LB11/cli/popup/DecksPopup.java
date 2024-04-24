package IS24_LB11.cli.popup;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.ClientInGame;
import IS24_LB11.cli.utils.Side;
import IS24_LB11.cli.view.DecksView;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;
import IS24_LB11.game.components.PlayableCard;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.function.Consumer;

import static IS24_LB11.cli.utils.Side.*;

public class DecksPopup extends Popup {
    private ArrayList<NormalCard> normalCards;
    private ArrayList<GoldenCard> goldenCards;
    private boolean deckIsNormal;
    private int cardIndex;

    public DecksPopup(ViewHub viewHub, ArrayList<NormalCard> normalCards, ArrayList<GoldenCard> goldenCards) {
        super(viewHub, new DecksView(viewHub.getScreenSize(), normalCards, goldenCards));
        this.deckIsNormal = true; //true = normal, false = golden
        this.cardIndex = 0;
        loadDecks(normalCards, goldenCards);
    }

    public void loadDecks(ArrayList<NormalCard> normalCards, ArrayList<GoldenCard> goldenCards) {
        this.normalCards = normalCards;
        this.goldenCards = goldenCards;
        if (!goldenCards.getLast().isFaceDown()) goldenCards.getLast().flip();
        if (!normalCards.getLast().isFaceDown()) normalCards.getLast().flip();
        manageView(decksView -> {
            decksView.loadGoldenDeck(goldenCards);
            decksView.loadNormalDeck(normalCards);
            decksView.build();
        });
    }

    public PlayableCard getSelectedCard() {
        if (deckIsNormal) return normalCards.get(cardIndex);
        else return goldenCards.get(cardIndex);
    }

    public void shiftPointer(Side side) {
        int size = getSelectedDeckSize();
        if (side.isVertical()) {
            if (side == SUD) cardIndex = (cardIndex+1) % size;
            else cardIndex = cardIndex == 0 ? size - 1 : cardIndex - 1;
        } else deckIsNormal = !deckIsNormal;
        manageView(decksView -> {
            decksView.updatePointerPosition(deckIsNormal, cardIndex);
            decksView.build();
        });
        //drawViewInStage();
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
        manageView(decksView -> {
            decksView.updatePointerPosition(deckIsNormal, cardIndex);
            decksView.build();
        });
        super.enable();
    }

    @Override
    public void disable() {
        manageView(decksView -> {
            decksView.hidePointer();
            decksView.build();
        });
        super.disable();
    }

    protected void manageView(Consumer<DecksView> consumer) {
        consumer.accept((DecksView) popView);
    }

    private int getSelectedDeckSize() {
        if (deckIsNormal) return normalCards.size();
        else return goldenCards.size();
    }

    public Consumer<KeyStroke> keyStrokeConsumer(ClientInGame game) {
        return (keyStroke) -> {
            if (!enabled) return; // pointer is not here
            if (keyStroke.isCtrlDown()) {
                switch (keyStroke.getKeyType()) {
                    case ArrowUp -> shiftPointer(NORD);
                    case ArrowDown -> shiftPointer(SUD);
                    case ArrowLeft -> shiftPointer(WEST);
                    case ArrowRight -> shiftPointer(EAST);
                    case Enter -> game.drawCardFromDeck();
                    default -> {
                        return;
                    }
                }
                if (visible) update();
                game.setStrokeConsumed(true);
            }
        };
    }

    public boolean selectedNormalDeck() {
        return deckIsNormal;
    }

    public int getCardIndex() {
        return cardIndex;
    }
}
