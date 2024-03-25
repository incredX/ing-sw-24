package IS24_LB11.game.components;

import org.wasd.game.utils.SerialObject;
import org.wasd.game.symbol.Symbol;

import java.util.HashMap;

public interface Card extends SerialObject {
    void updateCounters(HashMap<Symbol, Integer> counters);
    void flip();
    Symbol getSuit();
    Symbol getCorner(int dir);
    int getPoints();
    boolean hasCorner(int dir);
    boolean isFaceDown();
}
