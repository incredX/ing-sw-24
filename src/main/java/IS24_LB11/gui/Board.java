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

/**
 * The Board class represents the graphical board of the game.
 * It extends the JavaFX Application class and handles the display and placement of game cards on the board.
 */
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

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * @param stage the primary stage for this application
     * @throws Exception if an error occurs during loading the FXML resource
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLScenes/GamePage.fxml"));
        loader.setController(this);

        Parent root = loader.load();

        // Test cards
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

    /**
     * Clears the player board by removing all children nodes.
     *
     * @param playerBoard the player board to clear
     */
    private void clearBoard(Pane playerBoard) {
        playerBoard.getChildren().clear();
    }

    /**
     * Moves the view to the center of the board.
     *
     * @param scrollPane the scroll pane to center
     */
    private void moveToCenter(ScrollPane scrollPane) {
        Platform.runLater(() -> {
            scrollPane.setHvalue((double) (centerBoardX + (cardX / 2)) / playerBoard.getWidth());
            scrollPane.setVvalue((double) (centerBoardY + (cardY / 2)) / playerBoard.getHeight());
        });
    }

    /**
     * Creates an ImageView for the given placed card.
     *
     * @param placedCard the placed card to create an ImageView for
     * @return the ImageView representing the placed card
     */
    private ImageView getImageView(PlacedCard placedCard) {
        Image image = ImageLoader.getImage(placedCard.card().asString());
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(cardX);
        imageView.setFitHeight(cardY);

        Position positionOnBoard = getPositionOnBoard(placedCard.position().getX(), placedCard.position().getY());

        imageView.setLayoutX(positionOnBoard.getX()); // X coordinate
        imageView.setLayoutY(positionOnBoard.getY()); // Y coordinate

        return imageView;
    }

    /**
     * Calculates the position on the board for the given coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the position on the board
     */
    private Position getPositionOnBoard(int x, int y) {
        return new Position(centerBoardX + (x * (cardX - cardCornerX)), centerBoardY + (y * (cardY - cardCornerY)));
    }

    /**
     * The main method to launch the JavaFX application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
