package IS24_LB11.cli.automation;

import IS24_LB11.cli.CliClient;
import IS24_LB11.cli.Debugger;
import IS24_LB11.network.Server;

public class Spawner {
    public static void main(String[] args) {
        new Thread(() -> {
            Server server = new Server(54321);
            server.start();
        }).start();

        Thread client1 = new Thread(() -> {
            CliClient.main(new String[] { "",
                    "{\"type\":\"login\",\"username\":\"wasd\"}",
                    "{\"type\":\"numOfPlayers\",\"numOfPlayers\":2}"
            });
        });

        Thread client2 = new Thread(() -> {
            CliClient.main(new String[] { "",
                    "{\"type\":\"login\",\"username\":\"loremIpsum\"}"
            });
        });

        try {
            Thread.sleep(1000);
            client1.start();
            Thread.sleep(2000);
            client2.start();

            client1.join();
            client2.join();
        } catch (InterruptedException e) {
            Debugger.print(e);
        }

    }
}
