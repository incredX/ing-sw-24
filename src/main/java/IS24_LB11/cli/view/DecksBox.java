package IS24_LB11.cli.view;

import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.cli.utils.CliBox;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

import java.util.ArrayList;

import static IS24_LB11.cli.view.PlayableCardView.HEIGHT;
import static IS24_LB11.cli.view.PlayableCardView.WIDTH;

public class DecksBox extends CliBox {
    private static final int DEFAULT_WIDTH = 2*WIDTH+4;
    private static final int DEFAULT_HEIGHT = 3*HEIGHT+2;

    private ArrayList<NormalCardView> normalDeck;
    private ArrayList<GoldenCardView> goldenDeck;
    private boolean selectedNormalDeck;
    private int selectedCard;

    public DecksBox(TerminalSize parentSize, ArrayList<NormalCard> normalCards, ArrayList<GoldenCard> goldenCards) {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT,
                (parentSize.getColumns()-DEFAULT_WIDTH)/2, (parentSize.getRows()-DEFAULT_HEIGHT)/2,
                new SingleBorderStyle());
        this.normalDeck = (ArrayList<NormalCardView>) normalCards.stream().map(card -> new NormalCardView(card));
        this.goldenDeck = (ArrayList<GoldenCardView>) goldenCards.stream().map(card -> new GoldenCardView(card));
        this.selectedNormalDeck = true;
        this.selectedCard = 0;
    }

    public void build() {
        drawBorders();
        drawNormalDeck();
        drawGoldenDeck();
        drawPointer();
    }

    public void loadNormalDeck(ArrayList<NormalCard> normalCards) {
        int y = 0;
        normalDeck.clear();
        for (NormalCard normalCard : normalCards) {
            normalDeck.add(new NormalCardView(normalCard));
            normalDeck.getLast().setPosition(0, y);
            normalDeck.getLast().build();
            y += HEIGHT;
        }
    }

    public void loadGoldenDeck(ArrayList<GoldenCard> goldenCards) {
        int y = 0;
        goldenDeck.clear();
        for (GoldenCard goldenCard : goldenCards) {
            goldenDeck.add(new GoldenCardView(goldenCard));
            goldenDeck.getLast().setPosition(firstRow()+WIDTH+2, y);
            goldenDeck.getLast().build();
            y += HEIGHT;
        }
    }

    private void drawNormalDeck() {
        for (NormalCardView normalCard : normalDeck) draw(normalCard);
    }

    private void drawGoldenDeck() {
        for (GoldenCardView goldenCard : goldenDeck) draw(goldenCard);
    }

    private void drawPointer() {
        int baseX = firstColumn() + 2 + (selectedNormalDeck ? 0 : WIDTH+2);
        int baseY = firstRow() + selectedCard*HEIGHT;
        drawCell(new TerminalPosition(baseX, baseY), '#', TextColor.ANSI.YELLOW_BRIGHT);
    }

    public void setSelectedCard(int index) {
        selectedCard = index%3;
    }

    public void setSelectedDeck(boolean selectedNormalDeck) {
        this.selectedNormalDeck = selectedNormalDeck;
    }
}
