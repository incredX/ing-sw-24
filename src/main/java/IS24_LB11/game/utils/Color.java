package IS24_LB11.game.utils;

import IS24_LB11.game.Result;

public enum Color {
    RED, GREEN, BLUE, YELLOW;

    public static Result<Color> tryFromInt(int index) {
        return switch (index) {
            case 0 -> Result.Ok(RED);
            case 1 -> Result.Ok(GREEN);
            case 2 -> Result.Ok(BLUE);
            case 3 -> Result.Ok(YELLOW);
            default -> Result.Error("invalid integer to convert in Color");
        };
    }
    public static Color fromInt(int index) {
        return switch (index%4) {
            case 0 -> RED;
            case 1 -> GREEN;
            case 2 -> BLUE;
            case 3 -> YELLOW;
            default -> null;
        };
    }

}

