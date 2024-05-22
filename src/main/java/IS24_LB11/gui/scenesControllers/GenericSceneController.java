package IS24_LB11.gui.scenesControllers;

import IS24_LB11.gui.Chat;
import IS24_LB11.gui.PopUps;
import IS24_LB11.gui.phases.ClientGUIState;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Generic controller class for scenes in the application.
 */
public class GenericSceneController {
    protected Chat chat = new Chat();

    @FXML
    protected ListView<Text> chatPane;

    @FXML
    protected TextArea messageBox;

    @FXML
    protected BorderPane chatBox;

    @FXML
    protected Button buttonSend;

    protected ClientGUIState genericState;
    protected PopUps popUps = new PopUps();
    protected Stage stage;

    /**
     * Shows a pop-up notification with the given message.
     *
     * @param message the message to display in the pop-up
     */
    public void showPopUpNotification(String message) {
        popUps.popUpMaker(message);
    }

    /**
     * Handles the exit action, displaying a confirmation dialog.
     *
     * @param stage the stage to close if the exit is confirmed
     */
    public void exit(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add("PopUpStyle.css");

        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit!");
        alert.setContentText("Are you sure?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            genericState.shutdown();
            System.out.println("You successfully logged out!");
            stage.close();
        }
    }

    /**
     * Shows a pop-up to restart the game.
     */
    public void showPopUpRestartGame() {
        popUps.restartGame(genericState, this);
    }

    /**
     * Restarts the game by closing the current stage and starting the client GUI.
     *
     * @throws Exception if an error occurs during the restart
     */
    public void restart() throws Exception {
        stage.close();
        genericState.getClientGUI().start(stage);
    }

    /**
     * Shows an exit notification with the given message.
     *
     * @param message the message to display in the notification
     */
    public void showExitNotification(String message) {
        popUps.lastPlayerLeft(stage, genericState, this);
    }

    /**
     * Handles sending messages from the chat box.
     * Processes different commands based on the input text.
     */
    public void send() {
        String[] strings = messageBox.getText().split(" +", 3);
        switch (strings[0]) {
            case "help":
                showHelp();
                break;
            case "sendto":
                sendPrivateMessage(strings);
                break;
            case "sendtoall":
                sendPublicMessage(strings);
                break;
            case "clear":
                clearChat();
                break;
            default:
                chatPane.getItems().add(new Text("Invalid input"));
                break;
        }
        messageBox.setText("");
    }

    /**
     * Hides the chat box by minimizing its size.
     */
    public void chatHide() {
        chatBox.setPrefWidth(chatBox.getMinWidth());
        chatBox.setPrefHeight(chatBox.getMinHeight());
        chatBox.setLayoutX(20);
        chatBox.setLayoutY(600);
    }

    /**
     * Displays the chat box by expanding its size.
     */
    public void chatDisplay() {
        chatBox.setPrefWidth(chatBox.getMaxWidth());
        chatBox.setPrefHeight(chatBox.getMaxHeight());
        chatBox.setLayoutX(20);
        chatBox.setLayoutY(300);
    }

    /**
     * Adds a message to the chat pane and chat history.
     *
     * @param msg the message to add
     */
    public void addMessage(String msg) {
        Text text = new Text(msg);
        chatPane.getItems().add(text);
        chat.addMessage(text);
    }

    /**
     * Updates the chat pane with a list of messages.
     *
     * @param messages the list of messages to add to the chat pane
     */
    public void updateChat(ArrayList<Text> messages) {
        for (Text text : messages) {
            chatPane.getItems().add(text);
            chat.addMessage(text);
        }
    }

    /**
     * Clears all messages from the chat pane.
     */
    public void clearChat() {
        chatPane.getItems().clear();
    }

    /**
     * Shows commands in the chat pane.
     */
    private void showHelp() {
        chatPane.getItems().add(new Text("--------------------------------------------------"));
        chatPane.getItems().add(new Text("Private chat command:"));
        chatPane.getItems().add(new Text("sendto username msg"));
        chatPane.getItems().add(new Text("--------------------------------------------------"));
        chatPane.getItems().add(new Text("Public chat command:"));
        chatPane.getItems().add(new Text("sendtoall msg"));
        chatPane.getItems().add(new Text("--------------------------------------------------"));
        chatPane.getItems().add(new Text("Clear messages chat command:"));
        chatPane.getItems().add(new Text("clear"));
        chatPane.getItems().add(new Text("--------------------------------------------------"));
    }

    /**
     * Sends a private message to a specified user.
     *
     * @param strings the array containing the command, username, and message
     */
    private void sendPrivateMessage(String[] strings) {
        if (strings.length < 3) {
            chatPane.getItems().add(new Text("Invalid input for private message"));
            return;
        }
        genericState.sendMessage(strings[1], genericState.getUsername(), strings[2]);
        Text sendTo = new Text("<me> " + strings[2]);
        chatPane.getItems().add(sendTo);
        chat.addMessage(sendTo);
    }

    /**
     * Send message to all users.
     *
     * @param strings the array containing the command and message
     */
    private void sendPublicMessage(String[] strings) {
        String msg = strings.length == 2 ? strings[1] : strings[1] + " " + strings[2];
        Text sendToAll = new Text("<me> " + msg);
        genericState.sendToAll(genericState.getUsername(), msg);
        chatPane.getItems().add(sendToAll);
        chat.addMessage(sendToAll);
    }
}
