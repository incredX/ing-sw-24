package IS24_LB11.cli.popup;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.utils.Side;
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
    private boolean deckIsGolden;
    private int cardIndex;

    public DecksPopup(ViewHub viewHub, ArrayList<NormalCard> normalCards, ArrayList<GoldenCard> goldenCards) {
        super(viewHub, new DecksView(viewHub.getScreenSize(), normalCards, goldenCards));
        this.normalCards = normalCards;
        this.goldenCards = goldenCards;
        this.deckIsGolden = true; //true = golden, false = normal
        this.cardIndex = 0;
    }

    public void loadDecks(ArrayList<NormalCard> normalCards, ArrayList<GoldenCard> goldenCards) {
        this.normalCards = normalCards;
        this.goldenCards = goldenCards;
        manageView(decksView -> {
            decksView.loadGoldenDeck(goldenCards);
            decksView.loadNormalDeck(normalCards);
            decksView.build();
        });
    }

    @Override
    public boolean consumeKeyStroke(KeyStroke keyStroke) {
        if (!enabled) return false; // pointer is not here
        if (keyStroke.isCtrlDown()) {
            switch (keyStroke.getKeyType()) {
                case ArrowUp -> shiftPointer(NORD);
                case ArrowDown -> shiftPointer(SUD);
                case ArrowLeft -> shiftPointer(WEST);
                case ArrowRight -> shiftPointer(EAST);
                case Enter -> {
                    hide();
                    //TODO: save somewhere card to draw
                }
                default -> {
                    return false;
                }
            }
            if (visible) update();
            return true;
        }
        return false;
    }

    public PlayableCard getSelectedCard() {
        if (deckIsGolden) return goldenCards.get(cardIndex);
        else return normalCards.get(cardIndex);
    }

    public void shiftPointer(Side side) {
        int size = getSelectedDeckSize();
        if (side.isVertical()) {
            if (side == SUD) cardIndex = (cardIndex+1) % size;
            else cardIndex = cardIndex == 0 ? size - 1 : cardIndex - 1;
        } else deckIsGolden = !deckIsGolden;
        manageView(decksView -> {
            decksView.updatePointerPosition(deckIsGolden, cardIndex);
            decksView.build();
        });
        //drawViewInStage();
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
        manageView(decksView -> {
            decksView.updatePointerPosition(deckIsGolden, cardIndex);
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
        if (deckIsGolden == false) return normalCards.size();
        else return goldenCards.size();
    }
}
