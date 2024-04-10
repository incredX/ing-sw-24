package IS24_LB11.game.utils;

import IS24_LB11.game.Result;

import java.util.function.Consumer;
import java.util.function.Function;

public enum Direction {
    UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT;

    private static final Direction[] ALL_DIRECTIONS =
            new Direction[] {UP_LEFT,UP_RIGHT,DOWN_LEFT,DOWN_RIGHT};

    private static final Function<String, String> ERR_PARSE =
            s -> String.format("impossible to parse %s as Corner", s);

    public static Direction parse(int dir) {
        return ALL_DIRECTIONS[dir%4];
    }

    public static Result<Direction> tryParse(int dir) {
        if (dir >= 0 && dir < 4) return Result.Ok(ALL_DIRECTIONS[dir]);
        return Result.Error(ERR_PARSE.apply("int"), "integer out of bounds (0..4)");
    }

    public static Result<Direction> tryParse(String dir) {
        try { return Result.Ok(Direction.valueOf(dir)); }
        catch (IllegalArgumentException e) {
            return Result.Error(ERR_PARSE.apply("String"), e.getMessage());
        }
    }

    public static void forEachDirection(Consumer<Direction> consumer) {
        for (Direction direction : ALL_DIRECTIONS) consumer.accept(direction);
    }

    public Direction opposite() {
        return switch (this) {
            case UP_LEFT -> DOWN_RIGHT;
            case UP_RIGHT -> DOWN_LEFT;
            case DOWN_LEFT -> UP_RIGHT;
            case DOWN_RIGHT -> UP_LEFT;
        };
    }

    public Position relativePosition() {
        int x = 2*(this.ordinal()&1)-1, y = (this.ordinal()&2)-1;
        return new Position(x, y);
    }

    public boolean isUp() {
        return (this.ordinal()&2) == 0;
    }

    public boolean isDown() {
        return (this.ordinal()&2) == 1;
    }

    public boolean isLeft() {
        return (this.ordinal()&1) == 0;
    }

    public boolean isRight() {
        return (this.ordinal()&1) == 1;
    }
}
