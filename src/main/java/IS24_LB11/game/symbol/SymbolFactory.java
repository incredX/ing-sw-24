package IS24_LB11.game.symbol;

import org.wasd.game.utils.SyntaxException;

public class SymbolFactory {
    public static Symbol fromCharacter(Character c) throws SyntaxException {
        if (Suit.isValidChar(c)) return Suit.fromCharacter(c);
        if (CodexObj.isValidChar(c)) return CodexObj.fromCharacter(c);
        if (EmptySymbol.isValidChar(c)) return new EmptySymbol();
        if (c == Symbol.nullChar) return null;
        throw new SyntaxException(String.format(Symbol.INVALID_CHAR_MSG, c));
    }
}
