package IS24_LB11.gui;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class PopUps {

    public void maxPlayersAlert(ActionEvent event) {
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

        if (result != null) {
            //this will take the input pressed and parse that into int
            int selectedNumber = Integer.parseInt(result.getText());

            //send to server the selected number
            System.out.println("You selected: " + selectedNumber + " players.");
        }
    }

}
