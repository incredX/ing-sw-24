package IS24_LB11.gui.phases;

import IS24_LB11.gui.InputHandlerGUI;
import IS24_LB11.gui.ServerHandlerGUI;

import java.io.IOException;

public class LoginPhaseGUI extends ClientGUIState{
    String username;

    public LoginPhaseGUI(InputHandlerGUI inputHandlerGUI){

    }

    public void execute(String username, int port, String ip){
        try {
            this.serverHandler=new ServerHandlerGUI(this,ip,port);
            this.inputHandlerGUI=new InputHandlerGUI(serverHandler.getWriter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        inputHandlerGUI.sendLogin(username);
    }
}
