package IS24_LB11.cli.style;

import IS24_LB11.cli.utils.Side;
import IS24_LB11.game.utils.Direction;
import com.googlecode.lanterna.TextColor;

public class DoubleBorderStyle implements BorderStyle {
    private static final char HORIZONTAL = '═';
    private static final char VERTICAL = '║';
    private static final char[] CORNERS = new char[]{'╔', '╗', '╚', '╝'};
    private static final char[] SEPARATORS = new char[]{'╦', '╩', '╠', '╣'};

    private TextColor color;

    public DoubleBorderStyle(TextColor color) {
        this.color = color;
    }

    public DoubleBorderStyle() {
        this(TextColor.ANSI.DEFAULT);
    }

    @Override
    public char getHLine() { return HORIZONTAL; }
    @Override
    public char getVLine() { return VERTICAL; }
    @Override
    public char getCorner(int dir) { return CORNERS[dir]; }
    @Override
    public char getCorner(Direction dir) { return CORNERS[dir.ordinal()]; }
    @Override
    public char getSeparator(int dir) { return SEPARATORS[dir]; }
    @Override
    public char getSeparator(Side side) { return SEPARATORS[side.ordinal()]; }
    @Override
    public TextColor getColor() {
        return color;
    }
}
