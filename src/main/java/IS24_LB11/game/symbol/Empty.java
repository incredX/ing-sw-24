package IS24_LB11.game.symbol;

public class Empty implements Symbol {
    public static final char CHAR = 'E';
    private static final Empty EMPTY = new Empty();
    private Empty() {}
    public static Empty symbol() { return EMPTY; }
    public static boolean isValidChar(Character c) {
        return c == CHAR;
    }
}
