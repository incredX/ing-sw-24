package IS24_LB11.game.components;

import IS24_LB11.game.symbol.Empty;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.*;

import java.util.HashMap;

public class NormalCard implements PlayableCard {
    protected final Suit mainSuit;
    protected final Corners frontCorners;
    protected boolean faceDown;
    protected final int points;

    /**
     * Construct a <code>NormalCard</code> from the information encoded in the given <code>String</code> with the following syntax:
     * <code>char</code> 0-3 : represent the card's corners. Must be parsable into <code>Symbol</code>
     * <code>char</code> 4   : represents the card's <code>Suit</code> and must be parsable as such
     * <code>char</code> 5   : represents the card's visible face ('f': front, 'b': back)
     * <code>char</code> 6   : represents the card's points. Must be parsable into an int between 0 and 9
     *
     * @param id a <code>String</code> containing the encoded information that identify the card
     * @throws SyntaxException if the information are not correctly encoded or missing
     */
    public NormalCard(String id) throws SyntaxException {
        if (id.length() < 7) {
            throw new SyntaxException(String.format(SHORT_ID_MSG, id));
        }
        frontCorners = new Corners(id);
        mainSuit = Suit.fromCharacter(id.charAt(4));
        char charFace = id.charAt(5);
        if (charFace == 'B' || charFace == 'F') {
            faceDown = charFace == 'B';
        } else {
            throw new SyntaxException(String.format(Symbol.INVALID_CHAR_MSG, charFace));
        }
        points = id.charAt(6) - '0';
        if (points < 0 || points > 9) {
            throw new SyntaxException(String.format(NOT_A_DIGIT_MSG, id.charAt(6)));
        }
    }

    /**
     *
     * @return a <code>String</code> that identify the card and its state with the syntax described in <code>NormalCard</code>
     */
    public String asString() {
        String str = "R";
        str += frontCorners.asString();
        str += Symbol.toChar(mainSuit);
        str += (faceDown) ? 'B' : 'F';
        str += points;
        return str;
    }

    public void updateCounters(HashMap<Symbol, Integer> counters) {
        if (faceDown) {
            counters.computeIfPresent(mainSuit, ((symbol, integer) -> integer+1));
        }
        frontCorners.updateCounters(counters);
    }

    public void flip() {
        faceDown = !faceDown;
    }

    public Symbol getSuit() { return mainSuit; }

    public Symbol getCorner(int dir) {
        if (faceDown) return Empty.symbol();
        return frontCorners.getCorner(dir);
    }

    public int getPoints() { return points; }

    public boolean hasCorner(int dir) {
        return faceDown || frontCorners.hasCorner(dir);
    }

    public boolean isFaceDown() { return faceDown; }
}