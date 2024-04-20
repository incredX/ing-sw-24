package IS24_LB11.cli.utils;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public interface CliFrame {
    void clear();
    void build();
    void rebuild();
    void print(Terminal terminal) throws IOException;
    void setTerminalPosition(TerminalPosition newPosition);
    TerminalPosition getTerminalPosition();
}
