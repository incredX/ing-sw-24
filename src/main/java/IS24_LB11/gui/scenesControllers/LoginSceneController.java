package IS24_LB11.gui.scenesControllers;


import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.gui.PopUps;
import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.phases.LoginGUIState;
import IS24_LB11.gui.phases.SetupGUIState;
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
import java.util.ArrayList;

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

        // set default values
        ipTextField.setText("localhost");
        portTextField.setText("54321");
    }

    public void changeToSetupState(PlayerSetup playerSetup,
                                   ArrayList<GoalCard> publicGoals,
                                   ArrayList<PlayableCard> normalDeck,
                                   ArrayList<PlayableCard> goldenDeck,
                                   ArrayList<String> playerNames){
        SetupSceneController setupSceneController = new SetupSceneController(new SetupGUIState(state), stage);
        setupSceneController.state.initialize(playerSetup,publicGoals,normalDeck,goldenDeck,playerNames);
        //stage.close();
        setupSceneController.showStage();
    }

    public void showStage() {
        this.stage.setResizable(false);
        this.stage.show();
    }

    public void exit(Stage stage)  {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit!");
        alert.setContentText("Are you sure?");

        if(alert.showAndWait().get() == ButtonType.OK){
            state.shutdown();
            System.out.println("You successfully logged out!");
            stage.close();
        }
    }

    public void login(){
        String username = usernameTextField.getText();
        if (username.isEmpty()){
            PopUps popUps = new PopUps();
            popUps.popUpMaker("Insert username please");
            return;
        }
        if (username.contains(" ")){
            PopUps popUps = new PopUps();
            popUps.popUpMaker("No spaces allowed");
            return;
        }
        String serverIP = ipTextField.getText();
        int port = Integer.valueOf(portTextField.getText());
        state.initialize(username,serverIP,port);
        state.execute();
        state.getServerHandler().setLoginSceneController(this);
    }

    public void setPlayers(){
        PopUps popUps = new PopUps();
        state.setMaxPlayers(popUps.maxPlayersAlert());
    }

    public void showPopUpNotification(String message){
        PopUps popUps = new PopUps();
        popUps.popUpMaker(message);
    }

    public void disableLogin(){
        loginButton.setDisable(true);
    }
}