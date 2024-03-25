package IS24_LB11.game.symbol;

import IS24_LB11.game.utils.SyntaxException;

public enum CodexObj implements Symbol {
    QUILL('Q'),
    INKWELL('K'),
    MANUSCRIPT('M');

    private final Character symbol;

    CodexObj(Character symbol) {
        this.symbol = symbol;
    }

    public Character getSymbol() {
        return symbol;
    }

    public static CodexObj fromCharacter(Character c) throws SyntaxException {
        if (c == 'Q') return QUILL;
        if (c == 'K') return INKWELL;
        if (c == 'M') return MANUSCRIPT;
        throw new SyntaxException(String.format(Symbol.INVALID_CHAR_MSG, c));
    }

    public static boolean isValidChar(Character c) {
        return c == 'Q' || c == 'K' || c == 'M';
    }
}
