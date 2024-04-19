package IS24_LB11.cli.utils;

import IS24_LB11.game.utils.Position;

public enum Side {
    NORD {
        @Override
        public Position asRelativePosition() { return new Position(0, -1); }
        @Override
        public Side opposite() { return SUD; }
    },
    SUD {
        @Override
        public Position asRelativePosition() { return new Position(0, 1); }
        @Override
        public Side opposite() { return NORD; }
    },
    WEST {
        @Override
        public Position asRelativePosition() { return new Position(-1, 0); }
        @Override
        public Side opposite() { return EAST; }
    },
    EAST {
        @Override
        public Position asRelativePosition() { return new Position(1, 0); }
        @Override
        public Side opposite() { return WEST; }
    };

    public static Side fromInt(int side) {
        side %= 4;
        if (side < 0) side = 4 + side;
        if (side == Side.NORD.ordinal()) return Side.NORD;
        if (side == Side.SUD.ordinal()) return Side.SUD;
        if (side == Side.WEST.ordinal()) return Side.WEST;
        return Side.EAST;
    }

    public Side opposite() {
        return this.opposite();
    }

    public Position asRelativePosition() {
        return this.asRelativePosition();
    }

    public boolean isVertical() {
        return (this.ordinal()&2) == 0;
    }
}
