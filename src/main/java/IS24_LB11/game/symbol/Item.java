package IS24_LB11.game.symbol;

import IS24_LB11.game.utils.SyntaxException;

public enum Item implements Symbol {
    QUILL,
    INKWELL,
    MANUSCRIPT;

    public static Item fromCharacter(Character c) throws SyntaxException {
        if (c == 'Q') return QUILL;
        if (c == 'K') return INKWELL;
        if (c == 'M') return MANUSCRIPT;
        throw new SyntaxException(String.format(Symbol.INVALID_CHAR_MSG, c));
    }

    public static boolean isValidChar(Character c) {
        return c == 'Q' || c == 'K' || c == 'M';
    }
}
