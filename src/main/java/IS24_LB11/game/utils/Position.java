package IS24_LB11.game.utils;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Position position) {
        return x == position.x && y == position.y;
    }

    public Position withRelative(Position delta) {
        return withRelative(delta.x, delta.y);
    }

    public Position withRelative(int deltaX, int deltaY) {
        return new Position(x+deltaX, y+deltaY);
    }

    public Position transform(Function<Integer, Integer> function) {
        return new Position(function.apply(x), function.apply(y));
    }

    public Position transformY(Function<Integer, Integer> function) {
        return new Position(x, function.apply(y));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() { return String.format("%2d;%2d", getX(), getY()); }
}
