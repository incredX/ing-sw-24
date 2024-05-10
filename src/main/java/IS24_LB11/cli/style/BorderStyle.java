package IS24_LB11.cli.style;

import IS24_LB11.cli.utils.Side;
import IS24_LB11.game.utils.Direction;
import com.googlecode.lanterna.TextColor;

public interface BorderStyle {
    char getHLine();
    char getVLine();
    char getCorner(int dir);
    char getCorner(Direction dir);
    char getSeparator(int dir);
    char getSeparator(Side side);
    TextColor getColor();
}
