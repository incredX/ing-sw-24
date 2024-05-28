package IS24_LB11.game.symbol;

import IS24_LB11.game.utils.SyntaxException;

/**
 * The <code>Item</code> enum represents various items in the game.
 * Each item can be represented by a single character.
 */
public enum Item implements Symbol {
    /**
     * Represents a quill item.
     */
    QUILL,

    /**
     * Represents an inkwell item.
     */
    INKWELL,

    /**
     * Represents a manuscript item.
     */
    MANUSCRIPT;

    /**
     * Converts a character to its corresponding <code>Item</code>.
     *
     * @param c the character representing the item
     * @return the <code>Item</code> corresponding to the character
     * @throws SyntaxException if the character does not correspond to any <code>Item</code>
     */
    public static Item fromCharacter(Character c) throws SyntaxException {
        if (c == 'Q') return QUILL;
        if (c == 'K') return INKWELL;
        if (c == 'M') return MANUSCRIPT;
        throw new SyntaxException(String.format(Symbol.INVALID_CHAR_MSG, c));
    }

    /**
     * Checks if a character is a valid representation of an <code>Item</code>.
     *
     * @param c the character to check
     * @return <code>true</code> if the character is a valid representation of an <code>Item</code>, <code>false</code> otherwise
     */
    public static boolean isValidChar(Character c) {
        return c == 'Q' || c == 'K' || c == 'M';
    }
}
