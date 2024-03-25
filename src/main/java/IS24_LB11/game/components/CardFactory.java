package IS24_LB11.game.components;

import IS24_LB11.game.utils.SerialObject;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.SyntaxException;

public class CardFactory {
    public static Card newCard(String str) throws SyntaxException {
        char c = str.charAt(0);
        try {
            if (c == 'R') return new NormalCard(str.substring(1));
            if (c == 'G') return new GoldenCard(str.substring(1));
            if (c == 'S') return new StarterCard(str.substring(1));
        } catch (SyntaxException e) {
            throw e.addContext("in " + str);
        }
        throw new SyntaxException(String.format(Symbol.INVALID_CHAR_MSG, c));
    }

    public static SerialObject newSerialCard(String str) throws SyntaxException {
        char c = str.charAt(0);
        if (c == 'R') return new NormalCard(str.substring(1));
        if (c == 'G') return new GoldenCard(str.substring(1));
        if (c == 'S') return new StarterCard(str.substring(1));
        if (c == 'O') return new GoalSymbol(str.substring(1));
        else throw new SyntaxException(String.format(Symbol.INVALID_CHAR_MSG, c));
    }
}
