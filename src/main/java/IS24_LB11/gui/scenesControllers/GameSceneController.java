package IS24_LB11.gui.scenesControllers;


import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.gui.ImageLoader;
import IS24_LB11.gui.PopUps;
import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.phases.GameGUIState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
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
    @FXML
    private ImageView playerColor1;
    @FXML
    private Text playerName1;
    @FXML
    private Text playerScore1;
    @FXML
    private ImageView playerColor2;
    @FXML
    private Text playerName2;
    @FXML
    private Text playerScore2;
    @FXML
    private ImageView playerColor3;
    @FXML
    private Text playerName3;
    @FXML
    private Text playerScore3;
    @FXML
    private ImageView playerColor4;
    @FXML
    private Text playerName4;
    @FXML
    private Text playerScore4;
    @FXML
    private Button chatButton;
    Stage stage = new Stage();
    Stage chatStage = new Stage();
    GameGUIState state;
    int numberPlayerInGame;

    ChatSceneController chatSceneController;

    public GameSceneController(ClientGUIState state) {
        this.state=(GameGUIState) state;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GamePageBackup.fxml"));
        loader.setController(this);

        this.stage.setTitle("Codex");
        try {
            this.stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FXMLLoader loaderChat = new FXMLLoader(getClass().getResource("/CHatView.fxml"));
        chatSceneController = new ChatSceneController((GameGUIState) state,chatStage);
        loaderChat.setController(chatSceneController);
        state.getServerHandler().setChatSceneController(chatSceneController);
        try {
            chatStage.setScene(new Scene(loaderChat.load()));
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

        numberPlayerInGame=state.getNumberOfPlayer();
        // button and image event has to be declared here
        handCard1.setOnMouseClicked(mouseEvent -> chooseHandCard(0));
        handCard2.setOnMouseClicked(mouseEvent -> chooseHandCard(1));
        handCard3.setOnMouseClicked(mouseEvent -> chooseHandCard(2));
        normalDeckCard1.setOnMouseClicked(mouseEvent -> chooseDeckCard(0,false));
        normalDeckCard2.setOnMouseClicked(mouseEvent -> chooseDeckCard(1,false));
        normalDeckCard3.setOnMouseClicked(mouseEvent -> chooseDeckCard(2,false));
        goldenDeckCard1.setOnMouseClicked(mouseEvent -> chooseDeckCard(0,true));
        goldenDeckCard2.setOnMouseClicked(mouseEvent -> chooseDeckCard(1,true));
        goldenDeckCard3.setOnMouseClicked(mouseEvent -> chooseDeckCard(2,true));
        chatButton.setOnMouseClicked(mouseEvent -> showChat());
        //same as disconnected
        hidePlayersInScoreboard();
        setUsernamesBoard();
    }

    private void showChat() {
        chatStage.show();
    }

    private void hidePlayersInScoreboard(){
        if (state.getNumberOfPlayer()<=3){
            playerColor4.setVisible(false);
            playerName4.setVisible(false);
            playerScore4.setVisible(false);
        }
        if (state.getNumberOfPlayer()<=2){
            playerColor3.setVisible(false);
            playerName3.setVisible(false);
            playerScore3.setVisible(false);
        }
        if (state.getNumberOfPlayer()<=1){
            playerColor2.setVisible(false);
            playerName2.setVisible(false);
            playerScore2.setVisible(false);
        }
    }

    private void setUsernamesBoard() {
        if (numberPlayerInGame>=2){
            playerName1.setText(state.getPlayers().get(0));
            playerName2.setText(state.getPlayers().get(1));
        }
        if (numberPlayerInGame>=3){
            playerName3.setText(state.getPlayers().get(2));
        }
        if (numberPlayerInGame>=4){
            playerName4.setText(state.getPlayers().get(3));
        }

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
        updateScore();
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
        if (numberPlayerInGame>=2){
            playerScore1.setText(String.valueOf(state.getPlayersScore().get(playerName1.getText())));
            playerScore2.setText(String.valueOf(state.getPlayersScore().get(playerName2.getText())));
        }if (numberPlayerInGame>=3){
            playerScore3.setText(String.valueOf(state.getPlayersScore().get(playerName3.getText())));
        }if (numberPlayerInGame>=4){
            playerScore4.setText(String.valueOf(state.getPlayersScore().get(playerName4.getText())));
        }
    }
    public void chooseHandCard(int n){
        ColorAdjust colorAdjustNotChoosen = new ColorAdjust();
        ColorAdjust colorAdjustChoosen = new ColorAdjust();
        colorAdjustNotChoosen.setContrast(-0.5);
        colorAdjustChoosen.setContrast(0);
        switch (n){
            case 0:
                state.chooseCardToPlay(state.getPlayer().getHand().get(0));
                handCard1.setEffect(colorAdjustChoosen);
                handCard2.setEffect(colorAdjustNotChoosen);
                handCard3.setEffect(colorAdjustNotChoosen);
                break;
            case 1:
                state.chooseCardToPlay(state.getPlayer().getHand().get(1));
                handCard1.setEffect(colorAdjustNotChoosen);
                handCard2.setEffect(colorAdjustChoosen);
                handCard3.setEffect(colorAdjustNotChoosen);
                break;
            case 2:
                state.chooseCardToPlay(state.getPlayer().getHand().get(2));
                handCard1.setEffect(colorAdjustNotChoosen);
                handCard2.setEffect(colorAdjustNotChoosen);
                handCard3.setEffect(colorAdjustChoosen);
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

    public void removePlayer(String playerDisconnected) {
        String color = state.getPlayersColors().get(playerDisconnected);
        state.removePlayer(playerDisconnected);
        numberPlayerInGame--;
        if (playerDisconnected.equals(playerName1.getText())){
            playerColor1.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/codexCards/pawns/greenPawn.png")));
            playerColor2.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/codexCards/pawns/yellowPawn.png")));
            playerColor3.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/codexCards/pawns/bluePawn.png")));
        }
        if (playerDisconnected.equals(playerName2.getText())){
            playerColor2.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/codexCards/pawns/yellowPawn.png")));
            playerColor3.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/codexCards/pawns/bluePawn.png")));
        }
        if (playerDisconnected.equals(playerName3.getText())){
            playerColor3.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/codexCards/pawns/bluePawn.png")));
        }
        hidePlayersInScoreboard();
        setUsernamesBoard();
        updateScore();
    }

    public void showPopUpNotification(String message) {
        PopUps popUps = new PopUps();
        popUps.popUpMaker(message);
    }
}
