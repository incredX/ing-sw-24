package IS24_LB11.gui;

import javafx.scene.image.Image;

import java.io.File;

public class ImageLoader{
    public static Image getImage(String cardId) {
        // Provide the file path of the image
        String path = "src/main/resources/graphicResources/codexCards/croppedCards/";

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

        // Load the image
        Image image = new Image(new File(path).toURI().toString());


        return image;
    }
}

