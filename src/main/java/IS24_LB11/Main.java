package IS24_LB11;

import IS24_LB11.cli.ClientCLI;
import IS24_LB11.gui.ClientGUI;
import IS24_LB11.network.Server;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The Main class serves as the entry point for the application.
 * It allows starting the application in GUI mode, CLI mode, or as a server based on the provided arguments.
 */
public class Main {

    /**
     * The main method processes command-line arguments to determine the mode in which to start the application.
     * Supported modes are GUI, CLI, and Server. The port for the server can also be specified.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Integer port = 54321;
        String mainToStart = "g";

        Iterator<String> iterArgs = Arrays.stream(args).iterator();

        while (iterArgs.hasNext()) {
            String arg = iterArgs.next();
            switch (arg) {
                case "-g", "--gui" -> mainToStart = "g";
                case "-c", "--cli" -> mainToStart = "c";
                case "-s", "--server" -> mainToStart = "s";
                case "-p", "--port" -> {
                    try {
                        port = Integer.parseInt(iterArgs.next());
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid port number: " + iterArgs.next());
                        return;
                    } catch (NoSuchElementException e) {
                        System.err.println("missing port number");
                        return;
                    }
                }
            }
        }

        switch (mainToStart) {
            case "g" -> ClientGUI.main(new String[0]);
            case "c" -> ClientCLI.main(new String[0]);
            case "s" -> new Server(port).start();
        }
    }
}
