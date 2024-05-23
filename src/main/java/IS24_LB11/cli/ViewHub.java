package IS24_LB11.cli;

import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.controller.GameState;
import IS24_LB11.cli.controller.LobbyState;
import IS24_LB11.cli.controller.SetupState;
import IS24_LB11.cli.view.popup.PopupView;
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

/**
 * The ViewHub class manages the various stages and views in the CLI application.
 * It handles rendering to the screen, managing notifications, popups, and the main command line interface.
 */
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

    /**
     * Constructs a ViewHub object, initializes the screen, and sets up the initial view.
     *
     * @throws IOException if there is an error initializing the screen
     */
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

    /**
     * The main run loop of the ViewHub, which continuously updates the screen.
     */
    @Override
    public void run() {
        Thread.currentThread().setName("view-hub");
        while (true) {
            synchronized (lock) {
                try {
                    stage.print(screen);
                    for (PopupView popup : popups) {
                        popup.print(screen);
                    }
                    if (notificationView != null) {
                        notificationView.print(screen);
                    }
                    commandLineView.print(screen);
                    screen.refresh();
                    lock.wait();
                } catch (InterruptedException e) {
                    break;
                } catch (IOException e) {
                    Debugger.print(e);
                }
            }
        }
        try {
            screen.close();
        } catch (IOException e) {
            Debugger.print(e);
        }
        Debugger.print("thread terminated.");
    }

    /**
     * Resizes the terminal and updates all views accordingly.
     *
     * @param size the new terminal size
     */
    public void resize(TerminalSize size) {
        synchronized (lock) {
            screenSize = size;
            stage.resize();
            if (notificationView != null) {
                notificationView.resize(size);
                notificationView.drawAll();
            }
        }
    }

    /**
     * Checks if the terminal screen size has changed.
     *
     * @return the new terminal size if it has changed, otherwise null
     */
    public TerminalSize screenSizeChanged() {
        synchronized (lock) {
            return screen.doResizeIfNecessary();
        }
    }

    /**
     * Notifies the main loop to update the screen.
     */
    public void update() {
        synchronized (lock) {
            lock.notify();
        }
    }

    /**
     * Adds a notification to the screen.
     *
     * @param message the notification message
     * @param title   the title of the notification
     */
    public void addNotification(String message, String title) {
        synchronized (lock) {
            notificationView = new NotificationView(screenSize, title, message);
            notificationView.drawAll();
        }
    }

    /**
     * Removes the current notification from the screen.
     */
    public void removeNotification() {
        if (notificationView != null) {
            stage.buildArea(notificationView.getRectangle());
            update();
            notificationView = null;
        }
    }

    /**
     * Adds a popup view to the screen.
     *
     * @param popup the popup view to add
     */
    public void addPopup(PopupView popup) {
        synchronized (lock) {
            popups.add(popup);
        }
        popups.getLast().setId(nextPopupId);
        nextPopupId++;
    }

    /**
     * Removes a popup view from the screen by its ID.
     *
     * @param id the ID of the popup to remove
     */
    public void removePopup(int id) {
        synchronized (lock) {
            for (int i = 0; i < popups.size(); i++) {
                if (popups.get(i).getId() != id) continue;
                stage.buildArea(popups.remove(i).getRectangle());
                break;
            }
            if (popups.isEmpty()) nextPopupId = 0;
        }
    }

    /**
     * Sets the game stage and updates the current state.
     *
     * @param gameState the new game state
     * @return the created GameStage
     */
    public GameStage setGameStage(GameState gameState) {
        GameStage gameStage = new GameStage(gameState, this);
        gameStage.loadCardViews();
        state = gameState;
        stage = gameStage;
        return gameStage;
    }

    /**
     * Sets the setup stage and updates the current state.
     *
     * @param setupState the new setup state
     * @return the created SetupStage
     */
    public SetupStage setSetupStage(SetupState setupState) {
        SetupStage setupStage = new SetupStage(this, setupState);
        setupStage.loadStarterCard();
        state = setupState;
        stage = setupStage;
        return setupStage;
    }

    /**
     * Sets the lobby stage and updates the current state.
     *
     * @param lobbyState the new lobby state
     * @return the created LobbyStage
     */
    public LobbyStage setLobbyStage(LobbyState lobbyState) {
        LobbyStage lobbyStage = new LobbyStage(this);
        state = lobbyState;
        stage = lobbyStage;
        return lobbyStage;
    }

    /**
     * Gets the screen object.
     *
     * @return the screen
     */
    public Screen getScreen() {
        return screen;
    }

    /**
     * Gets the current stage.
     *
     * @return the current stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Gets the command line view.
     *
     * @return the command line view
     */
    public CommandLineView getCommandLineView() {
        return commandLineView;
    }

    /**
     * Gets the current screen size.
     *
     * @return the current screen size
     */
    public TerminalSize getScreenSize() {
        return screenSize;
    }
}
