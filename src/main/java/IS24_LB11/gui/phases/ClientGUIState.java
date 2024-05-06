package IS24_LB11.gui.phases;

import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.gui.InputHandlerGUI;
import IS24_LB11.gui.ServerHandlerGUI;

import java.io.IOException;

public class ClientGUIState {
    private ClientGUIState actualState;
    protected String username;
    protected ServerHandlerGUI serverHandler;
    protected InputHandlerGUI inputHandlerGUI;

    public ClientGUIState(){
        this.actualState= null;
        this.username = "";
        this.serverHandler=serverHandler;
        try {
            this.inputHandlerGUI= new InputHandlerGUI(serverHandler.getWriter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setState(ClientGUIState nextState){
        this.actualState=nextState;
    }

    public void execute(){

    }

    public void setUsername(String username) {
        this.username = username;
    }
}