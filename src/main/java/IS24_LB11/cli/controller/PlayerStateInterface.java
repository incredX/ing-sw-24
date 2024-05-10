package IS24_LB11.cli.controller;

import IS24_LB11.cli.Scoreboard;
import IS24_LB11.cli.Table;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;
import IS24_LB11.game.components.PlayableCard;

import java.util.ArrayList;

public interface PlayerStateInterface {
    Table getTable();
    Scoreboard getScoreboard();
    ArrayList<GoalCard> getGoals();
    ArrayList<NormalCard> getNormalDeck();
    ArrayList<GoldenCard> getGoldenDeck();
    ArrayList<PlayableCard> getPlayerHand();
}
