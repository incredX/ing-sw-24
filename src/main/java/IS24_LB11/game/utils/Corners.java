package IS24_LB11.game.utils;

import IS24_LB11.game.symbol.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static IS24_LB11.game.components.CardInterface.SHORT_ID_MSG;

public class Corners {
    private final ArrayList<Symbol> corners;

    public Corners(String id) throws SyntaxException {
        if (id.length() < 4) throw new SyntaxException(String.format(SHORT_ID_MSG, id));

        corners = new ArrayList<>();

        for (int i=0; i<4; i++) {
            Character c = id.charAt(i);
            corners.add(Symbol.fromChar(c));
        }
    }

    public String asString() {
        return corners.stream()
                .map(s -> (s != null) ? Symbol.toChar(s) : Symbol.nullChar)
                .map(Object::toString)
                .reduce("", (acc, s) -> acc+s);
    }

    public void updateCounters(HashMap<Symbol, Integer> counters) {
        corners.stream()
                .filter(Objects::nonNull)
                .forEach(s -> counters.computeIfPresent(s, ((symbol, integer) -> integer+1)));
    }

    public boolean hasCorner(Direction dir) {
        return corners.get(dir.ordinal()) != null;
    }

    public Symbol getCorner(Direction dir) {
        return corners.get(dir.ordinal());
    }
}