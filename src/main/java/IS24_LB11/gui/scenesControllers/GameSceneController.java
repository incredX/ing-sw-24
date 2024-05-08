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
import javafx.scene.effect.ColorAdjust;
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
    @FXML
    private ImageView handCard1;
    @FXML
    private ImageView handCard2;
    @FXML
    private ImageView handCard3;
    @FXML
    private ImageView normalDeckCard1;
    @FXML
    private ImageView normalDeckCard2;
    @FXML
    private ImageView normalDeckCard3;
    @FXML
    private ImageView goldenDeckCard1;
    @FXML
    private ImageView goldenDeckCard2;
    @FXML
    private ImageView goldenDeckCard3;
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
        handCard1.setOnMouseClicked(mouseEvent ->chooseHandCard(0));
        handCard2.setOnMouseClicked(mouseEvent ->chooseHandCard(1));
        handCard3.setOnMouseClicked(mouseEvent ->chooseHandCard(2));
        normalDeckCard1.setOnMouseClicked(mouseEvent -> chooseDeckCard(0,false));
        normalDeckCard2.setOnMouseClicked(mouseEvent -> chooseDeckCard(1,false));
        normalDeckCard3.setOnMouseClicked(mouseEvent -> chooseDeckCard(2,false));
        goldenDeckCard1.setOnMouseClicked(mouseEvent -> chooseDeckCard(0,true));
        goldenDeckCard2.setOnMouseClicked(mouseEvent -> chooseDeckCard(1,true));
        goldenDeckCard3.setOnMouseClicked(mouseEvent -> chooseDeckCard(2,true));

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
        updateDeck();
        updateHand();
        reloadBoard();
    }
    private void reloadBoard() {

    }
    public void updateDeck(){
        normalDeckCard1.setImage(ImageLoader.getImage(state.getNormalDeck().get(0).asString()));
        normalDeckCard2.setImage(ImageLoader.getImage(state.getNormalDeck().get(1).asString()));
        normalDeckCard3.setImage(ImageLoader.getImage(state.getNormalDeck().get(2).asString()));
        goldenDeckCard1.setImage(ImageLoader.getImage(state.getGoldenDeck().get(0).asString()));
        goldenDeckCard2.setImage(ImageLoader.getImage(state.getGoldenDeck().get(1).asString()));
        goldenDeckCard3.setImage(ImageLoader.getImage(state.getGoldenDeck().get(2).asString()));
    }
    public void updateHand(){
        handCard1.setImage(ImageLoader.getImage(state.getPlayer().getHand().get(0).asString()));
        handCard2.setImage(ImageLoader.getImage(state.getPlayer().getHand().get(1).asString()));
        handCard3.setImage(ImageLoader.getImage(state.getPlayer().getHand().get(2).asString()));
    }
    public void updateScore(){
        //move pions
    }
    public void chooseHandCard(int n){
        ColorAdjust colorAdjustNotChoosen = new ColorAdjust();
        colorAdjustNotChoosen.setContrast(-0.5);
        switch (n){
            case 0:
                state.chooseCardToPlay(state.getPlayer().getHand().get(0));
                handCard2.setEffect(colorAdjustNotChoosen);
                handCard3.setEffect(colorAdjustNotChoosen);
                break;
            case 1:
                state.chooseCardToPlay(state.getPlayer().getHand().get(1));
                handCard1.setEffect(colorAdjustNotChoosen);
                handCard3.setEffect(colorAdjustNotChoosen);
                break;
            case 2:
                state.chooseCardToPlay(state.getPlayer().getHand().get(2));
                handCard1.setEffect(colorAdjustNotChoosen);
                handCard2.setEffect(colorAdjustNotChoosen);
                break;
        }
    }
    public void chooseDeckCard(int n,boolean deckType){
        if (deckType)
            state.chooseCardToDraw(state.getGoldenDeck().get(n),n,deckType);
        else
            state.chooseCardToDraw(state.getNormalDeck().get(n),n,deckType);
        state.execute();
    }
    public GameGUIState getState() {
        return state;
    }

}
