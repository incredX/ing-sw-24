package IS24_LB11.cli.view;

import IS24_LB11.cli.view.game.GoldenCardView;
import IS24_LB11.cli.view.game.NormalCardView;
import IS24_LB11.cli.view.game.PlayableCardView;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;
import IS24_LB11.game.components.PlayableCard;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.ArrayList;

import static IS24_LB11.cli.utils.Side.EAST;
import static IS24_LB11.cli.view.game.PlayableCardView.HEIGHT;
import static IS24_LB11.cli.view.game.PlayableCardView.WIDTH;
import static IS24_LB11.game.utils.Direction.DOWN_RIGHT;
import static IS24_LB11.game.utils.Direction.UP_RIGHT;

public class HandView extends CardsBoxView {
    private static final int DEFAULT_WIDTH = WIDTH+2;
    private static final int DEFAULT_HEIGHT = 3*HEIGHT+2;

    private final ArrayList<PlayableCardView> cardViews;

    public HandView(TerminalSize parentSize, ArrayList<PlayableCard> cards) {
        super("hand", DEFAULT_WIDTH, DEFAULT_HEIGHT,
                parentSize.getColumns()-DEFAULT_WIDTH,
                (parentSize.getRows()-DEFAULT_HEIGHT)/2);
        cardViews = new ArrayList<>(3);
        setMargins(0);
        loadCards(cards);
    }

    @Override
    public void build() {
        drawBorders();
        drawTitle();
        drawCards();
        drawPointer();
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        int x = terminalSize.getColumns()-DEFAULT_WIDTH;
        int y = (terminalSize.getRows()-getHeight())/2;
        setPosition(new TerminalPosition(x, y));
    }

    public void removeCard(int cardIndex) {
        cardViews.remove(cardIndex);
        //setSize(new TerminalSize(DEFAULT_WIDTH, getHeight()-HEIGHT));
    }

    public void loadCards(ArrayList<PlayableCard> cards) {
        cardViews.clear();
        int offset_factor = 0;
        int y = offset_factor * (HEIGHT/2-1);
        for (PlayableCard card : cards)  {
            switch (card) {
                case GoldenCard goldenCard -> cardViews.add(new GoldenCardView(goldenCard));
                case NormalCard normalCard -> cardViews.add(new NormalCardView(normalCard));
                default -> throw new IllegalArgumentException("Invalid card: " + card.asString());
            }
            cardViews.getLast().setPosition(0, y);
            cardViews.getLast().build();
            y += HEIGHT;
        }
    }

    public void drawCards() {
        for (PlayableCardView cardView : cardViews) draw(cardView);
    }

    @Override
    public void drawBorders() {
        super.drawBorders();
        drawCell(getCornerPosition(UP_RIGHT), borderStyle.getSeparator(EAST));
        drawCell(getCornerPosition(DOWN_RIGHT), borderStyle.getSeparator(EAST));
    }

    public void updatePointerPosition(int cardIndex) {
        pointerPosition = new TerminalPosition(7, firstRow()+cardIndex*HEIGHT+HEIGHT/2);
    }
}
