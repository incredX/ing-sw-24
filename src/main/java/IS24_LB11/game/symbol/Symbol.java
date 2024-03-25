package IS24_LB11.game.symbol;

public interface Symbol {
    String INVALID_CHAR_MSG = "invalid char ('%c')";
    Character nullChar = '_';
    Character getSymbol();
}
