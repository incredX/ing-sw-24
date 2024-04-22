package IS24_LB11.cli;

import IS24_LB11.cli.view.CommandLineView;
import IS24_LB11.cli.view.PopUpView;
import IS24_LB11.game.Board;
import IS24_LB11.game.Player;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.utils.Position;
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
                    popUp.ifPresent(p -> {
                        try { p.print(terminal); }
                        catch (IOException e) {
                            System.err.println("caught exception: "+e.getMessage());
                        }
                    });
                    commandLineView.print(terminal);
                    terminal.flush();
                }
                catch (InterruptedException e) { break; }
                catch (IOException e) {
                    System.err.println("caught exception: "+e.getMessage());
                }
            }
        }
        try {
            terminal.exitPrivateMode();
        } catch (IOException e) {
            System.err.println("caught exception: "+e.getMessage());
        }
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
            popUp.setPosition(0, stage.getYAndHeight()-3);
        });
        synchronized (terminal) {
            try { terminal.clearScreen(); }
            catch (IOException e) {
                System.err.println("caught exception: "+e.getMessage());
            }
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
        popUp = Optional.of(new PopUpView(stage, title, message));
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

    public GameStage setGameStage(Player player) {
        try {
            GameStage gameStage = new GameStage(terminal.getTerminalSize(), player);
            gameStage.loadCardViews();
            gameStage.setPointer(new Position(0,0));
            stage = gameStage;
            stage.build();
            update();
            return gameStage;
        } catch (IOException e) {
            System.err.println("caught exception: "+e.getMessage());
            return null;
        }
    }

    public SetupStage setSetupStage(PlayerSetup setup) {
        try {
            SetupStage setupStage = new SetupStage(terminal.getTerminalSize(), setup);
            stage = setupStage;
            stage.build();
            update();
            return setupStage;
        } catch (IOException e) {
            System.err.println("caught exception: "+e.getMessage());
            return null;
        }
    }

    public void setLobbyStage() {
        try {
            stage = new LobbyStage(terminal.getTerminalSize());
            stage.build();
            update();
        } catch (IOException e) {
            System.err.println("caught exception: "+e.getMessage());
        }
    }

    public Terminal getTerminal() { return terminal; }

    public Stage getStage() {
        return stage;
    }
}
