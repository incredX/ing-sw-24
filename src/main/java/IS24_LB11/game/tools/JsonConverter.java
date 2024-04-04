package IS24_LB11.game.tools;

import IS24_LB11.game.Board;
import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.Player;
import IS24_LB11.game.components.CardInterface;
import IS24_LB11.game.utils.*;

import static IS24_LB11.game.components.CardInterface.INVALID_INPUT;

public class JsonConverter {
    /*
    Per le carte non è necessario un vero e proprio to JSON basta fare override del metodo to String.
    Per board game e player sarà necessario l'utilizzo della libreria toJSON
    */
    private void checkNullObject(Object object) throws JsonException {
        //non mi piace com'è implementata l'exception, chiedere agli altri
        if (object == null)
            throw new JsonException("Object is null");
    }

    private String wrapTextBrackets(String string) throws JsonException {
        checkNullObject(string);
        return "{ " + string + " }";
    }

    public String objectToJSON(CardInterface card) throws JsonException {
        checkNullObject(card);
        String cardString = card.asString();
        switch (cardString.charAt(0)) {
            case 'O':
                if (cardString.length() == 6)
                    return  wrapTextBrackets("\"GoalCard\": \"" + card.asString() + "\"");
                else
                    return wrapTextBrackets("\"GoalCard\": \"" + card.asString() + "\"");
            case 'N':
                return wrapTextBrackets("\"NormalCard\": \"" + card.asString() + "\"");
            case 'G':
                return wrapTextBrackets("\"GoldenCard\": \"" + card.asString() + "\"");
            case 'S':
                return wrapTextBrackets("\"StarterCard\": \"" + card.asString() + "\"");
            default:
                throw new JsonException(String.format(INVALID_INPUT, cardString));
        }
    }
    public String objectToJSON(Board board) throws JsonException {
        checkNullObject(board);
        String str = "\"Board\": { ";

        str += "\"placedCards\": [ ";
        for (PlacedCard placedCard: board.getPlacedCards()) {
            str += objectToJSON(placedCard.card());
            str = str.substring(0,str.length()-2) + ", X" + placedCard.position().getX() + "Y" + placedCard.position().getY() + "}, ";
        }
        str = str.substring(0,str.length()-2);

        str += " ], \"AvailableSpots\": [";
        for(Position position: board.getAvailableSpots()){
            str = str + "X" + position.getX() + "Y" + position.getY() + " ";
        }
        str = str.substring(0,str.length()-2);
        str += "]";

        str += ", " + board.getSymbolCounter();
        //manca da implementare symbolCounter
        return wrapTextBrackets(str);
    }
    public String objectToJSON(Player player) throws JsonException {
        checkNullObject(player);
        String str = "\"Player\": { ";
        str += "\"Status\": " + player.isStatus() + ", ";
        str += "\"Name\": " + player.getName() + ", ";
        str += "\"PersonalGoal\": " + objectToJSON(player.getPersonalGoal()) + ", ";
        str += "\"OnHandCard\": {";
        for (CardInterface card: player.getOnHandCard())
            str = str + objectToJSON(card) + ", ";
        str += "}, ";
        str += "\"Color\": " + player.getColor() + ", ";
        str += "\"Score\": " + player.getScore() + "}";
        return wrapTextBrackets(str);
    }
}
