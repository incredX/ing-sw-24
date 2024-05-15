package IS24_LB11.gui;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Chat {
    ArrayList<Text> messages= new ArrayList<>();
    public void addMessage(Text mex){
        messages.add(mex);
    }
    public ArrayList<Text> getMessages() {
        return messages;
    }
}
