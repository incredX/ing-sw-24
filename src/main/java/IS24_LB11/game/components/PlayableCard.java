package IS24_LB11.game.components;

import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.SerialObject;

import java.util.HashMap;
import java.util.function.Consumer;

public interface PlayableCard extends SerialObject {
    void updateCounters(HashMap<Symbol, Integer> counters);
    void forEachCorner(Consumer<Symbol> consumer);
    void forEachDirection(Consumer<Integer> consumer);
    void flip();
    Symbol getSuit();
    Symbol getCorner(int dir);
    int getPoints();
    boolean hasCorner(int dir);
    boolean isFaceDown();
}
