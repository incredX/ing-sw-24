package IS24_LB11.gui.scenesControllers;

import IS24_LB11.gui.PopUps;
import IS24_LB11.gui.phases.ClientGUIState;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class GenericSceneController {
    ClientGUIState genericState;
    PopUps popUps = new PopUps();

    public Stage stage;
    public void showPopUpNotification(String message){
        popUps.popUpMaker(message);
    }
    public void exit(Stage stage)  {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit!");
        alert.setContentText("Are you sure?");

        if(alert.showAndWait().get() == ButtonType.OK){
            genericState.shutdown();
            System.out.println("You successfully logged out!");
            stage.close();
        }
    }
    public void showPopUpRestartGame(){
        popUps.restartGame(genericState,this);
    }
    public void restart() throws Exception {
        stage.close();
        genericState.getClientGUI().start(stage);
    }
    public void showExitNotification(String s) {
        popUps.lastPlayerLeft(stage,genericState, this);
    }
}
