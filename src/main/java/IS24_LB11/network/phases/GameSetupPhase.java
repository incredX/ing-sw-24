package IS24_LB11.network.phases;

import IS24_LB11.game.DeckException;
import IS24_LB11.game.Game;
import IS24_LB11.game.utils.SyntaxException;
import IS24_LB11.network.ClientHandler;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class GameSetupPhase {

    public static void startPhase(ClientHandler clientHandler, Game game, int players) {
        try {
            game = new Game(players);

            game.setupGame(clientHandler.getAllUsernames());

            PickPhase.startPhase(clientHandler, clientHandler.getGame());
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (DeckException e) {
            throw new RuntimeException(e);
        }
    }

}