package IS24_LB11.game.components;

import org.wasd.game.symbol.Symbol;
import org.wasd.game.symbol.SymbolFactory;
import org.wasd.game.utils.*;

import java.util.ArrayList;

public class GoalSymbol implements SerialObject {
    protected final ArrayList<Symbol> patSymbols;
    protected final int points;

    public GoalSymbol(String id) throws SyntaxException {
        if (id.length() < 5) {
            throw new SyntaxException(String.format(SHORT_ID_MSG, id));
        }
        points = id.charAt(2) - '0';
        if (points < 0 || points > 9) {
            throw new SyntaxException(String.format(NOT_A_DIGIT_MSG, id.charAt(1)));
        }
        if (points != 2 && points != 3) {
            throw new SyntaxException(String.format(INVALID_DIGIT_MSG, id.charAt(1)))
                    .addContext("(expected digits: 2 or 3)");
        }

        patSymbols = new ArrayList<>();
        for (int i = 3; i < 6; i++) {
            patSymbols.add(SymbolFactory.fromCharacter(id.charAt(i)));
        }
    }

    @Override
    public String asString() {
        String str = "O";
        str += points;
        str += patSymbols.stream().map(s -> s.getSymbol().toString()).reduce("", (acc, s) -> acc+s);
        return str;
    }
}
