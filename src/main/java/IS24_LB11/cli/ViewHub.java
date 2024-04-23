package IS24_LB11.cli;

import IS24_LB11.cli.popup.Popup;
import IS24_LB11.cli.view.CommandLineView;
import IS24_LB11.cli.view.NotificationView;
import IS24_LB11.game.Player;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.utils.Position;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.PriorityQueue;

public class ViewHub implements Runnable {
    private final Terminal terminal;
    private Stage stage;
    private CommandLineView commandLineView;
    private final ArrayList<Popup> popups;
    private Optional<NotificationView> notification;

    public ViewHub() throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        terminal = new DefaultTerminalFactory(System.out, System.in, charset).createTerminal();
        terminal.enterPrivateMode();
        stage = new Stage(this, terminal.getTerminalSize());
        commandLineView = new CommandLineView(terminal.getTerminalSize());
        popups = new ArrayList<>(4);
        notification = Optional.empty();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("thread-view-hub");
        stage.build();
        while (true) {
            synchronized (terminal) {
                try {
                    terminal.wait(50);
                    stage.print(terminal);
                    notification.ifPresent(p -> {
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
        synchronized (terminal) {
            try { terminal.clearScreen(); }
            catch (IOException ignored) { }
            commandLineView.resize(size);
            commandLineView.buildCommandLine(commandLine);
            commandLineView.build();
            notification.ifPresent(popUp -> {
                popUp.resize(size);
                popUp.build();
                popUp.setPosition(0, stage.getYAndHeight()-3);
            });
            stage.resize(size);
            stage.rebuild();
            for (Popup popup : popups) {
                popup.resize();
                popup.drawViewInStage();
            }
        }
    }

    public void update() {
        synchronized (terminal) {
            terminal.notify();
        }
    }

    public void updateCommandLine(CommandLine commandLine) {
        synchronized (terminal) {
            commandLineView.buildCommandLine(commandLine);
            commandLineView.build();
            terminal.notify(); }
    }

    public void addNotification(String message, String title) {
        if (notification.isPresent()) {
            stage.buildArea(notification.get().getRectangle());
            notification = Optional.empty();
        }
        notification = Optional.of(new NotificationView(stage, title, message));
        notification.get().build();
    }

    public void addNotification(String message) {
        addNotification(message, "");
    }

    public void removeNotification() {
        if (notification.isPresent()) {
            stage.buildArea(notification.get().getRectangle());
            notification = Optional.empty();
            update();
        }
    }

    public void addPopup(Popup popup) {
        popups.add(popup);
    }

    public void clearPopups() {
        popups.clear();
    }

    public GameStage setGameStage(Player player) {
        try {
            GameStage gameStage = new GameStage(this, terminal.getTerminalSize(), player);
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
            SetupStage setupStage = new SetupStage(this, terminal.getTerminalSize(), setup);
            stage = setupStage;
            stage.build();
            return setupStage;
        } catch (IOException e) {
            System.err.println("caught exception: "+e.getMessage());
            return null;
        }
    }

    public LobbyStage setLobbyStage() {
        try {
            LobbyStage lobbyStage = new LobbyStage(this, terminal.getTerminalSize());
            stage = lobbyStage;
            stage.build();
            return lobbyStage;
        } catch (IOException e) {
            System.err.println("caught exception: "+e.getMessage());
            return null;
        }
    }

    public Terminal getTerminal() { return terminal; }

    public Stage getStage() {
        return stage;
    }
}
