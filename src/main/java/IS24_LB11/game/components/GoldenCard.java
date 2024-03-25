package IS24_LB11.game.components;

import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.SyntaxException;

import java.util.ArrayList;

public class GoldenCard extends NormalCard {
    private final ArrayList<Suit> suitsNeeded;
    private final Symbol pointsCondition;

    /**
     * Construct a <code>GoldenCard</code> from the information encoded in the given <code>String</code> with the following syntax:
     * <code>char</code> 0-6 : same syntax followed by <code>NormalCard</code>
     * <code>char</code> 7   : represents the card's condition to win its points. Must be parsable into <code>Symbol</code>. In this case ' ' stands for corners and '_' stands for no-condition
     * <code>char</code> 8-* : represent the card's suits needed to be played
     *
     * @param id a <code>String</code> containing the encoded information that identify the card
     * @throws SyntaxException if the information are not correctly encoded or missing
     * @see NormalCard
     */
    public GoldenCard(String id) throws SyntaxException {
        super(id);
        if (id.length() < 9) {
            throw new SyntaxException(String.format(SHORT_ID_MSG, id));
        }
        pointsCondition = Symbol.fromChar(id.charAt(7));
        suitsNeeded = new ArrayList<>();
        for (int i = 8; i < id.length(); i++) {
            suitsNeeded.add(Suit.fromCharacter(id.charAt(i)));
        }
    }

    @Override
    public String asString() {
        String str = super.asString();
        str += Symbol.toChar(pointsCondition);
        str += suitsNeeded.stream().map(s -> Symbol.toChar(s).toString()).reduce("", (acc, s) -> acc+s);
        return str;
    }

    public Symbol getPointsCondition() { return pointsCondition; }

    public ArrayList<Suit> getSuitsNeeded() { return suitsNeeded; }
}