package IS24_LB11.game.components;

import IS24_LB11.game.symbol.Empty;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.*;

        import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Represents a normal card in the game with suit, corners, face state, and points.
 */
public class NormalCard implements PlayableCard {
    protected final Suit mainSuit;
    protected final Corners frontCorners;
    protected boolean faceDown;
    protected final int points;

    /**
     * Construct a <code>NormalCard</code> from the information encoded in the given <code>String</code> with the following syntax:
     * <code>char</code> 0-3 : represent the card's corners. Must be parsable into <code>Symbol</code>
     * <code>char</code> 4   : represents the card's <code>Suit</code> and must be parsable as such
     * <code>char</code> 5   : represents the card's visible face ('F': front, 'B': back)
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
        if (points < 0 || points > 5) {
            throw new SyntaxException(String.format(NOT_A_DIGIT_MSG, id.charAt(6)));
        }
    }

    /**
     * Returns a string that identifies the card and its state with the syntax described in <code>NormalCard</code>.
     *
     * @return a <code>String</code> that represents the card
     */
    public String asString() {
        String str = "N";
        str += frontCorners.asString();
        str += Symbol.toChar(mainSuit);
        str += (faceDown) ? 'B' : 'F';
        str += points;
        return str;
    }

    /**
     * Updates the provided counters with the card's suit or corners depending on its face state.
     *
     * @param counters a <code>HashMap</code> of <code>Symbol</code> and <code>Integer</code> representing the counters
     */
    public void updateCounters(HashMap<Symbol, Integer> counters) {
        if (faceDown) {
            counters.computeIfPresent(mainSuit, ((symbol, integer) -> integer + 1));
            return;
        }
        frontCorners.updateCounters(counters);
    }

    /**
     * Applies the provided consumer to each corner symbol of the card.
     *
     * @param consumer a <code>Consumer</code> of <code>Symbol</code>
     */
    @Override
    public void forEachCorner(Consumer<Symbol> consumer) {
        for (int i = 0; i < 4; i++) {
            if (hasCorner(i)) consumer.accept(getCorner(i));
        }
    }

    /**
     * Applies the provided consumer to each direction of the card.
     *
     * @param consumer a <code>Consumer</code> of <code>Integer</code>
     */
    @Override
    public void forEachDirection(Consumer<Integer> consumer) {
        for (int i = 0; i < 4; i++) {
            consumer.accept(i);
        }
    }

    /**
     * Flips the card, changing its face state.
     */
    public void flip() {
        faceDown = !faceDown;
    }

    /**
     * Returns the suit of the card.
     *
     * @return the card's <code>Suit</code>
     */
    public Symbol getSuit() {
        return mainSuit;
    }

    /**
     * Returns the symbol of the specified corner direction.
     *
     * @param dir the direction of the corner as an integer
     * @return the <code>Symbol</code> of the corner
     */
    public Symbol getCorner(int dir) {
        if (faceDown) return Empty.symbol();
        return frontCorners.getCorner(Direction.parse(dir));
    }

    /**
     * Returns the symbol of the specified corner direction.
     *
     * @param direction the direction of the corner as a <code>Direction</code>
     * @return the <code>Symbol</code> of the corner
     */
    public Symbol getCorner(Direction direction) {
        if (faceDown) return Empty.symbol();
        return frontCorners.getCorner(direction);
    }

    /**
     * Returns the points of the card.
     *
     * @return the points as an integer
     */
    public int getPoints() {
        return points;
    }

    /**
     * Checks if the card has a corner symbol in the specified direction.
     *
     * @param dir the direction of the corner as an integer
     * @return <code>true</code> if the card has a corner symbol, <code>false</code> otherwise
     */
    public boolean hasCorner(int dir) {
        return faceDown || frontCorners.hasCorner(Direction.parse(dir));
    }

    /**
     * Checks if the card has a corner symbol in the specified direction.
     *
     * @param direction the direction of the corner as a <code>Direction</code>
     * @return <code>true</code> if the card has a corner symbol, <code>false</code> otherwise
     */
    public boolean hasCorner(Direction direction) {
        return faceDown || frontCorners.hasCorner(direction);
    }

    /**
     * Returns whether the card is face down.
     *
     * @return <code>true</code> if the card is face down, <code>false</code> otherwise
     */
    public boolean isFaceDown() {
        return faceDown;
    }

    /**
     * Checks if this <code>NormalCard</code> is equal to another <code>PlayableCard</code>. The cards are considered equal if their string representations,
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
}
