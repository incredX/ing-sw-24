package IS24_LB11.cli;

import IS24_LB11.cli.utils.Side;
import com.googlecode.lanterna.input.KeyStroke;

public class BoardKeyConsumer implements KeyConsumer {
    private final int priority;
    private final BoardStage boardView;

    public BoardKeyConsumer(BoardStage boardStage, int priority) {
        this.boardView = boardStage;
        this.priority = priority;
    }

    public boolean consumeKeyStroke(KeyStroke keyStroke) {
        if (!keyStroke.isCtrlDown()) return false;
        switch (keyStroke.getKeyType()) {
            case ArrowUp -> boardView.shift(Side.NORD);
            case ArrowDown -> boardView.shift(Side.SUD);
            case ArrowLeft -> boardView.shift(Side.WEST);
            case ArrowRight -> boardView.shift(Side.EAST);
            default -> {
                return false;
            }
        }
        return true;
    }

    public int priority() {
        return priority;
    }
}
