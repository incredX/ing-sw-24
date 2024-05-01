package IS24_LB11.cli;


import IS24_LB11.cli.view.CommandLineView;
import com.googlecode.lanterna.TerminalSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandLineTest {
    private final int CMD_LINE_WIDTH = 10;
    private CommandLine cmdLine;

    @BeforeEach
    void init() {
        cmdLine = new CommandLine(new CommandLineView(new TerminalSize(CMD_LINE_WIDTH+6, 3)));
    }

    @Test
    void testSetLine() {
        //"Lorem ipsum."  with [...]= visible line's section, '|'= cursor :
        // ----[ ipsum.|]
        //      ^-- offset = 1 + line.length() - line.width()
        String input = "Lorem ipsum.";
        int offset = Integer.max(0, 1 + input.length() - cmdLine.getWidth());
        cmdLine.setLine(input);
        assert (cmdLine.getFullLine().equals(input));
        assert (cmdLine.getCursor() == input.length());
        assert (cmdLine.getOffset() == offset);
    }

    @Test
    void testClearLine() {
        String input = "Lorem ipsum.";
        cmdLine.setLine(input);
        cmdLine.clearLine();
        assert (cmdLine.getFullLine().isEmpty());
        assert (cmdLine.getCursor() == 0);
        assert (cmdLine.getOffset() == 0);
    }

    @Test
    @DisplayName("test insertion of chars at the end of the line")
    void testInsertChar1() {
        int cursor = 0;
        String input = "Lorem ipsum.";

        for (char c: input.toCharArray()) {
            cursor++;
            cmdLine.insertChar(c);
            assert (cmdLine.getFullLine().equals(input.substring(0, cursor)));
        }
    }

    @Test
    @DisplayName("test the insertion of chars inside the line")
    void testInsertChar2() {
        String[] input = new String[]{"Lorem ", "(*) ", "ipsum."};
        int cursor = 0;

        cmdLine.setLine(input[0]+input[2]);     // line = "Lorem ipsum"
        cmdLine.moveCursor(-input[2].length()); //        cursor ^

        for (char c: input[1].toCharArray()) {
            cursor++;
            cmdLine.insertChar(c);
            String subRes = input[0] + input[1].substring(0,cursor) + input[2];
            assert (cmdLine.getFullLine().equals(subRes));
        }
    }

    @Test
    @DisplayName("test delete of the last char")
    void testDeleteChar1() {
        String input = "Lorem ipsum.";
        cmdLine.setLine(input);

        for (int cursor=input.length()-1; cursor>=0; cursor--) {
            cmdLine.deleteChar();
            int offset = Integer.max(0, 1 + cursor - cmdLine.getWidth());
            String substr = input.substring(0,cursor);
            assert (cmdLine.getFullLine().equals(substr));
            assert (cmdLine.getCursor() == cursor);
            assert (cmdLine.getOffset() == offset);
        }
        cmdLine.deleteChar();
        assert (cmdLine.getFullLine().isEmpty());
        assert (cmdLine.getCursor() == 0);
        assert (cmdLine.getOffset() == 0);
    }

    @Test
    void testMoveCursor() {
        String input = "Lorem ipsum";
        int delta = 2;

        cmdLine.setLine(input);
        cmdLine.moveCursor(delta);  // try to move over the end of the line

        for (int i=input.length(); i>=0; i-=delta) {
            assert (i == cmdLine.getCursor());
            cmdLine.moveCursor(-delta);
        }
        cmdLine.moveCursor(-delta);  // try to move over the start of the line
        assert (0 == cmdLine.getCursor());
    }

    @Test
    @DisplayName("test commandline's control over its offset")
    void testGetVisibleLine() {
        String input = "Lorem ipsum.";
        int offset = Integer.max(0, 1 + input.length() - cmdLine.getWidth());
        int width = Integer.min(input.length(), CMD_LINE_WIDTH);

        cmdLine.setLine(input);
        assertEquals (offset, cmdLine.getOffset());
        assertEquals (input.substring(offset, Integer.min(input.length(), offset+width)), cmdLine.getVisibleLine());

        cmdLine.moveCursor(-cmdLine.getWidth()+1);
        assertEquals (offset, cmdLine.getOffset());
        assertEquals (input.substring(offset, Integer.min(input.length(), offset+width)), cmdLine.getVisibleLine());

        for (int i=0; i<4; i++) {
            if (offset > 0) offset--;
            cmdLine.moveCursor(-1);
            assertEquals (offset, cmdLine.getOffset());
            assertEquals (input.substring(offset, Integer.min(input.length(), offset+width)), cmdLine.getVisibleLine());
        }
    }
}
