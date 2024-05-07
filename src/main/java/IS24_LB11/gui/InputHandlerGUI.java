package IS24_LB11.gui;

import IS24_LB11.gui.phases.ClientGUIState;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;

public class InputHandlerGUI {
    private Socket socket;
    private PrintWriter writer;

    public InputHandlerGUI(PrintWriter writer) throws IOException {
        this.writer = writer;
    }

    public void sendLogin(String username){
        JsonObject message = new JsonObject();
        message.addProperty("type", "setUsername");
        message.addProperty("username", username);
        writer.println(message.toString());
        writer.flush();
    }
}
