package IS24_LB11.game.components;

import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.*;

        import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a starter card in the game, extending the functionality of a normal card with additional central suits and back corners.
 */
public class StarterCard extends NormalCard {
    private final ArrayList<Suit> centralSuits;
    private final Corners backCorners;

    /**
     * Construct a <code>StarterCard</code> from the information encoded in the given <code>String</code> with the following syntax:
     * <code>char</code> 0-6   : same syntax followed by <code>NormalCard</code>
     * <code>char</code> 7-9   : represents the suits found in the center of the card. If less than 3 suits are needed, is expected to be filled with '_'
     * <code>char</code> 10-13 : represent the suits in the corners of the back of the card
     *
     * @param id a <code>String</code> containing the encoded information that identify the card
     * @throws SyntaxException if the information are not correctly encoded or missing
     * @see NormalCard
     */
    public StarterCard(String id) throws SyntaxException {
        super(id);
        if (id.length() < 14) {
            throw new SyntaxException(String.format(SHORT_ID_MSG, id));
        }
        centralSuits = new ArrayList<>();
        for (int i = 7; i < 10; i++) {
            centralSuits.add(Suit.fromCharacter(id.charAt(i)));
        }
        backCorners = new Corners(id.substring(10));
    }

    /**
     * Returns a string that identifies the card and its state with the syntax described in <code>StarterCard</code>.
     *
     * @return a <code>String</code> that represents the card
     */
    public String asString() {
        String str = super.asString();
        str = str.replace(str.charAt(0), 'S');
        str += centralSuits.stream().map(s -> Symbol.toChar(s).toString()).reduce("", (acc, s) -> acc + s);
        str += backCorners.asString();
        return str;
    }

    /**
     * Updates the provided counters with the card's suits and corners depending on its face state.
     *
     * @param counters a <code>HashMap</code> of <code>Symbol</code> and <code>Integer</code> representing the counters
     */
    @Override
    public void updateCounters(HashMap<Symbol, Integer> counters) {
        if (faceDown) {
            backCorners.updateCounters(counters);
        } else {
            frontCorners.updateCounters(counters);
            centralSuits.forEach(symbol -> counters.computeIfPresent(symbol, ((s, cnt) -> cnt + 1)));
        }
    }

    /**
     * Returns the symbol of the specified corner direction.
     *
     * @param dir the direction of the corner as an integer
     * @return the <code>Symbol</code> of the corner
     */
    public Symbol getCorner(int dir) {
        if (faceDown) return backCorners.getCorner(Direction.parse(dir));
        return frontCorners.getCorner(Direction.parse(dir));
    }

    /**
     * Returns the symbol of the specified corner direction.
     *
     * @param direction the direction of the corner as a <code>Direction</code>
     * @return the <code>Symbol</code> of the corner
     */
    public Symbol getCorner(Direction direction) {
        if (faceDown) return backCorners.getCorner(direction);
        return frontCorners.getCorner(direction);
    }

    /**
     * Returns the suits found in the center of the card.
     *
     * @return an <code>ArrayList</code> of <code>Suit</code> representing the central suits
     */
    public ArrayList<Suit> getCentralSuits() {
        return centralSuits;
    }
}
