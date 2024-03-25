package IS24_LB11.game.symbol;

public class EmptySymbol implements Symbol {
    @Override
    public Character getSymbol() {
        return ' ';
    }

    public static boolean isValidChar(Character c) {
        return c == ' ';
    }
}
