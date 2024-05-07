package IS24_LB11.gui.scenesControllers;


import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.phases.LoginGUIState;
import IS24_LB11.gui.phases.PlayerStateInterface;
import javafx.fxml.FXML;


import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

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

    Stage stage = new Stage();
    LoginGUIState state;

    public LoginSceneController(ClientGUIState state) {
        this.state = (LoginGUIState) state;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginPage.fxml"));
        loader.setController(this);

        this.stage.setTitle("Codex");
        try {
            this.stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        stage.setOnCloseRequest(event -> {
            event.consume();
            exit(stage);
        });

    }

    @FXML
    private void initialize(){
        loginButton.setOnAction(event -> login());
        exitButton.setOnAction(event -> exit(stage));
    }

    public void showStage() {
        this.stage.show();
    }

    public void exit(Stage stage)  {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit!");
        alert.setContentText("Are you sure?");

        if(alert.showAndWait().get() == ButtonType.OK){
            System.out.println("You successfully logged out!");
            stage.close();
        }
        state.getServerHandler().shutdown();
    }

    public void login(){
        String username = usernameTextField.getText();
        String serverIP = ipTextField.getText();
        int port = Integer.valueOf(portTextField.getText());
        state.initialize(username,serverIP,port);
        state.execute();
    }
}