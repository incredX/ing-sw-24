package IS24_LB11.cli.event.server;

import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;
import IS24_LB11.game.utils.Color;

import java.util.ArrayList;

public record ServerPlayerSetupEvent(
        PlayerSetup setup,
        ArrayList<GoalCard> publicGoals,
        ArrayList<String> playersList,
        ArrayList<Color> colorList,
        ArrayList<NormalCard> normalDeck,
        ArrayList<GoldenCard> goldenDeck
) implements ServerEvent {}
