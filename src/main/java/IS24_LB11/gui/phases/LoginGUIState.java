package IS24_LB11.gui.phases;

import IS24_LB11.gui.ClientGUI;
import IS24_LB11.gui.InputHandlerGUI;
import IS24_LB11.gui.ServerHandlerGUI;
import IS24_LB11.gui.scenesControllers.LoginSceneController;

import java.io.IOException;

/**
 * The LoginGUIState class manages the login phase of the game in the GUI.
 * It handles the initialization and execution of the login process, as well as
 * communication with the server.
 */
public class LoginGUIState extends ClientGUIState implements PlayerStateInterface {
    private String desiredUsername;
    private String serverIp;
    private int serverPort;
    private LoginSceneController loginSceneController;

    /**
     * Constructs a new LoginGUIState with a reference to the main ClientGUI.
     *
     * @param clientGUI the main ClientGUI instance.
     */
    public LoginGUIState(ClientGUI clientGUI) {
        this.serverHandler = null;
        this.inputHandlerGUI = null;
        this.username = null;
        this.loginSceneController = null;
        this.clientGUI = clientGUI;
    }

    /**
     * Initializes the LoginGUIState with the desired username, server IP, and server port.
     *
     * @param username  the desired username.
     * @param serverIp  the IP address of the server.
     * @param serverPort the port number of the server.
     */
    public void initialize(String username, String serverIp, int serverPort) {
        this.desiredUsername = username;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    /**
     * Executes the login process by connecting to the server and sending the login information.
     */
    @Override
    public void execute() {
        if (serverHandler == null) {
            try {
                this.serverHandler = new ServerHandlerGUI(this, serverIp, serverPort);
                new Thread(serverHandler).start();
                this.inputHandlerGUI = new InputHandlerGUI(serverHandler.getWriter());
            } catch (IOException e) {
                System.out.println("Connection refused");
            }
        }
        inputHandlerGUI.sendLogin(desiredUsername);
    }

    /**
     * Sets the LoginSceneController for this state.
     *
     * @param loginSceneController the LoginSceneController to be set.
     */
    public void setLoginSceneController(LoginSceneController loginSceneController) {
        this.loginSceneController = loginSceneController;
    }

    /**
     * Sends the maximum number of players to the server.
     *
     * @param numOfPlayers the maximum number of players.
     */
    public void setMaxPlayers(int numOfPlayers) {
        inputHandlerGUI.sendMaxPlayers(numOfPlayers);
    }

    /**
     * Resets the server handler and input handler.
     */
    public void resetServerHandler() {
        serverHandler = null;
        inputHandlerGUI = null;
    }
}
