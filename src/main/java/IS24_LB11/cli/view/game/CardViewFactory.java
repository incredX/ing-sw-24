package IS24_LB11.cli.view.game;

import IS24_LB11.cli.utils.TerminalBox;
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

    public static TerminalBox newGoalCardView(GoalCard card) {
        return switch (card) {
            case GoalPattern goalPattern -> new GoalPatternView(goalPattern);
            case GoalSymbol goalSymbol -> new GoalView(goalSymbol);
            default -> null;
        };
    }
}
