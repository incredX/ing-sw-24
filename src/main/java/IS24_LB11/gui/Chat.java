package IS24_LB11.gui;

import javafx.scene.text.Text;
import java.util.ArrayList;

/**
 * The Chat class is used to manage and store chat messages for the GUI.
 * It maintains an ArrayList of Text objects representing the messages.
 */
public class Chat {

    // An ArrayList to store the chat messages
    private ArrayList<Text> messages = new ArrayList<>();

    /**
     * Adds a message to the chat.
     *
     * @param mex the message to be added as a Text object.
     */
    public void addMessage(Text mex) {
        messages.add(mex);
    }

    /**
     * Retrieves the list of messages in the chat.
     *
     * @return an ArrayList of Text objects representing the chat messages.
     */
    public ArrayList<Text> getMessages() {
        return messages;
    }
}
