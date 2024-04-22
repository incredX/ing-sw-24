package IS24_LB11.network.phases;

import IS24_LB11.game.Game;
import IS24_LB11.game.utils.SyntaxException;

import java.io.FileNotFoundException;

public class GameSetupPhase {

    public static void startPhase(Game game, int players) {
        try {
            game = new Game(players);
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
