package IS24_LB11.cli.style;

import IS24_LB11.cli.utils.Side;

public interface BorderStyle {
    char getHLine();
    char getVLine();
    char getCorner(int dir);
    char getSeparator(int dir);
    char getSeparator(Side side);
}
