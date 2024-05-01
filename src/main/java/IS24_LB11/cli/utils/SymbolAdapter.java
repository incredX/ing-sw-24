package IS24_LB11.cli.utils;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;

public class SymbolAdapter {
    public static TextCharacter fromSymbol(Symbol symbol) {
        return switch (symbol) {
            case Suit suit -> fromSuit(suit);
            case Item item -> fromItem(item);
            default -> new TextCharacter(' ');
        };
    }

    private static TextCharacter fromSuit(Suit suit) {
        return switch (suit) {
            case ANIMAL -> new TextCharacter('A', TextColor.ANSI.CYAN, TextColor.ANSI.DEFAULT);
            case MUSHROOM -> new TextCharacter('F', TextColor.ANSI.RED, TextColor.ANSI.DEFAULT);
            case INSECT -> new TextCharacter('I', TextColor.ANSI.MAGENTA, TextColor.ANSI.DEFAULT);
            case PLANT -> new TextCharacter('P', TextColor.ANSI.GREEN, TextColor.ANSI.DEFAULT);
        };
    }

    private static TextCharacter fromItem(Item item) {
        return switch (item) {
            case QUILL -> new TextCharacter('Q', TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.DEFAULT);
            case INKWELL -> new TextCharacter('K', TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.DEFAULT);
            case MANUSCRIPT -> new TextCharacter('M', TextColor.ANSI.YELLOW_BRIGHT, TextColor.ANSI.DEFAULT);
        };
    }
}
