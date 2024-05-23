package IS24_LB11.cli;

import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.event.CommandEvent;
import IS24_LB11.cli.view.CommandLineView;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * The CommandLine class represents a command line interface for user input.
 * It manages the input line, cursor position, and rendering the command line view.
 */
public class CommandLine {
    private static final int BASE_OFFSET = CommandLineView.COMMAND_INTRO.length();

    private final StringBuilder line;
    private final CommandLineView view;
    private boolean enabled;
    private int disabledChars;
    private int cursor;
    private int offset;
    private int width;

    /**
     * Constructs a CommandLine object with a given view.
     *
     * @param view the CommandLineView to be associated with this CommandLine
     */
    public CommandLine(CommandLineView view) {
        this.line = new StringBuilder();
        this.view = view;
        this.enabled = true;
        this.width = view.innerWidth() - BASE_OFFSET;
        this.disabledChars = 0;
        this.cursor = 0;
        this.offset = 0;
    }

    /**
     * Updates the command line view.
     */
    public void update() {
        view.loadCommandLine(this);
        view.drawAll();
    }

    /**
     * Inserts a character at the current cursor position.
     *
     * @param c the character to insert
     */
    public void insertChar(char c) {
        if (line.length() == cursor) {
            line.append(c);
        } else {
            line.insert(cursor, c);
        }
        moveCursor(1);
    }

    /**
     * Deletes a character at the current cursor position.
     */
    public void deleteChar() {
        if (cursor == 0) return;
        line.deleteCharAt(cursor - 1);
        if (offset > 0) offset--;
        moveCursor(-1);
    }

    /**
     * Sets the entire command line to a given string.
     *
     * @param string the string to set the command line to
     */
    public void setLine(String string) {
        line.replace(0, line.length(), string);
        cursor = line.length();
        offset = Integer.max(0, cursor - width + 1);
    }

    /**
     * Clears the command line.
     */
    public void clearLine() {
        line.delete(0, line.length());
        cursor = 0;
        offset = 0;
    }

    /**
     * Moves the cursor by a given delta.
     *
     * @param delta the amount to move the cursor
     */
    public void moveCursor(int delta) {
        int newCursor = cursor + delta;
        cursor = Integer.min(newCursor, line.length());
        cursor = Integer.max(cursor, 0);
        if (offset > cursor) offset = cursor;
        if (delta > 0 && cursor - offset >= width) {
            offset += delta;
        }
    }

    /**
     * Resizes the command line view based on the terminal size.
     *
     * @param screenSize the new terminal size
     */
    public void resize(TerminalSize screenSize) {
        view.resize(screenSize);
        width = view.innerWidth() - BASE_OFFSET;
        if (cursor >= width) cursor = width - 1;
        view.loadCommandLine(this);
        view.drawAll();
    }

    /**
     * Consumes a key stroke event and updates the command line state accordingly.
     *
     * @param state the client state
     * @param keyStroke the key stroke event
     */
    public void consumeKeyStroke(ClientState state, KeyStroke keyStroke) {
        if (keyStroke.isCtrlDown() || keyStroke.isAltDown()) {
            if (!togglePopup(state, keyStroke)) return;
        } else if (keyStroke.getKeyType() == KeyType.F1) {
            toggle();
        } else if (enabled) {
            switch (keyStroke.getKeyType()) {
                case Character -> insertChar(keyStroke.getCharacter());
                case Backspace -> deleteChar();
                case Enter -> {
                    if (!line.isEmpty()) state.tryQueueEvent(new CommandEvent(getFullLine()));
                    clearLine();
                }
                case ArrowUp, ArrowDown -> { }
                case ArrowLeft -> moveCursor(-1);
                case ArrowRight -> moveCursor(1);
                case Escape -> state.quit();
                default -> { return; }
            }
        } else {
            if (!togglePopup(state, keyStroke))
                return;
        }
        view.loadCommandLine(this);
        view.drawCommandLine();
        state.consumeKey();
    }

    /**
     * Toggles the visibility of a popup based on the key stroke.
     *
     * @param state the client state
     * @param keyStroke the key stroke event
     * @return true if a popup was toggled, false otherwise
     */
    private boolean togglePopup(ClientState state, KeyStroke keyStroke) {
        if (keyStroke.getKeyType() != KeyType.Character) return false;
        switch (keyStroke.getCharacter()) {
            case ' ' -> toggle();
            case '?' -> state.togglePopup("help");
            case 'h', 'H' -> state.togglePopup("hand");
            case 'd', 'D' -> state.togglePopup("decks");
            case 't', 'T' -> state.togglePopup("table");
            case 's', 'S' -> state.togglePopup("symbols");
            case 'c', 'C' -> state.togglePopup("chat");
            case 'w', 'W' -> state.hideAllPopups();
            default -> { return false; }
        }
        return true;
    }

    /**
     * Toggles the command line's enabled state and clears the line.
     */
    private void toggle() {
        enabled = !enabled;
        clearLine();
    }

    /**
     * Disables the command line and clears the line.
     */
    public void disable() {
        enabled = false;
        clearLine();
        view.loadCommandLine(this);
        view.drawCommandLine();
    }

    /**
     * Returns the full content of the command line.
     *
     * @return the full content of the command line
     */
    public String getFullLine() {
        return line.toString();
    }

    /**
     * Returns the visible part of the command line.
     *
     * @return the visible part of the command line
     */
    public String getVisibleLine() {
        return line.substring(offset, Integer.min(line.length(), offset + width));
    }

    /**
     * Returns the relative cursor position.
     *
     * @return the relative cursor position
     */
    public int getRelativeCursor() {
        return cursor - offset;
    }

    /**
     * Returns the current cursor position.
     *
     * @return the current cursor position
     */
    public int getCursor() {
        return cursor;
    }

    /**
     * Returns the current offset.
     *
     * @return the current offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Returns the current width of the command line.
     *
     * @return the current width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns whether the command line is enabled.
     *
     * @return true if the command line is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }
}
