package IS24_LB11.cli;

import IS24_LB11.cli.controller.ClientInGame;
import IS24_LB11.cli.controller.ClientInSetup;
import IS24_LB11.cli.view.PopupView;
import IS24_LB11.cli.view.CommandLineView;
import IS24_LB11.cli.view.NotificationView;
import IS24_LB11.cli.view.stage.GameStage;
import IS24_LB11.cli.view.stage.LobbyStage;
import IS24_LB11.cli.view.stage.SetupStage;
import IS24_LB11.cli.view.stage.Stage;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ViewHub implements Runnable {
    private final Object lock = new Object();
    private final Terminal terminal;
    private final CommandLineView commandLineView;
    private final ArrayList<PopupView> popups;
    private NotificationView notificationView;
    private Stage stage;
    private TerminalSize screenSize;
    private int nextPopupId;

    public ViewHub() throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        terminal = new DefaultTerminalFactory(System.out, System.in, charset).createTerminal();
        terminal.enterPrivateMode();
        screenSize = terminal.getTerminalSize();
        commandLineView = new CommandLineView(screenSize);
        stage = new Stage(this);
        popups = new ArrayList<>(4);
        notificationView = null;
        nextPopupId = 0;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("thread-view-hub");
        stage.build();
        while (true) {
            synchronized (lock) {
                try {
                    lock.wait(10);
                    stage.print(terminal);
                    for (PopupView popup : popups) popup.print(terminal);
                    if (notificationView != null) notificationView.print(terminal);
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
            if (notificationView != null) {
                notificationView.resize(size);
                notificationView.build();
            }
        }
    }

    public void update() {
        synchronized (lock) {
            lock.notify();
        }
    }

    public void updateStage() {
        synchronized (lock) {
            stage.build();
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
        notificationView = new NotificationView(screenSize, title, message);
        notificationView.build();
        stage.setCover(notificationView, true);
        update();
    }

    public void addNotification(String message) {
        addNotification(message, "");
    }

    public void removeNotification() {
        if (notificationView != null) {
            stage.setCover(notificationView, false);
            stage.buildArea(notificationView.getRectangle());
            notificationView = null;
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
                break;
            }
            if (popups.isEmpty()) nextPopupId = 0;
        }
    }

    public GameStage setGameStage(ClientInGame state) {
        GameStage gameStage = new GameStage(state, this);
        gameStage.loadCardViews();
        stage = gameStage;
        update();
        return gameStage;
    }

    public SetupStage setSetupStage(ClientInSetup state) {
        SetupStage setupStage = new SetupStage(this, state);
        setupStage.loadStarterCard();
        stage = setupStage;
        return setupStage;
    }

    public LobbyStage setLobbyStage() {
        LobbyStage lobbyStage = new LobbyStage(this);
        stage = lobbyStage;
        return lobbyStage;
    }

    public Terminal getTerminal() { return terminal; }

    public TerminalSize getScreenSize() { return screenSize; }

    public Stage getStage() {
        return stage;
    }
}
