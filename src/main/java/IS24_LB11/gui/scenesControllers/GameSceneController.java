package IS24_LB11.gui.scenesControllers;


import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Position;
import IS24_LB11.gui.ImageLoader;
import IS24_LB11.gui.PopUps;
import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.phases.GameGUIState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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
    Stage stage;
    Stage chatStage = new Stage();
    GameGUIState state;
    int numberPlayerInGame;

    // Board Variables
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Pane playerBoard;

    private final int centerBoardX = 10000;
    private final int centerBoardY = 10000;

    private final int cardX = 300;
    private final int cardY = 210;

    private final int cardCornerX = 70;
    private final int cardCornerY = 84;

    private ArrayList<ImageView> availableSpotsTemporaryCards = new ArrayList<>();

    ChatSceneController chatSceneController;

    public GameSceneController(ClientGUIState state, Stage stage) {
        this.state = (GameGUIState) state;
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GamePageBackup.fxml"));
        loader.setController(this);

        this.stage.setTitle("Codex");
        try {
            this.stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FXMLLoader loaderChat = new FXMLLoader(getClass().getResource("/ChatView.fxml"));
        chatSceneController = new ChatSceneController((GameGUIState) state, chatStage);
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
    public void initialize() {
        state.getServerHandler().setGameSceneController(this);

        numberPlayerInGame = state.getNumberOfPlayer();
        // button and image event has to be declared here
        handCard1.setOnMouseClicked(mouseEvent -> chooseHandCard(0));
        handCard2.setOnMouseClicked(mouseEvent -> chooseHandCard(1));
        handCard3.setOnMouseClicked(mouseEvent -> chooseHandCard(2));
        normalDeckCard1.setOnMouseClicked(mouseEvent -> chooseDeckCard(0, false));
        normalDeckCard2.setOnMouseClicked(mouseEvent -> chooseDeckCard(1, false));
        normalDeckCard3.setOnMouseClicked(mouseEvent -> chooseDeckCard(2, false));
        goldenDeckCard1.setOnMouseClicked(mouseEvent -> chooseDeckCard(0, true));
        goldenDeckCard2.setOnMouseClicked(mouseEvent -> chooseDeckCard(1, true));
        goldenDeckCard3.setOnMouseClicked(mouseEvent -> chooseDeckCard(2, true));

        disableDecks(true);

        chatButton.setOnMouseClicked(mouseEvent -> showChat());

        // load goal cards
        goalCard1.setImage(ImageLoader.getImage(state.getPublicGoals().get(0).asString()));
//        ImageLoader.roundCorners(goalCard1);

        goalCard2.setImage(ImageLoader.getImage(state.getPublicGoals().get(1).asString()));
//        ImageLoader.roundCorners(goalCard2);

        privateGoalCard.setImage(ImageLoader.getImage(state.getPlayer().getPersonalGoal().asString()));
//        ImageLoader.roundCorners(privateGoalCard);

        //same as disconnected
        switch (numberPlayerInGame) {
            case 3:
                yellowPion.setVisible(false);
                break;
            case 2:
                yellowPion.setVisible(false);
                bluePion.setVisible(false);
                break;
        }
        hidePlayersInScoreboard();
        setUsernamesBoard();
    }

    private void showChat() {
        chatStage.show();
    }

    private void hideInitialPawns() {
        if (numberPlayerInGame <= 3)
            yellowPion.setVisible(false);
        if (numberPlayerInGame <= 2)
            bluePion.setVisible(false);
    }

    private void hidePlayersInScoreboard() {
        if (state.getNumberOfPlayer() <= 3) {
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
        if (numberPlayerInGame >= 2) {
            playerName1.setText(state.getPlayers().get(0));
            playerName2.setText(state.getPlayers().get(1));
        }
        if (numberPlayerInGame >= 3) {
            playerName3.setText(state.getPlayers().get(2));
        }
        if (numberPlayerInGame >= 4) {
            playerName4.setText(state.getPlayers().get(3));
        }

    }

    public void showStage() {
        // Place starterCard
        moveToCenter(scrollPane);
        ImageView starterCard =  getImageView(state.getPlayer().getBoard().getPlacedCards().getFirst());
        ImageLoader.roundCorners(starterCard);
        playerBoard.getChildren().add(starterCard);
        scrollPane.setPannable(true);

        this.stage.setResizable(false);
        this.stage.show();
    }

    public void exit(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit!");
        alert.setContentText("Are you sure?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            state.shutdown();
            System.out.println("You successfully logged out!");
            stage.close();
        }
    }

    public void updateGame(String currentPlayerTurn,
                           ArrayList<Integer> playerScores,
                           ArrayList<PlayableCard> normalDeck,
                           ArrayList<PlayableCard> goldenDeck) {
        state.update(currentPlayerTurn, playerScores, normalDeck, goldenDeck);
        updateDeck();
        updateHand();
        updateScore();
        disableAllCardInputs(!state.isThisPlayerTurn());
    }

    private void disableHand(Boolean bool) {
       handCard1.setDisable(bool);
       handCard2.setDisable(bool);
       handCard3.setDisable(bool);
    }

    private void disableDecks(Boolean bool) {
        switch (state.getNormalDeck().size()){
            case 0:
                normalDeckCard1.setDisable(false);
                normalDeckCard2.setDisable(false);
                normalDeckCard3.setDisable(false);
                break;
            case 1:
                normalDeckCard1.setDisable(bool);
                normalDeckCard2.setDisable(false);
                normalDeckCard3.setDisable(false);
                break;
            case 2:
                normalDeckCard1.setDisable(bool);
                normalDeckCard2.setDisable(bool);
                normalDeckCard3.setDisable(false);
                break;
            default:
                normalDeckCard1.setDisable(bool);
                normalDeckCard2.setDisable(bool);
                normalDeckCard3.setDisable(bool);
                break;
        }
        switch (state.getGoldenDeck().size()){
            case 0:
                goldenDeckCard1.setDisable(false);
                goldenDeckCard2.setDisable(false);
                goldenDeckCard3.setDisable(false);
                break;
            case 1:
                goldenDeckCard1.setDisable(bool);
                goldenDeckCard2.setDisable(false);
                goldenDeckCard3.setDisable(false);
                break;
            case 2:
                goldenDeckCard1.setDisable(bool);
                goldenDeckCard2.setDisable(bool);
                goldenDeckCard3.setDisable(false);
                break;
            default:
                goldenDeckCard1.setDisable(bool);
                goldenDeckCard2.setDisable(bool);
                goldenDeckCard3.setDisable(bool);
                break;
        }
    }

        private void disableAllCardInputs(Boolean bool){
        disableHand(bool);
        disableDecks(bool);
    }

    public void updateDeck() {
        if (state.getNormalDeck().size()>=1)
            normalDeckCard1.setImage(ImageLoader.getImage(state.getNormalDeck().get(0).asString()));
        if (state.getNormalDeck().size()>=2)
            normalDeckCard2.setImage(ImageLoader.getImage(state.getNormalDeck().get(1).asString()));
        if (state.getNormalDeck().size()>=3) {
            PlayableCard lastCardNormalDeck = state.getNormalDeck().get(2);
            lastCardNormalDeck.flip();
            normalDeckCard3.setImage(ImageLoader.getImage(lastCardNormalDeck.asString()));
        }
//        PlayableCard flippedNormalCard = state.getNormalDeck().get(2);
//        flippedNormalCard.flip();
//        normalDeckCard3.setImage(ImageLoader.getImage(flippedNormalCard.asString()));

        if (state.getGoldenDeck().size()>=1)
            goldenDeckCard1.setImage(ImageLoader.getImage(state.getGoldenDeck().get(0).asString()));
        if (state.getGoldenDeck().size()>=2)
            goldenDeckCard2.setImage(ImageLoader.getImage(state.getGoldenDeck().get(1).asString()));
        if (state.getGoldenDeck().size()>=3) {
            PlayableCard lastCardGoldenDeck = state.getGoldenDeck().get(2);
            lastCardGoldenDeck.flip();
            goldenDeckCard3.setImage(ImageLoader.getImage(lastCardGoldenDeck.asString()));
        }
//        PlayableCard flippedGoldenCard = state.getGoldenDeck().get(2);
//        flippedGoldenCard.flip();
//        goldenDeckCard3.setImage(ImageLoader.getImage(flippedGoldenCard.asString()));
    }

    public void updateHand() {
        handCard1.setImage(ImageLoader.getImage(state.getPlayer().getHand().get(0).asString()));
        handCard1.setOpacity(1);

        handCard2.setImage(ImageLoader.getImage(state.getPlayer().getHand().get(1).asString()));
        handCard2.setOpacity(1);

        if (state.getPlayer().getHand().size()>2) {
            handCard3.setDisable(false);
            handCard3.setImage(ImageLoader.getImage(state.getPlayer().getHand().get(2).asString()));
        }
        else {
            handCard3.setImage(null);
            handCard3.setDisable(true);
        }
        handCard3.setOpacity(1);

    }

    public void updateScore() {
        if (numberPlayerInGame >= 2) {
            playerScore1.setText(String.valueOf(state.getPlayersScore().get(playerName1.getText())));
            playerScore2.setText(String.valueOf(state.getPlayersScore().get(playerName2.getText())));
        }
        if (numberPlayerInGame >= 3) {
            playerScore3.setText(String.valueOf(state.getPlayersScore().get(playerName3.getText())));
        }
        if (numberPlayerInGame >= 4) {
            playerScore4.setText(String.valueOf(state.getPlayersScore().get(playerName4.getText())));
        }
    }

    public void chooseHandCard(int n) {


        switch (n) {
            case 0:
                state.chooseCardToPlay(state.getPlayer().getHand().getFirst());
                handCard1.setOpacity(1);
                handCard2.setOpacity(0.5);
                handCard3.setOpacity(0.5);
                placeTemporaryCardsOnAvailableSpots(handCard1);
                break;
            case 1:
                state.chooseCardToPlay(state.getPlayer().getHand().get(1));
                handCard1.setOpacity(0.5);
                handCard2.setOpacity(1);
                handCard3.setOpacity(0.5);
                placeTemporaryCardsOnAvailableSpots(handCard2);
                break;
            case 2:
                state.chooseCardToPlay(state.getPlayer().getHand().get(2));
                handCard1.setOpacity(0.5);
                handCard2.setOpacity(0.5);
                handCard3.setOpacity(1);
                placeTemporaryCardsOnAvailableSpots(handCard3);
                break;
        }
        if (state.getNormalDeck().size()==0 && state.getGoldenDeck().size()==0)
            state.execute();
    }

    public void chooseDeckCard(int n, boolean deckType) {
        if (deckType)
            state.chooseCardToDraw(state.getGoldenDeck().get(n), n, deckType);
        else
            state.chooseCardToDraw(state.getNormalDeck().get(n), n, deckType);
        state.execute();
        updateHand();
    }

    public GameGUIState getState() {
        return state;
    }

    public void removePlayer(String playerDisconnected) {
        switch (state.getPlayersColors().get(playerDisconnected)) {
            case GREEN -> greenPion.setVisible(false);
            case RED -> redPion.setVisible(false);
            case BLUE -> bluePion.setVisible(false);
            case YELLOW -> yellowPion.setVisible(false);
        }
        state.removePlayer(playerDisconnected);
        numberPlayerInGame--;
        if (playerDisconnected.equals(playerName1.getText())) {
            playerColor1.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/codexCards/pawns/greenPawn.png")));
            playerColor2.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/codexCards/pawns/yellowPawn.png")));
            playerColor3.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/codexCards/pawns/bluePawn.png")));
        }
        if (playerDisconnected.equals(playerName2.getText())) {
            playerColor2.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/codexCards/pawns/yellowPawn.png")));
            playerColor3.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/codexCards/pawns/bluePawn.png")));
        }
        if (playerDisconnected.equals(playerName3.getText())) {
            playerColor3.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/codexCards/pawns/bluePawn.png")));
        }
        hidePlayersInScoreboard();
        setUsernamesBoard();
        updateScore();
    }

    private void clearBoard(Pane playerBoard){
        playerBoard.getChildren().clear();
    }

    private void clearTemporaryCardsFromBoard(Pane playerBoard){
        for(ImageView imageView : availableSpotsTemporaryCards){
            playerBoard.getChildren().remove(imageView);
        }
        availableSpotsTemporaryCards.clear();
    }

    private void moveToCenter(ScrollPane scrollPane){
        Platform.runLater(() -> {
            scrollPane.setHvalue((double) (centerBoardX+(cardX/2)) / playerBoard.getWidth());
            scrollPane.setVvalue((double) (centerBoardY+(cardY/2)) / playerBoard.getHeight());
        });
    }

    private ImageView getImageView(PlacedCard placedCard){
        Image image = ImageLoader.getImage(placedCard.card().asString());
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(cardX);
        imageView.setFitHeight(cardY);

        Position positionOnBoard = getPositionOnBoard(placedCard.position().getX(), placedCard.position().getY());

        imageView.setLayoutX(positionOnBoard.getX()); // X coordinate
        imageView.setLayoutY(positionOnBoard.getY()); // Y coordinate

        return imageView;
    }

    private ImageView getImageView(String custom, int x, int y){
        Image image = ImageLoader.getImage(custom);
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(cardX);
        imageView.setFitHeight(cardY);

        Position positionOnBoard = getPositionOnBoard(x, y);

        imageView.setLayoutX(positionOnBoard.getX()); // X coordinate
        imageView.setLayoutY(positionOnBoard.getY()); // Y coordinate

        return imageView;
    }

    private Position getPositionOnBoard(int x, int y){

        Position pos =
                new Position(centerBoardX+(x*(cardX-cardCornerX)), centerBoardY+(y*(cardY-cardCornerY)));

        return pos;
    }

    private ArrayList<Position> getAvailableSpots(){
        return state.getPlayer().getBoard().getAvailableSpots();
    }

    private void placeTemporaryCardsOnAvailableSpots(ImageView selectedCardFromHand){
        for(Position pos : getAvailableSpots()){
            ImageView availableSpot = getImageView("AvailableSpot", pos.getX(), pos.getY());
            availableSpot.setOpacity(0.5);
            ImageLoader.roundCorners(availableSpot);

            if(!imageViewInPosition(availableSpot)){
                playerBoard.getChildren().add(availableSpot);
                availableSpotsTemporaryCards.add(availableSpot);
                availableSpot.setOnMouseClicked(mouseEvent -> placeCard(availableSpot, selectedCardFromHand));
            }
        }
    }


    private void placeCard(ImageView availableSpot, ImageView selectedCardFromHand){
        Position realPosition = getRealPosition((int)availableSpot.getLayoutX(), (int)availableSpot.getLayoutY());

        ImageView cardToBePlaced = new ImageView(selectedCardFromHand.getImage());
        cardToBePlaced.setFitWidth(availableSpot.getFitWidth());
        cardToBePlaced.setFitHeight(availableSpot.getFitHeight());
        cardToBePlaced.setLayoutX(availableSpot.getLayoutX());
        cardToBePlaced.setLayoutY(availableSpot.getLayoutY());

        ImageLoader.roundCorners(cardToBePlaced);

        if(state.placeCard(new PlacedCard(state.getCardChooseToPlay(), realPosition))){
            playerBoard.getChildren().add(cardToBePlaced);
            clearTemporaryCardsFromBoard(playerBoard);
            updateHand();
            disableHand(true);
            state.setPositionOfPlacedCard(realPosition);
        }
        else {
            System.out.printf("\nCard can't be placed there\n");
        }
    }

    private Position getRealPosition(int relativeX, int relativeY){
        int realX = (relativeX - centerBoardX)/(cardX - cardCornerX);
        int realY = (relativeY - centerBoardY)/(cardY - cardCornerY);

        return new Position(realX, realY);
    }

    private boolean imageViewInPosition(ImageView imageView){
        for(Node alreadyPlaced : playerBoard.getChildren()){
            if(alreadyPlaced.getLayoutX() == imageView.getLayoutX() && alreadyPlaced.getLayoutY() == imageView.getLayoutY()){
                return true;
            }
        }
        return false;
    }

    public void showPopUpNotification(String message) {
        PopUps popUps = new PopUps();
        popUps.popUpMaker(message);
    }

    public void showExitNotification(String s) {
        PopUps popUps = new PopUps();
        popUps.lastPlayerLeft(stage,state);
    }
}
