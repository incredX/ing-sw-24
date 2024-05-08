package IS24_LB11.cli.view.popup;

import IS24_LB11.cli.view.game.GoldenCardView;
import IS24_LB11.cli.view.game.NormalCardView;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.ArrayList;

import static IS24_LB11.cli.view.game.PlayableCardView.HEIGHT;
import static IS24_LB11.cli.view.game.PlayableCardView.WIDTH;

public class DecksView extends CardsBoxView {
    private static final int DEFAULT_WIDTH = 2*WIDTH+4;
    private static final int DEFAULT_HEIGHT = 3*HEIGHT+3;

    private ArrayList<NormalCardView> normalDeck;
    private ArrayList<GoldenCardView> goldenDeck;

    public DecksView(TerminalSize parentSize) {
        super("decks", DEFAULT_WIDTH, DEFAULT_HEIGHT,
                (parentSize.getColumns()-DEFAULT_WIDTH)/2, (parentSize.getRows()-DEFAULT_HEIGHT)/2);
        //loadNormalDeck(normalCards);
        //loadGoldenDeck(goldenCards);
    }

    @Override
    public void drawAll() {
        drawBorders();
        drawTitle();
        drawNormalDeck();
        drawGoldenDeck();
        drawDashedLine();
        drawPointer();
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        int x = (terminalSize.getColumns()-getWidth())/2;
        int y = (terminalSize.getRows()-getHeight())/2;
        setPosition(new TerminalPosition(x, y));
    }

    public void loadNormalDeck(ArrayList<NormalCard> normalCards) {
        int y = 0;
        if (normalDeck == null) normalDeck = new ArrayList<>();
        else normalDeck.clear();
        for (NormalCard normalCard : normalCards) {
            normalDeck.add(new NormalCardView(normalCard));
            normalDeck.getLast().setPosition(0, y);
            normalDeck.getLast().drawAll();
            y += HEIGHT + (y == HEIGHT ? 1 : 0);
        }
    }

    public void loadGoldenDeck(ArrayList<GoldenCard> goldenCards) {
        int y = 0;
        if (goldenDeck == null) goldenDeck = new ArrayList<>();
        else goldenDeck.clear();
        for (GoldenCard goldenCard : goldenCards) {
            goldenDeck.add(new GoldenCardView(goldenCard));
            goldenDeck.getLast().setPosition(firstRow()+WIDTH-1, y);
            goldenDeck.getLast().drawAll();
            y += HEIGHT + (y == HEIGHT ? 1 : 0);
        }
    }

    private void drawDashedLine() {
        String line = "-".repeat(innerWidth()-3);
        fillRow(firstRow()+2*HEIGHT, firstColumn(), line);
    }

    private void drawNormalDeck() {
        for (NormalCardView normalCard : normalDeck) drawBox(normalCard);
    }

    private void drawGoldenDeck() {
        for (GoldenCardView goldenCard : goldenDeck) drawBox(goldenCard);
    }

    public void updatePointerPosition(boolean deck, int cardIndex) {
        pointerPosition = new TerminalPosition(
                7 + (deck ? 0 : WIDTH),
                firstRow() + HEIGHT/2 + cardIndex * HEIGHT + (cardIndex == 2 ? 1 : 0)
        );
    }
}
