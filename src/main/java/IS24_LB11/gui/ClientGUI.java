package IS24_LB11.gui;

import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.phases.LoginGUIState;
import IS24_LB11.gui.phases.PlayerStateInterface;
import IS24_LB11.gui.scenesControllers.LoginSceneController;
import javafx.application.Application;
import javafx.stage.Stage;



public class ClientGUI extends Application {

    ClientGUIState state;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.state = new LoginGUIState();

        LoginSceneController loginSceneController = new LoginSceneController(this.state);
        loginSceneController.showStage();

    }

    public static void main(String[] args) {
        launch(args);
    }

}