package IS24_LB11.gui;

import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.scenesControllers.GenericSceneController;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.ArrayList;

public class PopUps {

    public int maxPlayersAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Max Number Of Players");
        alert.setHeaderText("Please select the max number of players: ");

        //create the wanted buttons
        ButtonType buttonTypeTwo = new ButtonType("2");
        ButtonType buttonTypeThree = new ButtonType("3");
        ButtonType buttonTypeFour = new ButtonType("4");


        //put the buttons into the alert
        alert.getButtonTypes().setAll(buttonTypeTwo, buttonTypeThree, buttonTypeFour);

        //needed to show the alert and wait the button pressing
        ButtonType result = alert.showAndWait().orElse(null);
        int selectedNumber = 0;
        if (result != null) {
            //this will take the input pressed and parse that into int
            selectedNumber = Integer.parseInt(result.getText());

            //send to server the selected number
            System.out.println("You selected: " + selectedNumber + " players.");
        }
        return selectedNumber;
    }

    //da implementare nel controller perchè altrimenti non riconosce l'azione
    //          /\
    //          ||
//    public void makePopUp(ActionEvent event) {
//        popUpMaker(message);
//    }
    public void popUpMaker(String message) {
        //create a new type of alert without symbol
        Alert alert = new Alert(Alert.AlertType.NONE);

        //create the button with "ok" text inside
        ButtonType buttonType = new ButtonType("OK");

        //import the button inside the alert
        alert.getButtonTypes().setAll(buttonType);

        //insert the received message into the alert field
        alert.setContentText(message);
        alert.show();
    }


    public void lastPlayerLeft(Stage stage, ClientGUIState state, GenericSceneController genericSceneController) {

        Alert alert = new Alert(Alert.AlertType.NONE);

        ButtonType quit = new ButtonType("QUIT");
        ButtonType restart = new ButtonType("RESTART");

        alert.getButtonTypes().setAll(quit, restart);

        alert.setContentText("You are the only player left! Press OK to quit!");

        ButtonType result = alert.showAndWait().orElse(null);

        if(result.equals(quit)){
            stage.close();
        }
        else if(result.equals(restart)) {
            try {
                genericSceneController.restart();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        state.shutdown();

    }

    public void restartGame(ClientGUIState state, GenericSceneController genericSceneController){
        Alert alert = new Alert(Alert.AlertType.NONE);

        ButtonType buttonType = new ButtonType("OK");

        alert.getButtonTypes().setAll(buttonType);

        alert.setContentText("Server CRASHED, press OK to restart Client");

        ButtonType result = alert.showAndWait().orElse(null);

        if(result != null){
            try {
                genericSceneController.restart();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
