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

            //da implementare nel controller perchè altrimenti non riconosce il bottone
            //      /\
            //      ||
            // randomButton.setDisable(true);
        }
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
        ButtonType buttonTypeTwo = new ButtonType("OK");

        //import the button inside the alert
        alert.getButtonTypes().setAll(buttonTypeTwo);

        //insert the received message into the alert field
        alert.setContentText(message);
        alert.show();

    }

}
