package IS24_LB11.cli.view.game;

import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.cli.utils.SymbolAdapter;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.symbol.Empty;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;

public class GoldenCardView extends PlayableCardView {
    public GoldenCardView(GoldenCard card) {
        super(card, new SingleBorderStyle(TextColor.ANSI.YELLOW_BRIGHT));
    }

    public void drawAll() {
        super.drawAll();
        drawSuitsNeeded();
        drawPoints();
    }

    @Override
    protected void drawPoints() {
        GoldenCard card = (GoldenCard) this.card;
        if (card.isFaceDown() || card.getPoints() == 0) return;
        if (card.getPointsCondition() == null) {
            super.drawPoints();
            return;
        }

        TerminalPosition center = new TerminalPosition(WIDTH/2, firstRow());
        char symbol = Symbol.toChar(card.getPointsCondition());

        if (symbol == Empty.CHAR) symbol = '#';
        drawChar(center, '|');
        drawChar(center.withRelative(-2, 0), (char)(card.getPoints()+'0'), TextColor.ANSI.YELLOW_BRIGHT);
        drawChar(center.withRelative(2, 0), symbol, TextColor.ANSI.YELLOW_BRIGHT);
        drawChar(center.withRelative(-4, 0), '[');
        drawChar(center.withRelative(4, 0), ']');
    }

    private void drawSuitsNeeded() {
        GoldenCard card = (GoldenCard) this.card;
        if (card.isFaceDown()) return;
        int numSuits = (int) card.getSuitsNeeded().stream().filter(suit -> suit != null).count();
        TerminalPosition pos = new TerminalPosition((WIDTH)/2-numSuits-1, lastRow());

        drawChar(pos, '[');
        drawChar(pos.withRelative(numSuits*2+2, 0), ']');
        for (int i=0; i<numSuits; i++) {
            Suit suit = card.getSuitsNeeded().get(i);
            drawChar(pos.withRelative(2+i*2, 0), SymbolAdapter.fromSymbol(suit));
        }
    }

}
