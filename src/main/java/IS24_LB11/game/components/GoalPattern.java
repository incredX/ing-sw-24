package IS24_LB11.game.components;

import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.SyntaxException;

public class GoalPattern extends GoalSymbol {
    private final char variant;
    private final int dir;

    public GoalPattern(String id) throws SyntaxException {
        super(id);
        if (id.length() < 10) {
            throw new SyntaxException(String.format(SHORT_ID_MSG, id));
        }
        char c8 = id.charAt(8);

        variant = id.charAt(7);
        dir = c8 - '0';

        if (dir < 0 || dir > 9) {
            throw new SyntaxException(String.format(NOT_A_DIGIT_MSG, c8));
        }

        if (variant == 'L') {
            if (dir >= 4) {
                throw new SyntaxException(String.format(INVALID_DIGIT_MSG, c8))
                        .addContext("(expected a digit in range 0..=3)");
            }
        } else if (variant == 'X') {
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
}
