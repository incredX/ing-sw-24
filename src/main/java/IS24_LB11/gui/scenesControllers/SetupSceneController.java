package IS24_LB11.gui.scenesControllers;

import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.phases.SetupGUIState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class SetupSceneController {
    @FXML
    private ImageView goalCard1;

    @FXML
    private ImageView goalCard2;

    @FXML
    private ImageView starterCard;
    @FXML
            private Button readyButton;

    Stage stage = new Stage();
    SetupGUIState state;
    public SetupSceneController(ClientGUIState state){
        this.state=(SetupGUIState) state;
        //change page
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
        chooseGoal(0);
        // TODO: load goal cards images
        starterCard.setOnMouseClicked(mouseEvent -> flipStarterCard());
        state.getServerHandler().setSetupSceneController(this);
        System.out.println("Setup initialized");
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
            state.shutdown();
            System.out.println("You successfully logged out!");
            stage.close();
        }
    }

    private void chooseGoal(int n){
        state.setChoosenGoalIndex(n);
        ColorAdjust colorAdjustChoosen = new ColorAdjust();
        ColorAdjust colorAdjustNotChoosen = new ColorAdjust();
        colorAdjustChoosen.setBrightness(0.5);
        colorAdjustNotChoosen.setBrightness(-0.5);
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
        //System.out.println("Flipped Starter Card: " + state.getStarterCard().asString());
        // TODO: flip card image
    }

    public SetupGUIState getState() {
        return state;
    }

    public void ready(){
        readyButton.setDisable(true);
        state.execute();
    }
}
