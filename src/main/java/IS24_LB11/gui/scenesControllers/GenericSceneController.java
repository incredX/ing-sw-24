package IS24_LB11.gui.scenesControllers;

import IS24_LB11.gui.Chat;
import IS24_LB11.gui.PopUps;
import IS24_LB11.gui.phases.ClientGUIState;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GenericSceneController {
    Chat chat = new Chat();
    @FXML
    protected ListView chatPane;
    @FXML protected TextArea messageBox;
    @FXML
    protected BorderPane chatBox;
    @FXML
    protected Button buttonSend;
    ClientGUIState genericState;
    PopUps popUps = new PopUps();

    public Stage stage;
    public void showPopUpNotification(String message){
        popUps.popUpMaker(message);
    }
    public void exit(Stage stage)  {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add("PopUpStyle.css");

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
    public void send(){
        String[] strings = messageBox.getText().split(" ",3);
        switch (strings[0]){
            case "help":
                Text help = new Text("Private chat command:");
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
                break;

            case "sendto":
                genericState.sendMessage(strings[1], genericState.getUsername(), strings[2]);
                Text sendTo = new Text("<me> " + strings[2]);
                chatPane.getItems().add(sendTo);
                chat.addMessage(sendTo);
                break;
            case "sendtoall":
                String msg = new String();
                if (strings.length==2)
                    msg=strings[1];
                else
                    msg=strings[1] + " " + strings[2];
                Text sendToAll = new Text("<me> "+msg);
                genericState.sendToAll(genericState.getUsername(), msg);
                chatPane.getItems().add(sendToAll);
                chat.addMessage(sendToAll);
                break;
            case "clear":
                clearChat();
                break;
            default:
                chatPane.getItems().add("Invalid input");
                break;
        }
        messageBox.setText("");
    }

    public void chatHide() {
        chatBox.setPrefWidth(chatBox.getMinWidth());
        chatBox.setPrefHeight(chatBox.getMinHeight());
        chatBox.setLayoutX(20);
        chatBox.setLayoutY(600);
    }
    public void chatDisplay() {
        chatBox.setPrefWidth(chatBox.getMaxWidth());
        chatBox.setPrefHeight(chatBox.getMaxHeight());
        chatBox.setLayoutX(20);
        chatBox.setLayoutY(300);
    }

    public void addMessage(String msg){
        Text text = new Text(msg);
        chatPane.getItems().add(text);
        chat.addMessage(text);
    }
    public void updateChat(ArrayList<Text> messages) {
        for(Text text:messages) {
            chatPane.getItems().add(text);
            chat.addMessage(text);
        }
    }
    public void clearChat(){
        chatPane.getItems().clear();
    }
}
