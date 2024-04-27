package IS24_LB11.cli.view.game;

import IS24_LB11.cli.style.DoubleBorderStyle;
import IS24_LB11.cli.utils.SymbolAdapter;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.symbol.Symbol;
import com.googlecode.lanterna.TerminalPosition;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static IS24_LB11.cli.utils.Side.EAST;
import static IS24_LB11.cli.utils.Side.WEST;

public class StarterCardView extends PlayableCardView {
    public StarterCardView(StarterCard card) {
        super(card, new DoubleBorderStyle());
    }

    @Override
    public void drawAll() {
        super.drawAll();
        drawCentralSuits();
    }

    private void drawCentralSuits() {
        if (card.isFaceDown()) return;
        StarterCard card = (StarterCard) this.card;
        TerminalPosition center = new TerminalPosition(WIDTH/2, HEIGHT/2);
        List<Symbol> suits = card.getCentralSuits()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        int numSuits = suits.size();

        for (int i=0; i<numSuits; i++) {
            TerminalPosition pos = center.withRelative(0, 2*i+1-numSuits);
            drawChar(pos, SymbolAdapter.fromSymbol(suits.get(i)));
            drawClosedSquare(pos);
            if (i > 0) {
                drawChar(pos.withRelative(-2, -1), borderStyle.getSeparator(WEST));
                drawChar(pos.withRelative(2, -1), borderStyle.getSeparator(EAST));
            }
        }
    }
}
