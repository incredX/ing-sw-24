package IS24_LB11.game.components;

import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.SyntaxException;

/**
 * Factory class for creating different types of cards.
 */
public class CardFactory {

    /**
     * Creates a new instance of a PlayableCard based on the provided string identifier.
     * The identifier's first character determines the type of card to be created:
     * - 'N' for NormalCard
     * - 'G' for GoldenCard
     * - 'S' for StarterCard
     *
     * @param str the string identifier for the card
     * @return a new instance of PlayableCard
     * @throws SyntaxException if the string is empty or contains an invalid identifier
     */
    public static PlayableCard newPlayableCard(String str) throws SyntaxException {
        if(str.isEmpty()) {
            throw new SyntaxException("Empty string id");
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

    /**
     * Creates a new instance of a CardInterface based on the provided string identifier.
     * The identifier's first character determines the type of card to be created:
     * - 'N' for NormalCard
     * - 'G' for GoldenCard
     * - 'S' for StarterCard
     * - 'O' for GoalSymbol or GoalPattern
     *
     * @param str the string identifier for the card
     * @return a new instance of CardInterface
     * @throws SyntaxException if the string is empty or contains an invalid identifier
     */
    public static CardInterface newSerialCard(String str) throws SyntaxException {
        if(str.isEmpty()) {
            throw new SyntaxException("Empty string id");
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
