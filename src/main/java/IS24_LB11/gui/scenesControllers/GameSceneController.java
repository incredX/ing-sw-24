package IS24_LB11.gui.scenesControllers;


import IS24_LB11.game.PlacedCard;

import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.Position;
import IS24_LB11.gui.ImageLoader;
import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.phases.GameGUIState;
import IS24_LB11.gui.scenesControllers.ScoreboardController.AnimationInstruction;
import IS24_LB11.gui.scenesControllers.ScoreboardController.ScoreboardCoordinates;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.SequentialTransition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class GameSceneController extends GenericSceneController{
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab boardTab;
    @FXML
    private Tab scoreboardTab;
    @FXML
    protected BorderPane chatBox;
    @FXML
    protected TextArea messageBox;
    @FXML
    protected Button buttonSend;
    @FXML
    protected ImageView goalCard1;
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
    private Button flipHandCard1;
    @FXML
    private Button flipHandCard2;
    @FXML
    private Button flipHandCard3;
    // Board Variables
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Pane playerBoard;
    @FXML
    private ImageView cardBackground;
    @FXML
    private ImageView boardBackground;
    @FXML
    private Button centerBoardButton;
    @FXML
    private Tab handTab;

    @FXML
    private Text animal;
    @FXML
    private Text plant;
    @FXML
    private Text mushroom;
    @FXML
    private Text insect;
    @FXML
    private Text quill;
    @FXML
    private Text manuscript;
    @FXML
    private Text inkwell;


    GameGUIState state;
    int numberPlayerInGame;
    private final int centerBoardX = 10000;
    private final int centerBoardY = 10000;
    private final int cardX = 300;
    private final int cardY = 210;
    private final int cardCornerX = 70;
    private final int cardCornerY = 84;
    private ArrayList<ImageView> availableSpotsTemporaryCards = new ArrayList<>();

    ArrayList<AnimationInstruction> scoreboardPositions = new ArrayList<>();
    ArrayList<TranslateTransition> translations = new ArrayList<>();


    public GameSceneController(ClientGUIState state, Stage stage) {
        this.state = (GameGUIState) state;
        this.genericState=state;
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLScenes/GamePage.fxml"));
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
    public void initialize() {
        state.getServerHandler().setGameSceneController(this);
        numberPlayerInGame = state.getNumberOfPlayer();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        boardBackground.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/backGround.jpeg")));
        //cardBackground.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/backGround.jpeg")));
        // button and image event has to be declared here
        handCard1.setOnMouseClicked(mouseEvent -> chooseHandCard(0));
        handCard2.setOnMouseClicked(mouseEvent -> chooseHandCard(1));
        handCard3.setOnMouseClicked(mouseEvent -> chooseHandCard(2));
        flipHandCard1.setOnAction(mouseEvent -> flipHandCard(0));
        flipHandCard2.setOnAction(mouseEvent -> flipHandCard(1));
        flipHandCard3.setOnAction(mouseEvent -> flipHandCard(2));
        normalDeckCard1.setOnMouseClicked(mouseEvent -> chooseDeckCard(0, false));
        normalDeckCard2.setOnMouseClicked(mouseEvent -> chooseDeckCard(1, false));
        normalDeckCard3.setOnMouseClicked(mouseEvent -> chooseDeckCard(2, false));
        goldenDeckCard1.setOnMouseClicked(mouseEvent -> chooseDeckCard(0, true));
        goldenDeckCard2.setOnMouseClicked(mouseEvent -> chooseDeckCard(1, true));
        goldenDeckCard3.setOnMouseClicked(mouseEvent -> chooseDeckCard(2, true));

        disableDecks(true);

        centerBoardButton.setOnAction(mouseEvent -> moveToCenter(scrollPane));

        chatBox.setOnMouseEntered(mouseEvent -> chatDisplay());
        chatBox.setOnMouseExited(mouseEvent -> chatHide());
        buttonSend.setOnMouseClicked(mouseEvent -> send());
        chatHide();
        // load goal cards
        goalCard1.setImage(ImageLoader.getImage(state.getPublicGoals().get(0).asString()));
        goalCard2.setImage(ImageLoader.getImage(state.getPublicGoals().get(1).asString()));
        privateGoalCard.setImage(ImageLoader.getImage(state.getPlayer().getPersonalGoal().asString()));

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
        updateSymbolsCounter();
        scoreboardPositions = ScoreboardCoordinates.generate();



        scoreboardPositions.add(new AnimationInstruction(0, 430, 615));
        scoreboardPositions.add(new AnimationInstruction(1, 508, 615));
        scoreboardPositions.add(new AnimationInstruction(2, 586, 615));
        scoreboardPositions.add(new AnimationInstruction(3, 623, 544));
        scoreboardPositions.add(new AnimationInstruction(4, 545, 544));
        scoreboardPositions.add(new AnimationInstruction(5, 467, 544));
        scoreboardPositions.add(new AnimationInstruction(6, 389, 544));
        scoreboardPositions.add(new AnimationInstruction(7, 389, 473));
        scoreboardPositions.add(new AnimationInstruction(8, 467, 473));
        scoreboardPositions.add(new AnimationInstruction(9, 545, 473));
        scoreboardPositions.add(new AnimationInstruction(10, 623, 473));
        scoreboardPositions.add(new AnimationInstruction(11, 623, 402));
        scoreboardPositions.add(new AnimationInstruction(12, 545, 402));
        scoreboardPositions.add(new AnimationInstruction(13, 467, 402));
        scoreboardPositions.add(new AnimationInstruction(14, 389, 402));
        scoreboardPositions.add(new AnimationInstruction(15, 389, 331));
        scoreboardPositions.add(new AnimationInstruction(16, 467, 331));
        scoreboardPositions.add(new AnimationInstruction(17, 545, 331));
        scoreboardPositions.add(new AnimationInstruction(18, 623, 331));
        scoreboardPositions.add(new AnimationInstruction(19, 623, 260));
        scoreboardPositions.add(new AnimationInstruction(20, 508, 228));
        scoreboardPositions.add(new AnimationInstruction(21, 389, 260));
        scoreboardPositions.add(new AnimationInstruction(22, 389, 189));
        scoreboardPositions.add(new AnimationInstruction(23, 389, 118));
        scoreboardPositions.add(new AnimationInstruction(24, 432, 59));
        scoreboardPositions.add(new AnimationInstruction(25, 505, 45));
        scoreboardPositions.add(new AnimationInstruction(26, 578, 59));
        scoreboardPositions.add(new AnimationInstruction(27, 621, 118));
        scoreboardPositions.add(new AnimationInstruction(28, 621, 189));
        scoreboardPositions.add(new AnimationInstruction(29, 508, 131));


        for (int i=1; i<=scoreboardPositions.size(); i++) {

            int diffX = scoreboardPositions.get(i%30).getX() - scoreboardPositions.get((i-1)%30).getX();
            int diffY = scoreboardPositions.get(i%30).getY() - scoreboardPositions.get((i-1)%30).getY();

            translations.add(new TranslateTransition(Duration.millis(500)));
            translations.getLast().setByX(diffX);
            translations.getLast().setByY(diffY);

        }
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

    public void executeAnimations(int[] initScore, int[] finalScore){


        try {
            for (Color color : state.getPlayersColors().values()) {
                switch (color) {
                    case Color.RED:

                        animate(redPion, initScore[0], finalScore[0]);

                        break;

                    case Color.GREEN:

                        animate(greenPion, initScore[1], finalScore[1]);

                        break;

                    case Color.BLUE:

                        animate(bluePion, initScore[2], finalScore[2]);

                        break;

                    case Color.YELLOW:

                        animate(yellowPion, initScore[3], finalScore[3]);

                        break;
                }
            }
        }catch(InterruptedException e){
            System.out.println(e);
        }
    }

    public void updateGame(String currentPlayerTurn,
                           ArrayList<Integer> playerScores,
                           ArrayList<PlayableCard> normalDeck,
                           ArrayList<PlayableCard> goldenDeck) throws InterruptedException {

        //Useful for animating scores
        int[] initScore = new int[4];
        int[] finalScore = new int[4];

        for (int i=0; i <state.getPlayers().size(); i++) {
            initScore[i]=state.getPlayersScore().get(state.getPlayers().get(i));
        }

        state.update(currentPlayerTurn, playerScores, normalDeck, goldenDeck);

        for (int i=0; i <state.getPlayers().size(); i++) {
            finalScore[i]=state.getPlayersScore().get(state.getPlayers().get(i));
        }

        updateDeck();

        updateHand();

        executeAnimations(initScore, finalScore);

        updateScore();

        disableAllCardInputs(!state.isThisPlayerTurn());
    }
    public void updateGame(ArrayList<Integer> playerScores) throws InterruptedException {
        //Useful for animating scores
        int[] initScore = new int[4];
        int[] finalScore = new int[4];

        for (int i=0; i <state.getPlayers().size(); i++) {
            initScore[i]=state.getPlayersScore().get(state.getPlayers().get(i));
            System.out.println(initScore[i]);
        }

        state.update(playerScores);

        for (int i=0; i <state.getPlayers().size(); i++) {
            finalScore[i]=state.getPlayersScore().get(state.getPlayers().get(i));
            System.out.println(finalScore[i]);
        }


        executeAnimations(initScore, finalScore);

        updateScore();


    }

    private void disableGenericDeck(ImageView deckCard1, ImageView deckCard2, ImageView deckCard3, Boolean disable, Boolean deckType){
        int size = deckType?state.getGoldenDeck().size():state.getNormalDeck().size();
        switch (size){
            case 0:
                deckCard1.setDisable(true);
                deckCard2.setDisable(true);
                deckCard3.setDisable(true);
                deckCard1.setImage(null);
                deckCard2.setImage(null);
                deckCard3.setImage(null);
                break;
            case 1:
                deckCard1.setDisable(disable);
                deckCard2.setDisable(true);
                deckCard3.setDisable(true);
                deckCard2.setImage(null);
                deckCard3.setImage(null);
                break;
            case 2:
                deckCard1.setDisable(disable);
                deckCard2.setDisable(disable);
                deckCard3.setDisable(true);
                deckCard3.setImage(null);
                break;
            default:
                deckCard1.setDisable(disable);
                deckCard2.setDisable(disable);
                deckCard3.setDisable(disable);
                break;
        }
    }

    private void disableDecks(Boolean bool) {
        disableGenericDeck(normalDeckCard1,normalDeckCard2,normalDeckCard3,bool,false);
        disableGenericDeck(goldenDeckCard1,goldenDeckCard2,goldenDeckCard3,bool,true);
    }

    public void disableAllCardInputs(Boolean bool){
        disableHand(bool);
        disableDecks(bool);
    }

    public void updateDeck() {
        if (state.getNormalDeck().size()>=1)
            normalDeckCard1.setImage(ImageLoader.getImage(state.getNormalDeck().get(0).asString()));
        if (state.getNormalDeck().size()>=2)
            normalDeckCard2.setImage(ImageLoader.getImage(state.getNormalDeck().get(1).asString()));
        if (state.getNormalDeck().size()>=3) {
            StringBuffer flippedCard = new StringBuffer(state.getNormalDeck().get(2).asString());
            flippedCard.setCharAt(6, 'B');

            normalDeckCard3.setImage(ImageLoader.getImage(String.valueOf(flippedCard)));
        }
        if (state.getGoldenDeck().size()>=1)
            goldenDeckCard1.setImage(ImageLoader.getImage(state.getGoldenDeck().get(0).asString()));
        if (state.getGoldenDeck().size()>=2)
            goldenDeckCard2.setImage(ImageLoader.getImage(state.getGoldenDeck().get(1).asString()));
        if (state.getGoldenDeck().size()>=3) {
            StringBuffer flippedCard = new StringBuffer(state.getGoldenDeck().get(2).asString());
            flippedCard.setCharAt(6, 'B');

            goldenDeckCard3.setImage(ImageLoader.getImage(String.valueOf(flippedCard)));
        }
    }

    public void updateHand() {
        handCard1.setImage(ImageLoader.getImage(state.getPlayer().getHand().get(0).asString()));
        handCard1.setOpacity(1);

        handCard2.setImage(ImageLoader.getImage(state.getPlayer().getHand().get(1).asString()));
        handCard2.setOpacity(1);

        if (state.getPlayer().getHand().size()>2) {
            handCard3.setImage(ImageLoader.getImage(state.getPlayer().getHand().get(2).asString()));
            flipHandCard3.setVisible(true);
            flipHandCard3.setDisable(false);
        }
        else {
            handCard3.setImage(null);
            flipHandCard3.setVisible(false);
            flipHandCard3.setDisable(true);
        }
        handCard3.setOpacity(1);

    }

    private void disableHand(Boolean bool) {
        handCard1.setDisable(bool);
        handCard2.setDisable(bool);
        handCard3.setDisable(bool);
    }

    private void flipHandCard(int index) {
        switch (index) {
            case 0:
                state.getPlayer().getHand().get(index).flip();
                handCard1.setImage(ImageLoader.getImage(state.getPlayer().getHand().get(index).asString()));
                break;
            case 1:
                state.getPlayer().getHand().get(index).flip();
                handCard2.setImage(ImageLoader.getImage(state.getPlayer().getHand().get(index).asString()));
                break;
            case 2:
                state.getPlayer().getHand().get(index).flip();
                handCard3.setImage(ImageLoader.getImage(state.getPlayer().getHand().get(index).asString()));
                break;
        }
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
        clearTemporaryCardsFromBoard(playerBoard);
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

    }

    public void chooseDeckCard(int n, boolean deckType) {
        if(state.getPlayer().getHand().size()<3) {
            if (deckType)
                state.chooseCardToDraw(state.getGoldenDeck().get(n), n, deckType);
            else
                state.chooseCardToDraw(state.getNormalDeck().get(n), n, deckType);
            state.execute();
            updateHand();
            handTab.getTabPane().getSelectionModel().select(handTab);
        }
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

        setUsernamesBoard();

        String basePath = "/graphicResources/codexCards/pawns/";
        if (numberPlayerInGame >=2) {
            playerColor1.setImage(new Image(GameSceneController.class.getResourceAsStream(basePath + Color.toPawn(state.getPlayersColors().get(playerName1.getText())))));
            playerColor2.setImage(new Image(GameSceneController.class.getResourceAsStream(basePath + Color.toPawn(state.getPlayersColors().get(playerName2.getText())))));
        }
        if (numberPlayerInGame>=3)
            playerColor3.setImage(new Image(GameSceneController.class.getResourceAsStream(basePath + Color.toPawn(state.getPlayersColors().get(playerName3.getText())))));


        hidePlayersInScoreboard();
        setUsernamesBoard();
        updateScore();

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
            availableSpot.setOpacity(0.7);
            ImageLoader.roundCorners(availableSpot);

            if(!imageViewInPosition(availableSpot)){
                playerBoard.getChildren().add(availableSpot);
                availableSpotsTemporaryCards.add(availableSpot);
                availableSpot.setOnMouseClicked(mouseEvent -> placeCard(availableSpot, selectedCardFromHand));
            }
        }
    }


    private void placeCard(ImageView availableSpot, ImageView selectedCardFromHand){

        if(this.state.getPlayer().name().equals("Jonh") || this.state.getPlayer().name().equals("John") ||
                this.state.getPlayer().name().equals("john") || this.state.getPlayer().name().equals("jonh"))
            boardBackground.setImage(new Image(GameSceneController.class.getResourceAsStream("/graphicResources/codexCards/croppedCards/croppedBack/GXB.png")));


        Position realPosition = getRealPosition((int)availableSpot.getLayoutX(), (int)availableSpot.getLayoutY());

        ImageView cardToBePlaced = new ImageView(selectedCardFromHand.getImage());
        cardToBePlaced.setFitWidth(availableSpot.getFitWidth());
        cardToBePlaced.setFitHeight(availableSpot.getFitHeight());
        cardToBePlaced.setLayoutX(availableSpot.getLayoutX());
        cardToBePlaced.setLayoutY(availableSpot.getLayoutY());

        ImageLoader.roundCorners(cardToBePlaced);

        if(state.placeCard(new PlacedCard(state.getCardChooseToPlay(), realPosition))){
            playerBoard.getChildren().add(cardToBePlaced);
            updateSymbolsCounter();
            clearTemporaryCardsFromBoard(playerBoard);
            updateHand();
            disableHand(true);
            state.setPositionOfPlacedCard(realPosition);
            if((state.getGoldenDeck().size() == 0 && state.getNormalDeck().size()==0) || state.isFinalTurn()){
                state.execute();
            }
        }
        else {
            this.addMessage("Card can't be placed there");
        }
    }


    public void updateSymbolsCounter() {
        HashMap<Symbol,Integer> data = state.getPlayer().getBoard().getSymbolCounter();

        animal.setText(String.valueOf(data.get(Suit.ANIMAL)));
        plant.setText(String.valueOf(data.get(Suit.PLANT)));
        mushroom.setText(String.valueOf(data.get(Suit.MUSHROOM)));
        insect.setText(String.valueOf(data.get(Suit.INSECT)));

        inkwell.setText(String.valueOf(data.get(Item.INKWELL)));
        quill.setText(String.valueOf(data.get(Item.QUILL)));
        manuscript.setText(String.valueOf(data.get(Item.MANUSCRIPT)));
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
    public void setFinalTurn(){
        state.setIsFinalTurn(true);
    }



    private void animate (ImageView player, int startingPoints, int finalPoints) throws InterruptedException {

        ArrayList<TranslateTransition> tts = new ArrayList<>();


        SequentialTransition st = new SequentialTransition(player);


        st.getChildren().addAll(getSubarray(startingPoints, finalPoints));

        st.setNode(player);

        st.play();
    }


    private ArrayList<TranslateTransition> getSubarray(int startingPoints, int finalPoints) {

        ArrayList<TranslateTransition> tts = new ArrayList<>();


        for (int i=startingPoints ; i < finalPoints ; i++) {
            tts.add(translations.get(i%30));
        }


        return tts;
    }

}
