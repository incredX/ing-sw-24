package IS24_LB11.cli;

import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.controller.GameState;
import IS24_LB11.cli.controller.LobbyState;
import IS24_LB11.cli.controller.SetupState;
import IS24_LB11.cli.view.PopupView;
import IS24_LB11.cli.view.CommandLineView;
import IS24_LB11.cli.view.NotificationView;
import IS24_LB11.cli.view.stage.GameStage;
import IS24_LB11.cli.view.stage.LobbyStage;
import IS24_LB11.cli.view.stage.SetupStage;
import IS24_LB11.cli.view.stage.Stage;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.ArrayList;

public class ViewHub implements Runnable {
    private final Object lock = new Object();
    private final Screen screen;
    private final CommandLineView commandLineView;
    private final ArrayList<PopupView> popups;
    private NotificationView notificationView;
    private TerminalSize screenSize;
    private ClientState state;
    private Stage stage;
    private int nextPopupId;

    public ViewHub() throws IOException {
        screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        screenSize = screen.getTerminalSize();
        commandLineView = new CommandLineView(screenSize);
        stage = new Stage(this);
        popups = new ArrayList<>(4);
        notificationView = null;
        nextPopupId = 0;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("view-hub");
        while (true) {
            synchronized (lock) {
                try {
                    Debugger.print("print");
                    stage.print(screen);
                    for (PopupView popup : popups) popup.print(screen);
                    if (notificationView != null) notificationView.print(screen);
                    commandLineView.print(screen);
                    screen.refresh(Screen.RefreshType.COMPLETE);
                    lock.wait();
                }
                catch (InterruptedException e) { break; }
                catch (IOException e) { Debugger.print(e); }
            }
        }
        try {
            screen.close();
        } catch (IOException e) { Debugger.print(e); }
        Debugger.print("thread terminated.");
    }

    public void resize(TerminalSize size, CommandLine commandLine) {
        screenSize = size;
        synchronized (lock) {
            commandLineView.resize(size);
            commandLineView.buildCommandLine(commandLine);
            commandLineView.drawAll();
            stage.resize();
            for (PopupView popup : popups) stage.buildArea(popup.getRectangle());
            if (notificationView != null) {
                notificationView.resize(size);
                notificationView.drawAll();
            }
            //lock.notify();
        }
        Debugger.print("resize");
    }

    public TerminalSize screenSizeChanged() {
        synchronized (lock) {
            return screen.doResizeIfNecessary();
        }
    }

    public void update() {
        synchronized (lock) {
            lock.notify();
        }
    }

    public void updateStage() {
        synchronized (lock) {
            stage.drawAll();
            lock.notify();
        }
    }

    public void updateCommandLine(CommandLine commandLine) {
        synchronized (lock) {
            commandLineView.buildCommandLine(commandLine);
            commandLineView.drawAll();
            lock.notify();
            //Debugger.print("update cmdline");
        }
    }

    public void addNotification(String message, String title) {
        synchronized (lock) {
            notificationView = new NotificationView(screenSize, title, message);
            notificationView.drawAll();
            //lock.notify();
        }
    }

    public void addNotification(String message) {
        addNotification(message, "");
    }

    public void removeNotification() {
        if (notificationView != null) {
            stage.buildArea(notificationView.getRectangle());
            notificationView = null;
        }
    }

    public void addPopup(PopupView popup) {
        synchronized (lock) {
            popups.add(popup);
        }
        popups.getLast().setId(nextPopupId);
        nextPopupId++;
    }

    public void removePopup(int id) {
        synchronized (lock) {
            for(int i=0; i<popups.size(); i++) {
                if (popups.get(i).getId() != id) continue;
                stage.buildArea(popups.remove(i).getRectangle());
                break;
            }
            if (popups.isEmpty()) nextPopupId = 0;
        }
    }

    public GameStage setGameStage(GameState gameState) {
        GameStage gameStage = new GameStage(gameState, this);
        gameStage.loadCardViews();
        state = gameState;
        stage = gameStage;
        return gameStage;
    }

    public SetupStage setSetupStage(SetupState setupState) {
        SetupStage setupStage = new SetupStage(this, setupState);
        setupStage.loadStarterCard();
        state = setupState;
        stage = setupStage;
        return setupStage;
    }

    public LobbyStage setLobbyStage(LobbyState lobbyState) {
        LobbyStage lobbyStage = new LobbyStage(this);
        state = lobbyState;
        stage = lobbyStage;
        return lobbyStage;
    }

    public Screen getScreen() {
        return screen;
    }

    public TerminalSize getScreenSize() { return screenSize; }

    public Stage getStage() {
        return stage;
    }
}
