package IS24_LB11.game.tools;

import IS24_LB11.game.components.*;
import IS24_LB11.game.utils.*;

import static IS24_LB11.game.utils.SerialObject.INVALID_INPUT;

public class JsonConverter {
    /*
    Per le carte non è necessario un vero e proprio to JSON basta fare override del metodo to String.
    Per board game e player sarà necessario l'utilizzo della libreria toJSON
    */

    public String objectToJSON(CardInterface card) throws JsonStringException {
        String cardString = card.asString();
        System.out.println(cardString);
        switch (cardString.charAt(0)){
            case 'O':
                if (cardString.length()==6)
                    return "{ \"GoalCard\" \"" + card.asString() + "\" }";
                else
                    return "{ \"GoalCard\" \"" + card.asString() + "\" }";
                break;
            case 'G':

                break;
            case 'S':

                break;
//        switch (cardString.length()) {
//            case 5:
//                return "{ \"GoalCard\" \"" + card.asString() + "\" }";
//            case 7:
//                if (cardString.charAt(0)=='O')
//                    return "{ \"GoalCard\" \"" + card.asString() + "\" }";
//                else
//                    return "{ \"NormalCard\" \"" + card.asString() + "\" }";
//            case 12:
//                return "{ \"GoldenCard\" \"" + card.asString() + "\" }";
//            case 13:
//                return "{ \"StarterCard\" \"" + card.asString() + "\" }";
//            default:
//                throw new JsonStringException(String.format(INVALID_INPUT, cardString));
        }
    }
}
