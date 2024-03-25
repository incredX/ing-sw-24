package IS24_LB11.game.symbol;

import IS24_LB11.game.utils.SyntaxException;

public class SymbolFactory {
    public static Symbol fromCharacter(Character c) throws SyntaxException {
        if (Suit.isValidChar(c)) return Suit.fromCharacter(c);
        if (Item.isValidChar(c)) return Item.fromCharacter(c);
        if (Empty.isValidChar(c)) return Empty.symbol();
        if (c == Symbol.nullChar) return null;
        throw new SyntaxException(String.format(Symbol.INVALID_CHAR_MSG, c));
    }
}
