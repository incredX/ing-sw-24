package IS24_LB11.game.components;

import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.SyntaxException;

import java.util.ArrayList;

/**
 * Represents a Golden Card in the game, which extends the functionality of a {@link NormalCard} by adding specific
 * suits needed and a condition to win points.
 */
public class GoldenCard extends NormalCard {
    private final ArrayList<Suit> suitsNeeded;
    private final Symbol pointsCondition;

    /**
     * Construct a <code>GoldenCard</code> from the information encoded in the given <code>String</code> with the following syntax:
     * <code>char</code> 0-6 : same syntax followed by <code>NormalCard</code>
     * <code>char</code> 7   : represents the card's condition to win its points. Must be parsable into <code>Symbol</code>. In this case ' ' stands for corners and '_' stands for no-condition
     * <code>char</code> 8-12: represent the card's suits needed to be played
     *
     * @param id a <code>String</code> containing the encoded information that identify the card
     * @throws SyntaxException if the information are not correctly encoded or missing
     * @see NormalCard
     */
    public GoldenCard(String id) throws SyntaxException {
        super(id);
        if (id.length() < 13) {
            throw new SyntaxException(String.format(SHORT_ID_MSG, id));
        }
        pointsCondition = Symbol.fromChar(id.charAt(7));
        suitsNeeded = new ArrayList<>();
        for (int i = 8; i < 13; i++) {
            suitsNeeded.add(Suit.fromCharacter(id.charAt(i)));
        }
    }

    /**
     * Checks if this <code>GoldenCard</code> is equal to another <code>PlayableCard</code>. The cards are considered equal if their string representations,
     * with 'B' replaced by 'F', are the same.
     *
     * @param other the other <code>PlayableCard</code> to compare with
     * @return <code>true</code> if the cards are equal, <code>false</code> otherwise
     */
    @Override
    public boolean equals(PlayableCard other) {
        String id = asString().replace('B', 'F');
        String otherId = other.asString().replace('B', 'F');
        return id.equals(otherId);
    }

    /**
     * Returns the string representation of the <code>GoldenCard</code>.
     *
     * @return a string that represents the <code>GoldenCard</code>
     */
    @Override
    public String asString() {
        String str = super.asString();
        str = str.replace(str.charAt(0), 'G');
        str += Symbol.toChar(pointsCondition);
        str += suitsNeeded.stream().map(s -> Symbol.toChar(s).toString()).reduce("", (acc, s) -> acc + s);
        return str;
    }

    /**
     * Returns the condition to win points for this <code>GoldenCard</code>.
     *
     * @return the points condition as a {@link Symbol}
     */
    public Symbol getPointsCondition() {
        return pointsCondition;
    }

    /**
     * Returns the list of suits needed to play this <code>GoldenCard</code>.
     *
     * @return an <code>ArrayList</code> of {@link Suit}
     */
    public ArrayList<Suit> getSuitsNeeded() {
        return suitsNeeded;
    }
}
