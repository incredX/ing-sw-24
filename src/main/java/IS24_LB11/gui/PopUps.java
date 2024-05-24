package IS24_LB11.gui;

import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.scenesControllers.GenericSceneController;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * This class handles various pop-up dialogs used in the GUI.
 */
public class PopUps {

    /**
     * Displays a confirmation dialog for selecting the maximum number of players.
     *
     * @return the number of players selected by the user
     */
    public int maxPlayersAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add("PopUpStyle.css");
        alert.setTitle("Max Number Of Players");
        alert.setHeaderText("Please select the max number of players: ");

        // Create the desired buttons
        ButtonType buttonTypeTwo = new ButtonType("2");
        ButtonType buttonTypeThree = new ButtonType("3");
        ButtonType buttonTypeFour = new ButtonType("4");

        // Add the buttons to the alert
        alert.getButtonTypes().setAll(buttonTypeTwo, buttonTypeThree, buttonTypeFour);

        // Show the alert and wait for a button press
        ButtonType result = alert.showAndWait().orElse(null);
        int selectedNumber = 0;
        if (result != null) {
            // Parse the selected number from the button text
            selectedNumber = Integer.parseInt(result.getText());

            // Send the selected number to the server
            System.out.println("You selected: " + selectedNumber + " players.");
        }
        return selectedNumber;
    }

    /**
     * Displays a pop-up with a specified message.
     *
     * @param message the message to display in the pop-up
     */
    public void popUpMaker(String message) {
        // Create a new alert without a symbol
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.getDialogPane().getStylesheets().add("PopUpStyle.css");

        // Create the OK button
        ButtonType buttonType = new ButtonType("OK");

        // Add the button to the alert
        alert.getButtonTypes().setAll(buttonType);

        // Set the message in the alert
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Displays a dialog when the player is the last one left in the game, offering
     * the options to quit or restart.
     *
     * @param stage the current stage
     * @param state the current client GUI state
     * @param genericSceneController the current scene controller
     */
    public void lastPlayerLeft(Stage stage, ClientGUIState state, GenericSceneController genericSceneController) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.getDialogPane().getStylesheets().add("PopUpStyle.css");

        // Create the quit and restart buttons
        ButtonType quit = new ButtonType("QUIT");
        ButtonType restart = new ButtonType("RESTART");

        // Add the buttons to the alert
        alert.getButtonTypes().setAll(quit, restart);

        // Set the content text in the alert
        alert.setContentText("You are the only player left!");

        // Show the alert and wait for a button press
        ButtonType result = alert.showAndWait().orElse(null);

        // Handle the button press
        if (result.equals(quit)) {
            stage.close();
        } else if (result.equals(restart)) {
            try {
                genericSceneController.restart();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        state.shutdown();
    }

    /**
     * Displays a dialog indicating the server has crashed and create pop up to restart the client.
     *
     * @param state the current client GUI state
     * @param genericSceneController the current scene controller
     */
    public void restartGame(ClientGUIState state, GenericSceneController genericSceneController) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.getDialogPane().getStylesheets().add("PopUpStyle.css");

        // Create the OK button
        ButtonType buttonType = new ButtonType("OK");

        // Add the button to the alert
        alert.getButtonTypes().setAll(buttonType);

        // Set the content text in the alert
        alert.setContentText("Server CRASHED, press OK to restart Client");

        // Show the alert and wait for a button press
        ButtonType result = alert.showAndWait().orElse(null);

        // Handle the button press
        if (result != null) {
            try {
                genericSceneController.restart();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
