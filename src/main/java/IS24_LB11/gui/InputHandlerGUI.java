package IS24_LB11.gui;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class InputHandlerGUI {
    private Socket socket;
    private PrintWriter writer;

    public InputHandlerGUI(PrintWriter writer) throws IOException {
        this.writer = writer;
    }

    public void sendLogin(String username){
        JsonObject message = new JsonObject();

        message.addProperty("type", "login");
        message.addProperty("username", username);

        writer.println(message.toString());
        writer.flush();
    }

    public void sendMaxPlayers(int numOfPlayers) {
        JsonObject message = new JsonObject();
        message.addProperty("type","numOfPlayers");
        message.addProperty("numOfPlayers",numOfPlayers);

        writer.println(message.toString());
        writer.flush();
    }
}
