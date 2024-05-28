package IS24_LB11.game.symbol;

import IS24_LB11.game.utils.SyntaxException;

/**
 * The <code>Suit</code> enum represents various suits in the game.
 * Each suit can be represented by a single character.
 */
public enum Suit implements Symbol {
    /**
     * Represents an animal suit.
     */
    ANIMAL,

    /**
     * Represents a mushroom suit.
     */
    MUSHROOM,

    /**
     * Represents an insect suit.
     */
    INSECT,

    /**
     * Represents a plant suit.
     */
    PLANT;

    /**
     * Converts a character to its corresponding <code>Suit</code>.
     *
     * @param c the character representing the suit
     * @return the <code>Suit</code> corresponding to the character
     * @throws SyntaxException if the character does not correspond to any <code>Suit</code>
     */
    public static Suit fromCharacter(Character c) throws SyntaxException {
        if (c == 'A') return ANIMAL;
        if (c == 'F') return MUSHROOM;
        if (c == 'I') return INSECT;
        if (c == 'P') return PLANT;
        if (c == Symbol.nullChar) return null;
        throw new SyntaxException(String.format(Symbol.INVALID_CHAR_MSG, c));
    }

    /**
     * Checks if a character is a valid representation of a <code>Suit</code>.
     *
     * @param c the character to check
     * @return <code>true</code> if the character is a valid representation of a <code>Suit</code>, <code>false</code> otherwise
     */
    public static boolean isValidChar(Character c) {
        return c == 'A' || c == 'F' || c == 'I' || c == 'P';
    }
}
