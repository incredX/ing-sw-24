package IS24_LB11.game.utils;

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

    public Position withRelative(int deltaX, int deltaY) {
        return new Position(x+deltaX, y+deltaY);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() { return String.format("%2d;%2d", getX(), getY()); }
}
