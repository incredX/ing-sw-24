package IS24_LB11;

import IS24_LB11.cli.CliClient;
import IS24_LB11.gui.MainApp;
import IS24_LB11.network.Server;

import java.util.Scanner;

public class Main {
    private static final String  INTRO = "Welcome to Codex Naturalis! What do you want to do?\n"+
            " [1] start a Server to host the game\n"+
            " [2] start a Client (GUI)\n"+
            " [3] start a Client (TUI)\n"+
            " > ";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(INTRO);
        int choice = -1;
        while (choice < 0) {
            String command = scanner.nextLine();
            try {
                choice = Integer.parseInt(command);
                if (choice < 0 || choice > 3) throw new NumberFormatException();
            }
            catch (NumberFormatException e) {
                System.out.println("Please enter a valid number between 1 and 3");
                choice = -1;
            }
        }
        switch (choice) {
            case 1 -> new Server(54321).start();
            case 2 -> MainApp.main(new String[]{});
            case 3 -> CliClient.main(new String[]{});
        }
    }
}
