package IS24_LB11.cli.event;

import com.googlecode.lanterna.TerminalSize;

public record ResizeEvent(TerminalSize size) implements Event { }
