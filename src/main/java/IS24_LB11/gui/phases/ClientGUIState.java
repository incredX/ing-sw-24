package IS24_LB11.gui.phases;

import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.gui.InputHandlerGUI;
import IS24_LB11.gui.ServerHandlerGUI;

import java.io.IOException;

public abstract class ClientGUIState {
    private ClientGUIState actualState;
    protected String username;
    protected ServerHandlerGUI serverHandler;
    protected InputHandlerGUI inputHandlerGUI;

    public ClientGUIState() {
        this.actualState = null;
        this.username = "";
    }

    public void setState(ClientGUIState nextState) {
        this.actualState = nextState;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
