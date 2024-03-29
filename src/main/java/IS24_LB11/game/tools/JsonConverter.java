package IS24_LB11.game.tools;

import IS24_LB11.game.components.CardInterface;
import IS24_LB11.game.utils.*;

import static IS24_LB11.game.components.CardInterface.INVALID_INPUT;

public class JsonConverter {
    /*
    Per le carte non è necessario un vero e proprio to JSON basta fare override del metodo to String.
    Per board game e player sarà necessario l'utilizzo della libreria toJSON
    */

    public String objectToJSON(CardInterface card) throws JsonStringException {
        String cardString = card.asString();
        switch (cardString.charAt(0)) {
            case 'O':
                if (cardString.length() == 6)
                    return "{ \"GoalCard\": \"" + card.asString() + "\" }";
                else
                    return "{ \"GoalCard\": \"" + card.asString() + "\" }";
            case 'N':
                return "{ \"NormalCard\": \"" + card.asString() + "\" }";
            case 'G':
                return "{ \"GoldenCard\": \"" + card.asString() + "\" }";
            case 'S':
                return "{ \"StarterCard\": \"" + card.asString() + "\" }";
            default:
                throw new JsonStringException(String.format(INVALID_INPUT, cardString));
        }
    }
}
