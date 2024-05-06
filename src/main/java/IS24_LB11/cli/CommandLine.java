package IS24_LB11.cli;

import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.event.CommandEvent;
import IS24_LB11.cli.view.CommandLineView;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class CommandLine {
    private static final int BASE_OFFSET = CommandLineView.COMMAND_INTRO.length();

    private final StringBuilder line;
    private final CommandLineView view;
    private boolean enabled;
    private int disabledChars;
    private int cursor;
    private int offset;
    private int width;
    //private long lastKeyEventTime;
    //private KeyType lastKeyEventType;

    public CommandLine(CommandLineView view) {
        this.line = new StringBuilder();
        this.view = view;
        this.enabled = true;
        this.width = view.innerWidth() - BASE_OFFSET;
        this.disabledChars = 0;
        this.cursor = 0;
        this.offset = 0;
        //this.lastKeyEventTime = 0;
        //this.lastKeyEventType = null;
    }

    public void update() {
        view.loadCommandLine(this);
        view.drawAll();
    }

    public void insertChar(char c) {
        if (line.length() == cursor) line.append(c);
        else line.insert(cursor, c);
        moveCursor(1);
    }

    public void deleteChar() {
        if (cursor == 0) return;
        line.deleteCharAt(cursor-1);
        if (offset > 0) offset--;
        moveCursor(-1);
    }

    public void setLine(String string) {
        line.replace(0, line.length(), string);
        cursor = line.length();
        offset = Integer.max(0,cursor - width +1);
    }

    public void clearLine() {
        line.delete(0, line.length());
        cursor = 0;
        offset = 0;
    }

    public void moveCursor(int delta) {
        int newCursor = cursor+delta;
        cursor = Integer.min(newCursor, line.length());
        cursor = Integer.max(cursor, 0);
        if (offset > cursor) offset = cursor;
        if (delta > 0 && cursor - offset >= width) {
            offset += delta;
        }
    }

    public void resize(TerminalSize screenSize) {
        view.resize(screenSize);
        width = view.innerWidth()-BASE_OFFSET;
        if (cursor >= width) cursor = width-1;
        view.loadCommandLine(this);
        view.drawAll();
    }

    public void consumeKeyStroke(ClientState state, KeyStroke keyStroke) {
        if (keyStroke.isCtrlDown() || keyStroke.isAltDown()) {
            if (!toggle(state, keyStroke)) return;
        } else if (keyStroke.getKeyType() == KeyType.F1) {
            toggle();
        } else if (enabled)
            switch (keyStroke.getKeyType()) {
            case Character -> insertChar(keyStroke.getCharacter());
            case Backspace -> deleteChar();
            case Enter -> {
                if(!line.isEmpty()) state.tryQueueEvent(new CommandEvent(getFullLine()));
                clearLine();
            }
            case ArrowLeft -> moveCursor(-1);
            case ArrowRight -> moveCursor(1);
            case Escape -> state.quit();
            default -> { return; }
        } else {
            if (!toggle(state, keyStroke))
                return;
        }
        view.loadCommandLine(this);
        view.drawCommandLine();
        state.keyConsumed();
    }

    private boolean toggle(ClientState state, KeyStroke keyStroke) {
        if (keyStroke.getKeyType() != KeyType.Character) return false;
        switch (keyStroke.getCharacter()) {
            case ' ' -> toggle();
            case 'h','H' -> state.togglePopup("hand");
            case 'd','D' -> state.togglePopup("decks");
            case 't','T' -> state.togglePopup("table");
            case 'w','W' -> state.hideAllPopups();
            default -> { return false; }
        }
        return true;
    }
    private void toggle() {
        enabled = !enabled;
        clearLine();
    }

//    private boolean isStrokeOnTime(KeyStroke keyStroke, long intervall) {
//        if (lastKeyEventType != null && lastKeyEventType != keyStroke.getKeyType()) {
//            return false;
//        }
//        return keyStroke.getEventTime() - lastKeyEventTime < intervall;
//    }

    public String getFullLine() { return line.toString(); }
    public String getVisibleLine() { return line.substring(offset, Integer.min(line.length(), offset+width)); }
    public int getRelativeCursor() { return cursor-offset; }
    public int getCursor() { return cursor; }
    public int getOffset() { return offset; }
    public int getWidth() { return width; }
    public boolean isEnabled() { return enabled; }
}
