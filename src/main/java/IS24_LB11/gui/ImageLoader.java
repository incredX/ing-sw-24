package IS24_LB11.gui;

import javafx.scene.image.Image;

import java.io.File;

public class ImageLoader{
    public static Image getImage(String cardId) {
        // Provide the file path of the image
        String path = "src/main/resources/graphicResources/codexCards/croppedCards/";

        //TODO: try path (/main/resources....)

        if (cardId.length() >= 8 && cardId.charAt(6) == 'F') {
            switch (cardId.charAt(0)) {
                case 'N':
                    path = path + "croppedFront/normalFront/" + cardId.substring(1) + ".jpg";
                    break;
                case 'G':
                    path = path + "croppedFront/goldenFront/" + cardId.substring(1) + ".jpg";
                    break;
                case 'S':
                    path = path + "croppedFront/starterFront/" + cardId.substring(1) + ".jpg";
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
                    path = path + "croppedBack/" + cardId.substring(11) + ".jpg";
                    break;
            }
        } else {
            // goals card path
            path = path + "croppedFront/goalFront/" + cardId + ".jpg";
        }

        // Load the image
        Image image = new Image(new File(path).toURI().toString());

        return image;
    }
}

