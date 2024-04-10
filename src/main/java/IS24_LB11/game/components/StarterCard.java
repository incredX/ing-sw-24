package IS24_LB11.game.components;

import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.*;

import java.util.ArrayList;
import java.util.HashMap;

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

    public String asString() {
        String str = super.asString();
        str = str.replace(str.charAt(0),'S');
        str += centralSuits.stream().map(s -> Symbol.toChar(s).toString()).reduce("", (acc, s) -> acc+s);
        str += backCorners.asString();
        return str;
    }

    @Override
    public void updateCounters(HashMap<Symbol, Integer> counters) {
        if (faceDown) {
            backCorners.updateCounters(counters);
        }
        centralSuits.forEach(symbol -> counters.computeIfPresent(symbol, ((s, cnt) -> cnt+1)));
    }

    public Symbol getCorner(int dir) {
        if (faceDown) return backCorners.getCorner(Direction.parse(dir));
        return frontCorners.getCorner(Direction.parse(dir));
    }

    public Symbol getCorner(Direction direction) {
        if (faceDown) return backCorners.getCorner(direction);
        return frontCorners.getCorner(direction);
    }

    public ArrayList<Suit> getCentralSuits() { return centralSuits; }
}