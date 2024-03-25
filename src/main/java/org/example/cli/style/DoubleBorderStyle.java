package org.example.cli.style;

import org.example.cli.utils.Side;

public class DoubleBorderStyle implements BorderStyle {
    private static final char HORIZONTAL = '═';
    private static final char VERTICAL = '║';
    private static final char[] CORNERS = new char[]{'╔', '╗', '╚', '╝'};
    private static final char[] SEPARATORS = new char[]{'╦', '╩', '╠', '╣'};

    public char getHLine() { return HORIZONTAL; }
    public char getVLine() { return VERTICAL; }
    public char getCorner(int dir) { return CORNERS[dir]; }
    public char getSeparator(int dir) { return SEPARATORS[dir]; }
    public char getSeparator(Side side) { return SEPARATORS[side.ordinal()]; }
}
