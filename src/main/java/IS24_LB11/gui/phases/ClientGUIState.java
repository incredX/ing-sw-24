package IS24_LB11.gui.phases;

import IS24_LB11.gui.InputHandlerGUI;
import IS24_LB11.gui.ServerHandlerGUI;

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

    public String getUsername() {
        return username;
    }

    public ServerHandlerGUI getServerHandler() {
        return serverHandler;
    }

    public void shutdown() {
        if (serverHandler == null)
            return;
        serverHandler.shutdown();
    }
}