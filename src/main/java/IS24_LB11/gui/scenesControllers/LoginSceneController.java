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

/**
 * Controller class for the login scene.
 */
public class LoginSceneController extends GenericSceneController {

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

    private LoginGUIState state;

    /**
     * Constructs a LoginSceneController and initializes the stage and scene.
     *
     * @param state the current client GUI state
     */
    public LoginSceneController(ClientGUIState state) {
        this.stage = new Stage();
        this.state = (LoginGUIState) state;
        this.genericState = state;
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

    /**
     * Initializes the login scene, setting default values and event handlers.
     */
    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> login());
        exitButton.setOnAction(event -> exit(stage));
        chatBox.getStylesheets().add("/ChatStyle.css");
        chatBox.setOnMouseEntered(mouseEvent -> chatDisplay());
        chatBox.setOnMouseExited(mouseEvent -> chatHide());
        chatHide();
        // set default values
        ipTextField.setText("localhost");
        portTextField.setText("54321");
    }

    /**
     * Changes the scene to the setup stage with the setup parameters.
     *
     * @param playerSetup the player setup information
     * @param publicGoals the public goal cards
     * @param normalDeck the normal deck of playable cards
     * @param goldenDeck the golden deck of playable cards
     * @param playerNames the list of player names
     */
    public void changeToSetupState(PlayerSetup playerSetup,
                                   ArrayList<GoalCard> publicGoals,
                                   ArrayList<PlayableCard> normalDeck,
                                   ArrayList<PlayableCard> goldenDeck,
                                   ArrayList<String> playerNames) {
        SetupSceneController setupSceneController = new SetupSceneController(new SetupGUIState(state), stage);
        setupSceneController.updateChat(this.chat.getMessages());
        setupSceneController.getState().initialize(playerSetup, publicGoals, normalDeck, goldenDeck, playerNames);
        setupSceneController.showStage();
    }

    /**
     * Displays the stage.
     */
    public void showStage() {
        this.stage.setResizable(false);
        this.stage.show();
    }

    /**
     * Handles the login action, validating the username and opening the connection.
     */
    public void login() {
        String username = usernameTextField.getText();
        if (username.isEmpty()) {
            popUps.popUpMaker("Insert username please");
            return;
        }
        if (username.contains(" ")) {
            popUps.popUpMaker("No spaces allowed");
            return;
        }
        if (username.length() > 11) {
            popUps.popUpMaker("Username too long");
            return;
        }

        String serverIP = ipTextField.getText();
        int port = Integer.valueOf(portTextField.getText());

        state.initialize(username, serverIP, port);
        state.setLoginSceneController(this);
        state.execute();
        if (state.getServerHandler()!=null)
            state.getServerHandler().setLoginSceneController(this);
    }

    /**
     * Sets the maximum number of players and show popUp that let you choose the maximum number of players.
     */
    public void setPlayers() {
        state.setMaxPlayers(popUps.maxPlayersAlert());
    }

    /**
     * Resets the server handler in the state.
     */
    public void resetServerHandler() {
        state.resetServerHandler();
    }

    /**
     * Disables the login button.
     */
    public void disableLogin() {
        loginButton.setDisable(true);
    }

}
