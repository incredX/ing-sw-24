package org.example.cli.utils;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public interface CliFrame {
    void clear();
    void build();
    void rebuild();
    void print(Terminal terminal) throws IOException;
    void setPosition(TerminalPosition newPosition);
    TerminalPosition getPosition();
}
