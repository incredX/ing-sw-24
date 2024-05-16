package IS24_LB11.gui.scenesControllers.ScoreboardController;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ScoreboardController implements Initializable {
    @FXML
    public AnchorPane pane;
    @FXML
    public ImageView yellowPion;
    @FXML
    public ImageView greenPion;
    @FXML
    public ImageView redPion;
    @FXML
    public ImageView bluePion;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        ArrayList<AnimationInstruction> scoreboardPositions = new ArrayList<>();

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



        executeAnimations(getSubarray(scoreboardPositions, 10, 62, redPion), redPion);
        executeAnimations(getSubarray(scoreboardPositions, 5, 50, bluePion), bluePion);
        executeAnimations(getSubarray(scoreboardPositions, 15, 80, yellowPion), yellowPion);
        executeAnimations(getSubarray(scoreboardPositions, 0, 30, greenPion), greenPion);

    }

    private void executeAnimations(ArrayList<AnimationInstruction> scoreboardPosition, ImageView player) {


        if (scoreboardPosition.size()==1) {
            return;
        }

        AnimationInstruction start = scoreboardPosition.getFirst();
        AnimationInstruction finish = scoreboardPosition.get(1);



        TranslateTransition translate = new TranslateTransition();
        translate.setNode(player);
        translate.setDuration(Duration.millis(500));

        translate.setByX((finish.getX())-(start.getX()));
        translate.setByY((finish.getY())-(start.getY()));


        translate.setOnFinished(event -> {
            scoreboardPosition.remove(start);

            executeAnimations(scoreboardPosition, player);
        });

        translate.play();
    }


    private ArrayList<AnimationInstruction> getSubarray(ArrayList<AnimationInstruction> scoreboardPosition, int startingPoints, int finalPoints, ImageView player) {

        ArrayList<AnimationInstruction> updatingScoreboard = new ArrayList<>();


        for (int i=startingPoints ; i <= finalPoints ; i++) {
            updatingScoreboard.add(scoreboardPosition.get(i%30));
        }

        switch (player.getId()) {
            case ("bluePion"):
                player.setX((scoreboardPosition.get(startingPoints).getX())-15);
                player.setY((scoreboardPosition.get(startingPoints).getY())-18);
                break;
            case ("greenPion"):
                player.setX((scoreboardPosition.get(startingPoints).getX())+10);
                player.setY((scoreboardPosition.get(startingPoints).getY())-18);
                break;
            case ("redPion"):
                player.setX((scoreboardPosition.get(startingPoints).getX())-15);
                player.setY((scoreboardPosition.get(startingPoints).getY())+7);
                break;
            case ("yellowPion"):
                player.setX((scoreboardPosition.get(startingPoints).getX())+10);
                player.setY((scoreboardPosition.get(startingPoints).getY())+7);
                break;
        }


        return updatingScoreboard;
    }

}
