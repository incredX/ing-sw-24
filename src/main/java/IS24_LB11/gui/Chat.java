package IS24_LB11.gui;

import javafx.fxml.FXML;

public class Chat {
    String messages="";
    public void addMessage(String username, String mex){
        messages.concat(username + ": " + messages + "\n");
    }
    public String getMessages() {
        return messages;
    }
}
