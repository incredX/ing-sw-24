package IS24_LB11.game.components;

import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.SyntaxException;

public class CardFactory {
    public static PlayableCard newPlayableCard(String str) throws SyntaxException {
        if(str.isEmpty()) {
            throw new  SyntaxException("Empty string id");
        }
        char c = str.charAt(0);
        try {
            if (c == 'N') return new NormalCard(str.substring(1));
            if (c == 'G') return new GoldenCard(str.substring(1));
            if (c == 'S') return new StarterCard(str.substring(1));
        } catch (SyntaxException e) {
            throw e.addContext("in " + str);
        }
        throw new SyntaxException(String.format(Symbol.INVALID_CHAR_MSG, c));
    }

    public static CardInterface newSerialCard(String str) throws SyntaxException {
        if(str.isEmpty()) {
            throw new  SyntaxException("Empty string id");
        }
        char c = str.charAt(0);
        try {
            if (c == 'N') return new NormalCard(str.substring(1));
            if (c == 'G') return new GoldenCard(str.substring(1));
            if (c == 'S') return new StarterCard(str.substring(1));
            if (c == 'O') {
                if (str.length() == 5) return new GoalSymbol(str.substring(1));
                else return new GoalPattern(str.substring(1));

            }
        } catch (SyntaxException e) {
            throw e.addContext("in " + str);
        }
        throw new SyntaxException(String.format(Symbol.INVALID_CHAR_MSG, c)).addContext("in " + str);
    }
}
