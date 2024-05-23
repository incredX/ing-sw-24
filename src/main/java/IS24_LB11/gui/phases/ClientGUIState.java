package IS24_LB11.gui.phases;

import IS24_LB11.gui.Chat;
import IS24_LB11.gui.ClientGUI;
import IS24_LB11.gui.InputHandlerGUI;
import IS24_LB11.gui.ServerHandlerGUI;

/**
 * The abstract class ClientGUIState serves as the base state for the ClientGUI application.
 * It manages the state transitions and handles common functionality required across different states.
 */
public abstract class ClientGUIState {
    protected ClientGUI clientGUI;
    private ClientGUIState actualState;

    protected String username;
    protected ServerHandlerGUI serverHandler;
    protected InputHandlerGUI inputHandlerGUI;
    protected Boolean isFinalTurn = false;

    protected Chat personalChat;

    /**
     * Constructs a new ClientGUIState with default values.
     */
    public ClientGUIState() {
        this.actualState = null;
        this.username = "";
        personalChat = new Chat();
    }

    /**
     * Shuts down the server handler if it is initialized.
     */
    public void shutdown() {
        if (serverHandler == null)
            return;
        serverHandler.shutdown();
    }

    /**
     * Sends a message from one user to another.
     *
     * @param to   the recipient of the message
     * @param from the sender of the message
     * @param mex  the message content
     */
    public void sendMessage(String to, String from, String mex) {
        inputHandlerGUI.sendMessage(to, from, mex);
    }

    /**
     * Sends a message to all users.
     *
     * @param from the sender of the message
     * @param mex  the message content
     */
    public void sendToAll(String from, String mex) {
        inputHandlerGUI.sendToAllMessage(from, mex);
    }

    /**
     * Gets the ClientGUI instance associated with this state.
     *
     * @return the ClientGUI instance
     */
    public ClientGUI getClientGUI() {
        return clientGUI;
    }

    /**
     * Gets the username associated with this state.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the InputHandlerGUI instance associated with this state.
     *
     * @return the InputHandlerGUI instance
     */
    public InputHandlerGUI getInputHandlerGUI() {
        return inputHandlerGUI;
    }

    /**
     * Gets the ServerHandlerGUI instance associated with this state.
     *
     * @return the ServerHandlerGUI instance
     */
    public ServerHandlerGUI getServerHandler() {
        return serverHandler;
    }

    /**
     * Checks if it is the final turn.
     *
     * @return true if it is the final turn, false otherwise
     */
    public Boolean isFinalTurn() {
        return isFinalTurn;
    }

    /**
     * Sets the final turn status.
     *
     * @param isFinalTurn true to mark as final turn, false otherwise
     */
    public void setIsFinalTurn(Boolean isFinalTurn) {
        this.isFinalTurn = isFinalTurn;
    }

    /**
     * Sets the next state for the ClientGUI.
     *
     * @param nextState the next state to transition to
     */
    public void setState(ClientGUIState nextState) {
        this.actualState = nextState;
    }

    /**
     * Sets the username associated with this state.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
