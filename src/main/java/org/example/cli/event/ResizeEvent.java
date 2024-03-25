package org.example.cli.event;

import com.googlecode.lanterna.TerminalSize;

public class ResizeEvent implements Event {
    private final TerminalSize size;

    public ResizeEvent(TerminalSize size) {
        this.size = size;
    }

    public TerminalSize getSize() { return size; }
}
