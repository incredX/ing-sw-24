package IS24_LB11.game.symbol;

import IS24_LB11.game.utils.SyntaxException;

public interface Symbol {
    String INVALID_CHAR_MSG = "invalid char ('%c')";
    Character nullChar = ' ';

    //Character getChar();

    static Symbol fromChar(Character c) throws SyntaxException {
        if (Suit.isValidChar(c)) return Suit.fromCharacter(c);
        if (Item.isValidChar(c)) return Item.fromCharacter(c);
        if (c == Empty.CHAR) return Empty.symbol();
        if (c == Symbol.nullChar) return null;
        throw new SyntaxException(String.format(Symbol.INVALID_CHAR_MSG, c));
    }

    static Character toChar(Symbol symbol) {
        if (symbol == Suit.ANIMAL) return 'A';
        if (symbol == Suit.MUSHROOM) return 'F';
        if (symbol == Suit.INSECT) return 'I';
        if (symbol == Suit.PLANT) return 'P';
        if (symbol == Item.QUILL) return 'Q';
        if (symbol == Item.INKWELL) return 'K';
        if (symbol == Item.MANUSCRIPT) return 'M';
        if (symbol.equals(Empty.symbol())) return Empty.CHAR;
        return nullChar;
    }
}
