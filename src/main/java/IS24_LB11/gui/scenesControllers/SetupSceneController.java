package IS24_LB11.gui.scenesControllers;

import IS24_LB11.gui.ImageLoader;
import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.phases.GameGUIState;
import IS24_LB11.gui.phases.SetupGUIState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class SetupSceneController extends GenericSceneController{
    @FXML
    private ImageView goalCard1;

    @FXML
    private ImageView goalCard2;

    @FXML
    private ImageView starterCard;
    @FXML
    private Button readyButton;
    @FXML
    private ImageView publicGoal1;
    @FXML
    private ImageView publicGoal2;


    SetupGUIState state;

    public SetupSceneController(ClientGUIState state, Stage stage){
        this.state=(SetupGUIState) state;
        this.genericState=state;
        this.stage=stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SetupPage.fxml"));
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
        readyButton.setOnAction(event->ready());
        goalCard1.setOnMouseClicked(mouseEvent -> chooseGoal(0));
        goalCard2.setOnMouseClicked(mouseEvent -> chooseGoal(1));
        starterCard.setOnMouseClicked(mouseEvent -> flipStarterCard());
        state.getServerHandler().setSetupSceneController(this);
        System.out.println("Setup initialized");

        chatBox.setOnMouseEntered(mouseEvent -> chatDisplay());
        chatBox.setOnMouseExited(mouseEvent -> chatHide());
        buttonSend.setOnMouseClicked(mouseEvent -> send());
        addMessage("Enter with mouse the chat to expand the chat");
        addMessage("Exit with mouse the chat to minimize the chat");
        addMessage("Type help for commands");
        chatHide();
    }

    public void changeToGameState(){
        GameSceneController gameSceneController = new GameSceneController(new GameGUIState(state),stage);
        gameSceneController.updateChat(this.chat.getMessages());
        gameSceneController.showStage();
    }
    public void showStage() {
        chatDisplay();
        goalCard1.setImage(ImageLoader.getImage(state.getPrivateGoals().get(0).asString()));
        goalCard2.setImage(ImageLoader.getImage(state.getPrivateGoals().get(1).asString()));
        starterCard.setImage(ImageLoader.getImage(state.getStarterCard().asString()));
        publicGoal1.setImage(ImageLoader.getImage(state.getPublicGoals().get(0).asString()));
        publicGoal2.setImage(ImageLoader.getImage(state.getPublicGoals().get(1).asString()));
        chooseGoal(0);

        this.stage.setResizable(false);
        this.stage.show();
    }

    private void chooseGoal(int n){
        state.setChoosenGoalIndex(n);
        ColorAdjust colorAdjustChoosen = new ColorAdjust();
        ColorAdjust colorAdjustNotChoosen = new ColorAdjust();
        colorAdjustChoosen.setContrast(0);
        colorAdjustNotChoosen.setContrast(-0.5);
        if (n==1){
            goalCard1.setEffect(colorAdjustNotChoosen);
            goalCard2.setEffect(colorAdjustChoosen);
        }
        else{
            goalCard2.setEffect(colorAdjustNotChoosen);
            goalCard1.setEffect(colorAdjustChoosen);
        }
    }

    private void flipStarterCard(){
        state.flipStarterCard();
        starterCard.setImage(ImageLoader.getImage(state.getStarterCard().asString()));
    }

    public SetupGUIState getState() {
        return state;
    }

    public void ready(){
        goalCard1.setDisable(true);
        goalCard2.setDisable(true);
        starterCard.setDisable(true);
        readyButton.setDisable(true);
        state.execute();
    }

    public void removePlayer(String playerDisconnected) {
        state.removePlayer(playerDisconnected);
    }



}
