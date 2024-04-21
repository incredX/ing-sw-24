package IS24_LB11.cli.view;

import IS24_LB11.cli.CommandLine;
import IS24_LB11.cli.LobbyStage;
import IS24_LB11.cli.Stage;
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
    private CommandLineView commandLineView;
    private Optional<PopUpView> popUp;

    public ViewHub() throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        terminal = new DefaultTerminalFactory(System.out, System.in, charset).createTerminal();
        terminal.enterPrivateMode();
        stage = new Stage(terminal.getTerminalSize());
        commandLineView = new CommandLineView(terminal.getTerminalSize());
        popUp = Optional.empty();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("thread-view-hub");
        stage.build();
        while (true) {
            synchronized (terminal) {
                try {
                    terminal.wait(20);
                    stage.print(terminal);
                    commandLineView.print(terminal);
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
        System.out.println(Thread.currentThread().getName() + " offline");
    }

    public void resize(TerminalSize size, CommandLine commandLine) {
        stage.resize(size);
        stage.build();
        commandLineView.resize(size);
        commandLineView.buildCommandLine(commandLine);
        commandLineView.build();
        popUp.ifPresent(popUp -> {
            popUp.resize(size);
            popUp.build();
            popUp.setTerminalPosition(popUpBasePosition()
                    .withRelative(-popUp.getWidth()/2, -popUp.getHeight()/2));
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
        commandLineView.buildCommandLine(commandLine);
        commandLineView.build();
    }

    public void addPopUp(String message, String title) {
        if (popUp.isPresent()) {
            stage.buildArea(popUp.get().getRectangle());
            popUp = Optional.empty();
        }
        popUp = Optional.of(new PopUpView(popUpBasePosition(), message, title));
        popUp.get().build();
    }

    public void addPopUp(String message) {
        addPopUp(message, "");
    }

    public void removePopUp() {
        if (popUp.isPresent()) {
            stage.buildArea(popUp.get().getRectangle());
            popUp = Optional.empty();
            update();
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

    public void setLobbyStage() {
        try {
            stage = new LobbyStage(terminal.getTerminalSize());
            stage.build();
            update();
        } catch (IOException ignored) { }
    }

    public Terminal getTerminal() { return terminal; }

    public Stage getStage() {
        return stage;
    }

    private TerminalPosition popUpBasePosition() {
        //int x = stage.getCenter().getColumn();
        //int y = stage.getHeight()-1;
        //return new TerminalPosition(x, y);
        return stage.getCenter();
    }
}
