package IS24_LB11.game.components;

import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.SyntaxException;

public class GoalPattern extends GoalSymbol {
    private final char variant;
    private final int dir;

    /**
     * Construct a <code>GoalPattern</code> from the information encoded in the given <code>String</code> with the following syntax:
     * <code>char</code> 1-4 : same syntax followed by <code>GoalSymbol</code>
     * <code>char</code> 5   : represents the pattern's variant (expected 'D' or 'L')
     * <code>char</code> 6   : represents the pattern's direction
     *
     * @param id a <code>String</code> containing the encoded information that identify the card
     * @throws SyntaxException if the information are not correctly encoded or missing
     */
    public GoalPattern(String id) throws SyntaxException {
        super(id);
        if (id.length() < 7) {
            throw new SyntaxException(String.format(SHORT_ID_MSG, id));
        }
        char c8 = id.charAt(6);

        variant = id.charAt(5);
        dir = c8 - '0';

        if (dir < 0 || dir > 9) {
            throw new SyntaxException(String.format(NOT_A_DIGIT_MSG, c8));
        }

        if (variant == 'L') {
            if (dir >= 4) {
                throw new SyntaxException(String.format(INVALID_DIGIT_MSG, c8))
                        .addContext("(expected a digit in range 0..=3)");
            }
        } else if (variant == 'D') {
            if (dir >= 2) {
                throw new SyntaxException(String.format(INVALID_DIGIT_MSG, c8))
                        .addContext("(expected a digit in range 0..=2)");
            }
        } else {
            throw new SyntaxException(String.format(Symbol.INVALID_CHAR_MSG, variant));
        }
    }

    @Override
    public String asString() {
        String str = super.asString();
        str += "P" + variant + dir;
        return str;
    }

    public char getVariant() { return variant; }

    public int getDir() { return dir; }
}
