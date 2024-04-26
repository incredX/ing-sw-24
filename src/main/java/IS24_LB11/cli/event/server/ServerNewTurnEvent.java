package IS24_LB11.cli.event.server;

import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;

import java.util.ArrayList;

public record ServerNewTurnEvent(
        String player,
        ArrayList<NormalCard> normalDeck,
        ArrayList<GoldenCard> goldenDeck,
        ArrayList<Integer> scores)
        implements ServerEvent {}
