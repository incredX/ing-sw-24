package IS24_LB11.cli;

import IS24_LB11.cli.popup.PopupView;
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

public class ViewHub implements Runnable {
    private final Object lock = new Object();
    private final Terminal terminal;
    private final CommandLineView commandLineView;
    private final ArrayList<PopupView> popups;
    private Stage stage;
    private TerminalSize screenSize;
    private Optional<NotificationView> notification;
    private int nextPopupId;

    public ViewHub() throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        terminal = new DefaultTerminalFactory(System.out, System.in, charset).createTerminal();
        terminal.enterPrivateMode();
        screenSize = terminal.getTerminalSize();
        stage = new Stage(this, screenSize);
        commandLineView = new CommandLineView(screenSize);
        popups = new ArrayList<>(4);
        notification = Optional.empty();
        nextPopupId = 0;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("thread-view-hub");
        stage.build();
        while (true) {
            synchronized (lock) {
                try {
                    lock.wait(50);
                    stage.print(terminal);
                    for (PopupView popup : popups) popup.print(terminal);
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
        screenSize = size;
        synchronized (lock) {
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
//            for (PopupView popupView : popups) {
//                popupView.resize(size);
//                //popupView.build();
//                stage.setCover(popupView, true);
//            }
        }
    }

    public void update() {
        synchronized (lock) {
            lock.notify();
        }
    }

    public void updateCommandLine(CommandLine commandLine) {
        synchronized (lock) {
            commandLineView.buildCommandLine(commandLine);
            commandLineView.build();
            lock.notify(); }
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

    public void addPopup(PopupView popup) {
        synchronized (lock) {
            popups.add(popup);
            stage.setCover(popups.getLast(), true);
        }
        popups.getLast().setId(nextPopupId);
        System.out.printf("new popup (%d) in %s, %s\n", nextPopupId, popup.getPosition(), popup.getSize());
        nextPopupId++;
    }

    public void removePopup(int id) {
        synchronized (lock) {
            for(int i=0; i<popups.size(); i++) {
                if (popups.get(i).getId() != id) continue;
                stage.setCover(popups.get(i), false);
                stage.buildArea(popups.remove(i).getRectangle());
                System.out.printf("popup %d removed\n", nextPopupId);
                return;
            }
        }
    }

    public void clearPopups() {
        synchronized (terminal) {
            popups.clear();
        }
        nextPopupId = 0;
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

    public TerminalSize getScreenSize() { return screenSize; }

    public Stage getStage() {
        return stage;
    }
}
