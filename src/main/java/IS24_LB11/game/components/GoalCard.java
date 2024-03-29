package IS24_LB11.game.components;

import IS24_LB11.game.symbol.Symbol;

import java.util.ArrayList;

public interface GoalCard extends CardInterface {
    int getPoints();
    ArrayList<Symbol> getSymbols();
}