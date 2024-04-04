package IS24_LB11.game.utils;

import IS24_LB11.game.symbol.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static IS24_LB11.game.components.CardInterface.SHORT_ID_MSG;

public class Corners {
    public static final int UP_LEFT = 0, UP_RIGHT = 1, DOWN_LEFT = 2, DOWN_RIGHT = 3;

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

    public boolean hasCorner(int dir) {
        return isCorner(dir) && corners.get(dir) != null;
    }

    public static Position getRelativePosition(int dir) {
        return new Position(2*(dir&1)-1, (dir&2)-1);
    }

    public Symbol getCorner(int dir) {
        if (isCorner(dir)) return corners.get(dir);
        return null;
    }

    public static int opposite(int direction) {
        return direction^3;
    }
    public static boolean isUp(int direction) { return (direction>>1) == 0; }
    public static boolean isRight(int direction) { return (direction&1) == 1; }
    public static boolean isCorner(int direction) { return direction >= 0 && direction <= 3; }
}