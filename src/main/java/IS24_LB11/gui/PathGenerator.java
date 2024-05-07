package IS24_LB11.gui;

import IS24_LB11.game.utils.SyntaxException;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PathGenerator {
    public Image getCardPath(String cardId) {

        try {
            return new Image(pathGeneratorString(cardId), true);
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String pathGeneratorString(String cardId) throws SyntaxException {

        String path = "@graphicResources/codexCards/croppedCards/";

        if (cardId.length() >= 8 && cardId.charAt(6) == 'F') {
            switch (cardId.charAt(0)) {
                case 'N':
                    path = path + "croppedFront/normalFront/" + cardId + ".jpg";
                    break;
                case 'G':
                    path = path + "croppedFront/goldenFront/" + cardId + ".jpg";
                    break;
                case 'S':
                    path = path + "croppedFront/starterFront/" + cardId + ".jpg";
                    break;

            }
        } else if (cardId.length() >= 8 && cardId.charAt(6) == 'B') {
            switch (cardId.charAt(0)) {
                case 'N':
                    path = path + "croppedBack/N" + cardId.charAt(5) + "B.jpg";
                    break;
                case 'G':
                    path = path + "croppedBack/G" + cardId.charAt(5) + ".jpg";
                    break;
                case 'S':
                    path = path + "croppedFront/" + cardId.substring(10) + ".jpg";
                    break;
            }
        } else {
            path = path + "croppedFront/goalFront/" + cardId + ".jpg";
        }
        System.out.println(path);
        return path;
    }
}
