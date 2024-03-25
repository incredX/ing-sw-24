package IS24_LB11.cli.style;

import IS24_LB11.cli.utils.Side;

public class SingleBorderStyle implements BorderStyle {
    private static final char HORIZONTAL = '─';
    private static final char VERTICAL = '│';
    private static final char[] CORNERS = new char[]{'┌', '┐', '└', '┘'};
    private static final char[] SEPARATORS = new char[]{'┬', '┴', '├', '┤'};

    public char getHLine() { return HORIZONTAL; }
    public char getVLine() { return VERTICAL; }
    public char getCorner(int dir) { return CORNERS[dir]; }
    public char getSeparator(int dir) { return SEPARATORS[dir]; }
    public char getSeparator(Side side) { return SEPARATORS[side.ordinal()]; }
}
