package IS24_LB11.game;

import IS24_LB11.game.components.CardFactory;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.utils.SyntaxException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class GameTest {
    @Test
    @DisplayName("Simulating game setup")
    public void gameStart() throws SyntaxException, FileNotFoundException, DeckException {
        JsonConverter jsonConverter = new JsonConverter();
        int playersNumber = 4;
        Game game = new Game(playersNumber);
        //Receiving players name
        ArrayList<String> playerNames = new ArrayList<>(playersNumber);
        for (int i = 0; i < 4; i++)
            playerNames.add("Player " + (i+1));
        game.setupGame(playerNames);
        //Receiving players Goal
        ArrayList<GoalCard> goalCardsChoosen = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            goalCardsChoosen.add(game.getPlayers().get(i).getSetup().getGoals()[i%2]);
        game.chooseGoalPhase(goalCardsChoosen);
        //TODO last print

    }
    @Test
    @DisplayName("Simulating normal turn")
    public void normalTurnSample(){

    }
}
