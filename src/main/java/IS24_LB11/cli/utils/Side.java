package IS24_LB11.cli.utils;

public enum Side {
    NORD,
    SUD,
    WEST,
    EAST;

    public static Side fromInt(int side) {
        side %= 4;
        if (side < 0) side = 4 + side;
        if (side == Side.NORD.ordinal()) return Side.NORD;
        if (side == Side.SUD.ordinal()) return Side.SUD;
        if (side == Side.WEST.ordinal()) return Side.WEST;
        return Side.EAST;
    }
}
