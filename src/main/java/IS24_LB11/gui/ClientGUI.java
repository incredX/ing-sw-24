package IS24_LB11.gui;

import IS24_LB11.gui.phases.ClientGUIState;

public class ClientGUI {

    public static void main(String[] args) {
        start(args);
    }

    private static void start(String[] args) {
        MainApp mainApp = new MainApp();
        mainApp.launch(args);
        ClientGUIState state = new ClientGUIState();
        // state.setState(new LoginPhaseGUI());
        while (true){


            // STOP PLAYING
            if(false)
                break;
        }
    }

}
