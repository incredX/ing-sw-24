package IS24_LB11.cli.view;

import IS24_LB11.cli.utils.CliBox;
import IS24_LB11.game.components.*;

public class CardViewFactory {
    public static PlayableCardView newPlayableCardView(PlayableCard card) {
        return switch(card) {
            case StarterCard starterCard -> new StarterCardView(starterCard);
            case GoldenCard goldenCard -> new GoldenCardView(goldenCard);
            case NormalCard normalCard -> new NormalCardView(normalCard);
            default -> null;
        };
    }

    public static CliBox newGoalCardView(GoalCard card) {
        return switch (card) {
            case GoalPattern goalPattern -> new GoalPatternView(goalPattern);
            case GoalSymbol goalSymbol -> new GoalSymbolView(goalSymbol);
            default -> null;
        };
    }
}
