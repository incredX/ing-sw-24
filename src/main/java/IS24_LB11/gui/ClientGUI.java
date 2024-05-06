package IS24_LB11.gui;

import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.phases.LoginPhaseGUI;

import java.io.IOException;

public class ClientGUI {

    public static void main(String[] args) {
        start(args);
    }

    private static void start(String[] args) {
        MainApp.launch(args);
        ClientGUIState state = new ClientGUIState();
        // ServerHandler serverHandler = new ServerHandlerGUI(state);
        // state.setState(new LoginPhaseGUI());
        while (true){


            // STOP PLAYING
            if(false)
                break;
        }
    }

}
