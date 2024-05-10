package IS24_LB11.cli.popup;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.controller.PlayerStateInterface;
import IS24_LB11.cli.view.popup.HelpView;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.function.Consumer;

public class HelpPoup extends Popup {
    private static final String FILE_NAME = "resources/commands.json";

    private final JsonArray commandList;
    private ClientState state;
    private int firstLine;
    private int numLines;

    public HelpPoup(ViewHub viewhub, ClientState state) {
        super(viewhub, new HelpView(viewhub.getScreenSize()));
        JsonArray commandList;
        try {
            commandList = new JsonParser().parse(new FileReader(FILE_NAME)).getAsJsonArray();
        } catch (FileNotFoundException e) {
            System.err.println("Failed to load command list");
            commandList = new JsonArray();
        }
        this.state = state;
        this.commandList = commandList;
        this.overlap = true;
        castView(helpView -> {
            helpView.loadCommands(this.commandList);
            numLines = helpView.getNumLines();
        });
        this.firstLine = 0;
        //this.numLines = ((HelpView)this.popView).getNumLines();
    }

    @Override
    public String label() { return "help"; }

    @Override
    public void update() {
        castView(helpView -> {
            helpView.setOffset(firstLine);
            helpView.redraw();
        });
    }

    @Override
    public void show() {
        enable();
        super.show();
    }

    @Override
    public void hide() {
        disable(); // an invisible popup is also disabled (invisible => disabled)
        super.hide();
    }

    @Override
    public void consumeKeyStroke(KeyStroke keyStroke) {
        if (!enabled) return;
        switch (keyStroke.getKeyType()) {
            case ArrowUp -> firstLine -= firstLine > 0 ? 1 : 0;
            case ArrowDown -> firstLine += firstLine < numLines - HelpView.DEFAULT_HEIGHT ? 1 : 0;
            case ArrowLeft, ArrowRight -> {}
            default -> { return; }
        }
        update();
        state.consumeKey();
    }

    public void setPlayerState(PlayerStateInterface playerState) {
        //this.playerState = playerState;
    }

    private void castView(Consumer<HelpView> consumer) {
        consumer.accept((HelpView)popView);
    }
}
