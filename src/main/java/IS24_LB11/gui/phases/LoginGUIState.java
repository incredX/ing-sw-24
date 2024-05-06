package IS24_LB11.gui.phases;

import IS24_LB11.gui.InputHandlerGUI;
import IS24_LB11.gui.ServerHandlerGUI;

import java.io.IOException;

public class LoginGUIState extends ClientGUIState implements PlayerStateInterface{
    String desiredUsername;
    String serverIp;
    int serverPort;

    public LoginGUIState(){
        this.serverHandler = null;
        this.inputHandlerGUI = null;
        this.username = null;
    }

    public void initialize(String username, String serverIp, int serverPort) {

        this.desiredUsername = username;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    @Override
    public void execute(){

        if (serverHandler == null) {
            try {
                this.serverHandler=new ServerHandlerGUI(this,serverIp,serverPort);

                new Thread(serverHandler).start();

                this.inputHandlerGUI=new InputHandlerGUI(serverHandler.getWriter());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        inputHandlerGUI.sendLogin(desiredUsername);
    }
}
