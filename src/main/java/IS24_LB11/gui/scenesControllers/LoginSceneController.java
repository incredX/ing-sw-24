package IS24_LB11.gui.scenesControllers;


import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.phases.LoginGUIState;
import IS24_LB11.gui.phases.SetupGUIState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class LoginSceneController extends GenericSceneController{

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

    LoginGUIState state;

    public LoginSceneController(ClientGUIState state) {
        this.stage=new Stage();

        this.state = (LoginGUIState) state;
        this.genericState=state;
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
        chatBox.setOnMouseEntered(mouseEvent -> chatDisplay());
        chatBox.setOnMouseExited(mouseEvent -> chatHide());
        chatHide();
        // set default values
        ipTextField.setText("localhost");
        portTextField.setText("54321");
    }

    public void changeToSetupState(PlayerSetup playerSetup,
                                   ArrayList<GoalCard> publicGoals,
                                   ArrayList<PlayableCard> normalDeck,
                                   ArrayList<PlayableCard> goldenDeck,
                                   ArrayList<String> playerNames){
        SetupSceneController setupSceneController = new SetupSceneController(new SetupGUIState(state),stage);
        setupSceneController.updateChat(this.chat.getMessages());
        setupSceneController.state.initialize(playerSetup,publicGoals,normalDeck,goldenDeck,playerNames);
        setupSceneController.showStage();
    }

    public void showStage() {
        this.stage.setResizable(false);
        this.stage.show();
    }



    public void login(){
        String username = usernameTextField.getText();
        if (username.isEmpty()){
            popUps.popUpMaker("Insert username please");
            return;
        }
        if (username.contains(" ")){
            popUps.popUpMaker("No spaces allowed");
            return;
        }
        if (username.length()>11){
            popUps.popUpMaker("Username too long");
            return;
        }
        String serverIP = ipTextField.getText();
        int port = Integer.valueOf(portTextField.getText());
        state.initialize(username,serverIP,port);
        state.execute();
        state.getServerHandler().setLoginSceneController(this);
    }

    public void setPlayers(){
        state.setMaxPlayers(popUps.maxPlayersAlert());
    }
    public void resetServerHandler(){
        state.resetServerHandler();
    }


    public void disableLogin(){
        loginButton.setDisable(true);
    }

}