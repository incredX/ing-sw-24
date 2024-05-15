package IS24_LB11.cli.popup;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.GameState;
import IS24_LB11.cli.controller.PlayerStateInterface;
import IS24_LB11.cli.view.popup.SymbolsView;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.function.Consumer;

public class SymbolsPopup extends Popup {
    private final GameState gameState;

    public SymbolsPopup(ViewHub viewhub, GameState gameState) {
        super(viewhub, new SymbolsView(viewhub.getScreenSize()));
        this.gameState = gameState;
        this.readOnly = true;
    }

    @Override
    public String label() {
        return "symbols";
    }

    @Override
    public void update() {
        castView(symbolsView -> {
            symbolsView.loadSymbols(gameState.getSymbolsCounter());
            symbolsView.drawAll();
        });
    }

    @Override
    public void consumeKeyStroke(KeyStroke keyStroke) {}

    @Override
    public void setPlayerState(PlayerStateInterface playerState) {}

    private void castView(Consumer<SymbolsView> consumer) {
        consumer.accept((SymbolsView) popView);
    }
}
