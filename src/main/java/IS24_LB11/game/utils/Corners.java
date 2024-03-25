package IS24_LB11.game.utils;

import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.symbol.SymbolFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Corners {
    public static final int UpRight = 0;
    public static final int UpLeft = 1;
    public static final int DownLeft = 2;
    public static final int DownRight = 3;

    private final ArrayList<Symbol> corners;

    public Corners(String id) throws SyntaxException {
        corners = new ArrayList<>();

        for (int i=0; i<4; i++) {
            if (i >= id.length()) {
                corners.add(null);
                continue;
            }
            Character c = id.charAt(i);
            corners.add(SymbolFactory.fromCharacter(c));
        }
    }

    public String asString() {
        return corners.stream()
                .map(s -> (s != null) ? s.getSymbol() : Symbol.nullChar)
                .map(Object::toString)
                .reduce("", (acc, s) -> acc+s);
    }

    public void updateCorners(HashMap<Symbol, Integer> counters) {
        corners.stream()
                .filter(Objects::nonNull)
                .forEach(s -> counters.computeIfPresent(s, ((symbol, integer) -> integer+1)));
    }

    public boolean hasCorner(int dir) {
        return dir >= 0 && dir <= 3 && corners.get(dir) != null;
    }

    public Symbol getCorner(int dir) {
        if (dir >= 0 && dir <= 3) return corners.get(dir);
        return null;
    }

    public static boolean isUp(int dir) { return dir <= UpLeft; }
    public static boolean isRight(int dir) { return dir%3 == UpRight; }
}
