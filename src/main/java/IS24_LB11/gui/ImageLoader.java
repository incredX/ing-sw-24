package IS24_LB11.gui;

import javafx.scene.SnapshotParameters;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ImageLoader{
    public static Image getImage(String cardId) {
        // Provide the file path of the image
        String path = "/graphicResources/codexCards/croppedCards/";

        if(cardId.equals("AvailableSpot")) {
            path = "/graphicResources/" + cardId + ".png";
        }
        else {
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
        }

        // Load the image
        Image image = new Image(ImageLoader.class.getResourceAsStream(path));

        return image;
    }

    public static void roundCorners(ImageView imageView){
        // set a clip to apply rounded border to the original image.
        Rectangle clip = new Rectangle(
                imageView.getFitWidth(), imageView.getFitHeight()
        );
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        imageView.setClip(clip);

        // snapshot the rounded image.
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = imageView.snapshot(parameters, null);

        // remove the rounding clip so that our effect can show through.
        imageView.setClip(null);

        // apply a shadow effect.
        imageView.setEffect(new DropShadow(20, Color.BLACK));

        // store the rounded image in the imageView.
        imageView.setImage(image);
    }
}

