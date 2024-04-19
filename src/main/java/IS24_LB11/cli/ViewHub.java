package IS24_LB11.cli;

import IS24_LB11.cli.view.PopUpView;
import IS24_LB11.game.Board;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import IS24_LB11.cli.utils.TerminalRectangle;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;

public class ViewHub implements Runnable {
    private final Terminal terminal;
    private Stage stage;
    private Optional<PopUpView> popUp;

    public ViewHub() throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        terminal = new DefaultTerminalFactory(System.out, System.in, charset).createTerminal();
        terminal.enterPrivateMode();
        stage = new Stage(terminal.getTerminalSize());
        popUp = Optional.empty();
    }

    @Override
    public void run() {
        stage.build();
        while (true) {
            synchronized (terminal) {
                try {
                    terminal.wait(50);
                    stage.print(terminal);
                    popUp.ifPresent(p -> {
                        try { p.print(terminal); }
                        catch (IOException ignored) {}
                    });
                    terminal.flush();
                }
                catch (InterruptedException e) { break; }
                catch (IOException ignored) { }
            }
        }
        try {
            terminal.exitPrivateMode();
        } catch (IOException ignored) { }
    }

    public void resize(TerminalSize size, CommandLine commandLine) {
        stage.resize(size);
        stage.build();
        stage.buildCommandLine(commandLine);
        popUp.ifPresent(popUp -> {
            int dx = popUp.getWidth()/2, dy = popUp.getHeight()/2;
            popUp.setTerminalPosition(stage.getCenter().withRelative(-dx, -dy));
        });
        synchronized (terminal) {
            try { terminal.clearScreen(); }
            catch (IOException ignored) {}
            terminal.notify();
        }
    }

    public void update() {
        synchronized (terminal) {
            terminal.notify();
        }
    }

    public void update(Consumer<Stage> consumer) {
        synchronized (terminal) {
            consumer.accept(stage);
            terminal.notify();
        }
    }

    public void updateStage(TerminalRectangle rectangle) {
        update(s -> s.buildRelativeArea(rectangle));
    }

    public void updateCommandLine(CommandLine commandLine) {
        update(s -> s.buildCommandLine(commandLine));
    }

    public void addPopUp(String message, String title) {
        TerminalSize size = new TerminalSize(stage.getWidth()/2, stage.getHeight()/2);
        TerminalPosition position = stage.getCenter().withRelative(-size.getColumns()/2, -size.getRows()/2);
        popUp = Optional.of(new PopUpView(size, position, message, title));
        popUp.get().build();
    }

    public void removePopUp() {
        if (popUp.isPresent()) {
            stage.buildArea(popUp.get().getRectangle());
            popUp = Optional.empty();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        update();
    }

    public void setBoardStage(Board board) {
        try {
            stage = new BoardView(terminal.getTerminalSize(), board);
            stage.build();
            update();
        } catch (IOException ignored) { }
    }

    public Terminal getTerminal() { return terminal; }

    public Stage getStage() {
        return stage;
    }
}
