package IS24_LB11.game.components;

import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.SerialObject;

import java.util.ArrayList;

public interface GoalCard extends SerialObject {
    int getPoints();
    ArrayList<Symbol> getSymbols();
}