package IS24_LB11.gui.phases;

import IS24_LB11.gui.Chat;
import IS24_LB11.gui.ClientGUI;
import IS24_LB11.gui.InputHandlerGUI;
import IS24_LB11.gui.ServerHandlerGUI;

public abstract class ClientGUIState {
    protected ClientGUI clientGUI;
    private ClientGUIState actualState;

    protected String username;
    protected ServerHandlerGUI serverHandler;
    protected InputHandlerGUI inputHandlerGUI;
    protected Boolean isFinalTurn = false;

    protected Chat personalChat;
    public ClientGUIState() {
        this.actualState = null;
        this.username = "";
        personalChat = new Chat();
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

    public ClientGUI getClientGUI() {
        return clientGUI;
    }

    public Boolean isFinalTurn(){
        return isFinalTurn;
    }

    public void setIsFinalTurn(Boolean isFinalTurn){
        this.isFinalTurn = isFinalTurn;
    }

    public InputHandlerGUI getInputHandlerGUI() {
        return inputHandlerGUI;
    }
    public void sendMessage(String to, String from,String mex){
        inputHandlerGUI.sendMessage(to,from,mex);
    }
    public void sendToAll (String from, String mex){
        inputHandlerGUI.sendToAllMessage(from, mex);
    }
}