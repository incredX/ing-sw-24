package IS24_LB11.game.symbol;

import IS24_LB11.game.utils.SyntaxException;

public enum Suit implements Symbol {
    ANIMAL,
    MUSHROOM,
    INSECT,
    PLANT;

    public static Suit fromCharacter(Character c) throws SyntaxException {
        if (c == 'A') return ANIMAL;
        if (c == 'F') return MUSHROOM;
        if (c == 'I') return INSECT;
        if (c == 'P') return PLANT;
        if (c == '_') return null;
        throw new SyntaxException(String.format(Symbol.INVALID_CHAR_MSG, c));
    }

    public static boolean isValidChar(Character c) {
        return c == 'A' || c == 'F' || c == 'I' || c == 'P';
    }
}