package IS24_LB11.gui.scenesControllers;


import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.gui.ImageLoader;
import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.phases.GameGUIState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class GameSceneController {
    @FXML
    private ImageView goalCard1;
    @FXML
            private ImageView goalCard2;
    @FXML
            private ImageView privateGoalCard;
    @FXML
            private ImageView redPion;
    @FXML
            private ImageView greenPion;
    @FXML
            private ImageView bluePion;
    @FXML
            private ImageView yellowPion;

    Stage stage = new Stage();
    GameGUIState state;

    public GameSceneController(ClientGUIState state) {
        this.state=(GameGUIState) state;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GamePage.fxml"));
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
    public void initialize(){
        state.getServerHandler().setGameSceneController(this);
        // button and image event has to be declared here
    }

    public void showStage(){
        goalCard1.setImage(ImageLoader.getImage(state.getPublicGoals().get(0).asString()));
        goalCard2.setImage(ImageLoader.getImage(state.getPublicGoals().get(1).asString()));
        privateGoalCard.setImage(ImageLoader.getImage(state.getPlayer().getPersonalGoal().asString()));
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
    public void updateGame(String currentPlayerTurn,
                           ArrayList<Integer> playerScores,
                           ArrayList<PlayableCard> normalDeck,
                           ArrayList<PlayableCard> goldenDeck) {
        state.update(currentPlayerTurn,
                playerScores,
                normalDeck,
                goldenDeck);
        reloadBoard();
    }

    private void reloadBoard() {

    }

    public GameGUIState getState() {
        return state;
    }
}
