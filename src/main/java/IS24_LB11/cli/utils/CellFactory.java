package IS24_LB11.cli.utils;

import com.googlecode.lanterna.TextColor;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;

public class CellFactory {
    public static Cell fromSymbol(Symbol symbol) {
        if (symbol == Suit.ANIMAL) return new Cell('A', TextColor.ANSI.CYAN);
        if (symbol == Suit.MUSHROOM) return new Cell('F', TextColor.ANSI.RED);
        if (symbol == Suit.INSECT) return new Cell('I', TextColor.ANSI.MAGENTA);
        if (symbol == Suit.PLANT) return new Cell('P', TextColor.ANSI.GREEN);
        if (symbol == Item.QUILL) return new Cell('Q', TextColor.ANSI.YELLOW_BRIGHT);
        if (symbol == Item.INKWELL) return new Cell('K', TextColor.ANSI.YELLOW_BRIGHT);
        if (symbol == Item.MANUSCRIPT) return new Cell('M', TextColor.ANSI.YELLOW_BRIGHT);
        return new Cell(' ', TextColor.ANSI.DEFAULT);
    }
}
