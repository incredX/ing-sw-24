package IS24_LB11.gui;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;


import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginSceneController {

    @FXML
    private AnchorPane scenePane;

    @FXML
    private ImageView codexLoginImageView;

    @FXML
    public Button loginButton;

    @FXML
    private Button exitButton;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField ipTextField;

    @FXML
    private TextField portTextField;

    Stage stage;

    public void exit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You're about to logout!");
        alert.setContentText("Are you sure?");

        if(alert.showAndWait().get() == ButtonType.OK){
            stage = (Stage) scenePane.getScene().getWindow();
            System.out.println("You successfully logged out!");
            stage.close();
        }
    }

    public void login(ActionEvent event){
        String username = usernameTextField.getText();
        String serverIP = ipTextField.getText();
        String port = portTextField.getText();
        System.out.println(username + serverIP + port);
    }

    //createNotificationPopUp
}