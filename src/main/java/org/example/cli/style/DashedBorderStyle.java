package org.example.cli.style;

import org.example.cli.utils.Side;

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
