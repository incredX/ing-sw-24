package IS24_LB11.game.components;

import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class GoalSymbol implements GoalCard {
    protected final ArrayList<Symbol> symbols;
    protected final int points;

    /**
     * Construct a <code>NormalCard</code> from the information encoded in the given <code>String</code> with the following syntax:
     * <code>char</code> 1   : represent the goal's points. Must be parsable into an int between 2 and 3
     * <code>char</code> 2-4 : represents the goal's <code>Symbol</code>s needed. If less than 3 symbols are required, the remaining chars must be '_'
     * <code>char</code> 5   : represents the card's visible face ('f': front, 'b': back)
     * <code>char</code> 6   : represents the card's points. Must be parsable into an int between 0 and 9
     *
     * @param id a <code>String</code> containing the encoded information that identify the card
     * @throws SyntaxException if the information are not correctly encoded or missing
     */
    public GoalSymbol(String id) throws SyntaxException {
        if (id.length() < 5) {
            throw new SyntaxException(String.format(SHORT_ID_MSG, id));
        }
        points = id.charAt(1) - '0';
        if (points < 0 || points > 9) {
            throw new SyntaxException(String.format(NOT_A_DIGIT_MSG, id.charAt(1)));
        }
        if (points != 2 && points != 3) {
            throw new SyntaxException(String.format(INVALID_DIGIT_MSG, id.charAt(1)))
                    .addContext("(expected digits: 2 or 3)");
        }

        symbols = new ArrayList<>();
        for (int i = 2; i < 5; i++) {
            symbols.add(Symbol.fromChar(id.charAt(i)));
        }
    }

    @Override
    public String asString() {
        String str = "O";
        str += points;
        str += symbols.stream().map(s -> Symbol.toChar(s).toString()).reduce("", (acc, s) -> acc+s);
        return str;
    }

    public int getPoints() { return points; }

    public ArrayList<Symbol> getSymbols() {
        return (ArrayList<Symbol>) symbols.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}