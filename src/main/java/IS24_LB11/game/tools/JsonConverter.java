package IS24_LB11.game.tools;

import IS24_LB11.game.Board;
import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.Player;
import IS24_LB11.game.components.*;
import IS24_LB11.game.utils.*;

import java.util.ArrayList;

import static IS24_LB11.game.components.CardInterface.INVALID_INPUT;

public class JsonConverter {
    /**
     * Checks if the provided object is null and throws a JsonException if it is.
     *
     * @param object the object to be checked
     * @throws JsonException if the provided object is null
     */
    private void checkNullObject(Object object) throws JsonException {
        //non mi piace com'è implementata l'exception, chiedere agli altri
        if (object == null)
            throw new JsonException("Object is null");
    }
    /**
     * Wraps the provided string with curly brackets and returns the result.
     *
     * @param string the string to be wrapped
     * @return the string wrapped with curly brackets
     * @throws JsonException if the provided string is null
     */

    private String wrapTextBrackets(String string) throws JsonException {
        checkNullObject(string);
        return "{ " + string + " }";
    }
    /**
     * Converts a CardInterface object to its JSON representation.
     *
     * @param card the CardInterface object to be converted
     * @return the JSON representation of the card
     * @throws JsonException if the provided card object is null or if the card representation is invalid
     */
    public String objectToJSON(CardInterface card) throws JsonException {
        checkNullObject(card);
        String cardString = card.asString();
        switch (cardString.charAt(0)) {
            case 'O':
                if (cardString.length() == 6)
                    return  wrapTextBrackets("\"Card\": \"" + card.asString() + "\"");
                else
                    return wrapTextBrackets("\"Card\": \"" + card.asString() + "\"");
            case 'N':
                return wrapTextBrackets("\"Card\": \"" + card.asString() + "\"");
            case 'G':
                return wrapTextBrackets("\"Card\": \"" + card.asString() + "\"");
            case 'S':
                return wrapTextBrackets("\"Card\": \"" + card.asString() + "\"");
            default:
                throw new JsonException(String.format(INVALID_INPUT, cardString));
        }
    }
    /**
     * Converts a Board object to its JSON representation.
     *
     * @param board the Board object to be converted
     * @return the JSON representation of the board
     * @throws JsonException if the provided board object is null
     */
    public String objectToJSON(Board board) throws JsonException {
        checkNullObject(board);
        String str = "\"Board\": { ";

        str += "\"placedCards\": [ ";
        for (PlacedCard placedCard: board.getPlacedCards()) {
            str += objectToJSON(placedCard.card());
            str = str.substring(0,str.length()-2) + ", X" + placedCard.position().getX() + "Y" + placedCard.position().getY() + "}, ";
        }
        str = str.substring(0,str.length()-2);

        str += " ]";
        //manca da implementare symbolCounter
        return wrapTextBrackets(str);
    }

    /**
     * Converts a Player object to its JSON representation.
     *
     * @param player the Player object to be converted
     * @return the JSON representation of the player
     * @throws JsonException if the provided player object is null
     */
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

    /**
     * Converts a JSON string representation of a card to a CardInterface object.
     *
     * @param stringInput The JSON string representing the card.
     * @return A CardInterface object representing the card described in the JSON string.
     * @throws JsonException   If there is an issue with parsing the JSON string.
     * @throws SyntaxException If there is a syntax error in the JSON string.
     */
    public CardInterface JSONToCard(String stringInput) throws JsonException, SyntaxException {
        CardFactory cardFactory = new CardFactory();
        checkNullObject(stringInput);
        if (!stringInput.contains("Card"))
            throw new JsonException(String.format(INVALID_INPUT, stringInput));

        stringInput = stringInput.substring(stringInput.indexOf("Card") + 8);
        stringInput = stringInput.substring(0,stringInput.indexOf("\""));
        switch (stringInput.charAt(0)) {
            case 'O':
                if (stringInput.length() == 6)
                    return cardFactory.newSerialCard(stringInput);
                else
                    return cardFactory.newSerialCard(stringInput);
            case 'N':
                return cardFactory.newPlayableCard(stringInput);
            case 'G':
                return cardFactory.newPlayableCard(stringInput);
            case 'S':
                return cardFactory.newPlayableCard(stringInput);
            default:
                throw new JsonException(String.format(INVALID_INPUT, stringInput));
        }
    }

    public Board JSONToBoard(String stringInput) throws JsonException, SyntaxException {
        String auxString;
        checkNullObject(stringInput);
        Board convertedBoard = new Board();
        int cnt = 3;
        while (stringInput.contains("Card")){
            auxString = stringInput.substring(stringInput.indexOf("{"),stringInput.indexOf("}")+1);
            int X = Integer.valueOf(auxString.substring(auxString.indexOf("X")+1,auxString.indexOf("Y")));
            int Y = Integer.valueOf(auxString.substring(auxString.indexOf("Y")+1,auxString.indexOf("}")));
            PlayableCard playableCard = (PlayableCard) JSONToCard(auxString.substring(auxString.indexOf("{"),auxString.indexOf(",")));
            if (playableCard.asString().startsWith("S"))
                convertedBoard.start((StarterCard) playableCard);
            else
                convertedBoard.placeCard((PlayableCard) JSONToCard(auxString.substring(auxString.indexOf("{"),auxString.indexOf(","))),new Position(X,Y));
            stringInput = stringInput.substring(stringInput.indexOf("}")+1);
        }
        return convertedBoard;
    }

    public  Player JSONToPlayer(String stringInput) throws JsonException {
        return null;
    }
    public JsonConvertable JSONToObject(String stringInput) throws JsonException, SyntaxException {
        stringInput = stringInput.substring(stringInput.indexOf("\""));
        String objectType = stringInput.substring(1, stringInput.substring(1).indexOf("\"")+1);
        switch (objectType) {
            case "Board":
                if (stringInput.contains("placedCards"))
                    return JSONToBoard(stringInput.substring(stringInput.indexOf("["),stringInput.indexOf("]")+1));
                throw new JsonException(String.format(INVALID_INPUT, stringInput));
            case "Player":
                return JSONToPlayer(stringInput);
            case "Card":
                return JSONToCard(stringInput);
            default:
                throw new JsonException(String.format(INVALID_INPUT, stringInput));
        }
    }
}
