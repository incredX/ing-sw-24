package IS24_LB11.gui;

import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.phases.LoginGUIState;
import IS24_LB11.gui.scenesControllers.LoginSceneController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The ClientGUI class initializes and starts the client-side graphical user interface (GUI) for the application.
 * It extends the JavaFX Application class and sets up the initial login state.
 */
public class ClientGUI extends Application {

    private ClientGUIState state;

    /**
     * The main entry point for all JavaFX applications. This method is called after the system is ready for the application to begin running.
     *
     * @param primaryStage the primary stage for this application, onto which the application scene can be set.
     * @throws Exception if something goes wrong during initialization.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.state = new LoginGUIState(this);

        LoginSceneController loginSceneController = new LoginSceneController(this.state);

        loginSceneController.showStage();
    }

    /**
     * The main method which serves as the entry point for the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
