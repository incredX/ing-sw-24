package IS24_LB11.cli;

import IS24_LB11.cli.utils.Side;
import com.googlecode.lanterna.input.KeyStroke;

public class BoardKeyConsumer implements KeyConsumer {
    private final int priority;
    private final GameStage boardView;

    public BoardKeyConsumer(GameStage gameStage, int priority) {
        this.boardView = gameStage;
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
