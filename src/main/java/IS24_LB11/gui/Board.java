package IS24_LB11.gui;

import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.components.CardFactory;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Position;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Board extends Application {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Pane playerBoard;

    private final int centerBoardX = 10000;
    private final int centerBoardY = 10000;

    private final int cardX = 300;
    private final int cardY = 210;

    private final int cardCornerX = 70;
    private final int cardCornerY = 82;


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GamePageBackup.fxml"));
        loader.setController(this);

        Parent root = loader.load();

        //Test cards
        ArrayList<String> cardStrings = new ArrayList<>();
        cardStrings.add("SEPIE_F0I__FPIA");
        cardStrings.add("NFEF_FB0");
        cardStrings.add("NAAE_AB0");
        cardStrings.add("NI_EEIB1");
        cardStrings.add("N_IKAAB0");

        ArrayList<PlacedCard> placedCards = new ArrayList<>();
        placedCards.add(new PlacedCard((PlayableCard) CardFactory.newSerialCard(cardStrings.get(0)), new Position(0, 0)));
        placedCards.add(new PlacedCard((PlayableCard) CardFactory.newSerialCard(cardStrings.get(1)), new Position(-1, -1)));
        placedCards.add(new PlacedCard((PlayableCard) CardFactory.newSerialCard(cardStrings.get(2)), new Position(1, -1)));
        placedCards.add(new PlacedCard((PlayableCard) CardFactory.newSerialCard(cardStrings.get(3)), new Position(1, 1)));
        placedCards.add(new PlacedCard((PlayableCard) CardFactory.newSerialCard(cardStrings.get(4)), new Position(-1, 1)));

        for (PlacedCard p : placedCards) {
            ImageView imageView = getImageView(p);
            ImageLoader.roundCorners(imageView);
            playerBoard.getChildren().add(imageView);
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        moveToCenter(scrollPane);
    }

    private void clearBoard(Pane playerBoard){
        playerBoard.getChildren().clear();
    }

    private void clearTemporaryCardsFromBoard(Pane playerBoard){
        //TODO: complete this
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

    private Position getPositionOnBoard(int x, int y){

        Position pos =
                new Position(centerBoardX+(x*(cardX-cardCornerX)), centerBoardY+(y*(cardY-cardCornerY)));

        return pos;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
