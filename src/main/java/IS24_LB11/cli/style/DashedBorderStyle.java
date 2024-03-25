package IS24_LB11.cli.style;

import IS24_LB11.cli.utils.Side;

public class DashedBorderStyle implements BorderStyle {
    private static final char HORIZONTAL = '-';
    private static final char VERTICAL = '¦';
    private static final char CORNER = '+';
    private static final char SEPARATOR = '+';;

    public char getHLine() { return HORIZONTAL; }
    public char getVLine() { return VERTICAL; }
    public char getCorner(int dir) { return CORNER; }
    public char getSeparator(int dir) { return SEPARATOR; }
    public char getSeparator(Side side) { return SEPARATOR; }
}
