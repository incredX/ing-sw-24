package org.example.cli.style;

import org.example.cli.utils.Side;

public interface BorderStyle {
    char getHLine();
    char getVLine();
    char getCorner(int dir);
    char getSeparator(int dir);
    char getSeparator(Side side);
}
