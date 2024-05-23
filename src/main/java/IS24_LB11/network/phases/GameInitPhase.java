package IS24_LB11.network.phases;

import IS24_LB11.game.DeckException;
import IS24_LB11.game.Game;
import IS24_LB11.game.utils.SyntaxException;
import IS24_LB11.network.ClientHandler;

import java.io.FileNotFoundException;

/**
 * The GameInitPhase class handles the initialization phase of the game.
 */
public class GameInitPhase {

    /**
     * Starts the game initialization phase.
     * This method sets up a new game, configures the game with player usernames, and transitions to the setup phase.
     *
     * @param clientHandler the client handler managing the client
     * @param game the game instance to be initialized
     * @param players the number of players participating in the game
     */
    public static void startPhase(ClientHandler clientHandler, Game game, int players) {
        try {
            game = new Game(players);

            game.setupGame(clientHandler.getAllUsernames());

            clientHandler.setGame(game);

            SetupPhase.startPhase(clientHandler, clientHandler.getGame());
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (DeckException e) {
            throw new RuntimeException(e);
        }
    }

}
