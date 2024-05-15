package IS24_LB11.cli.style;

import IS24_LB11.cli.utils.Side;
import IS24_LB11.game.utils.Direction;
import com.googlecode.lanterna.TextColor;

public class DashedBorderStyle implements BorderStyle {
    private static final char HORIZONTAL = '-';
    private static final char VERTICAL = '¦';
    private static final char CORNER = '+';
    private static final char SEPARATOR = '+';

    private TextColor color;

    public DashedBorderStyle(TextColor color) {
        this.color = color;
    }

    public DashedBorderStyle() {
        this(TextColor.ANSI.DEFAULT);
    }

    @Override
    public char getHLine() { return HORIZONTAL; }
    @Override
    public char getVLine() { return VERTICAL; }
    @Override
    public char getCorner(int dir) { return CORNER; }
    @Override
    public char getCorner(Direction dir) { return CORNER; }
    @Override
    public char getSeparator(int dir) { return SEPARATOR; }
    @Override
    public char getSeparator(Side side) { return SEPARATOR; }
    @Override
    public TextColor getColor() { return color; }
}
