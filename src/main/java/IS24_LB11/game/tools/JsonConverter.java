package IS24_LB11.game.tools;

import IS24_LB11.game.*;
import IS24_LB11.game.components.*;
import IS24_LB11.game.utils.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static IS24_LB11.game.tools.JsonException.INVALID_INPUT;
import static IS24_LB11.game.tools.JsonException.PLACEDCARDS_NOT_FOUND;

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
        return "{" + string + "}";
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
        Character c = cardString.charAt(0);
        switch (c){
            case 'O':
                return wrapTextBrackets("\"goalCard\":\"" + card.asString() + "\"");
            case 'G':
                return wrapTextBrackets("\"goldenCard\":\"" + card.asString() + "\"");
            case 'N':
                return wrapTextBrackets("\"normalCard\":\"" + card.asString() + "\"");
            case 'S':
                return wrapTextBrackets("\"starterCard\":\"" + card.asString() + "\"");
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
        String str = "\"Board\":{";
        str += "\"placedCards\":[";
        for (PlacedCard placedCard : board.getPlacedCards()) {
            str = str + objectToJSON(placedCard) + ",";
        }
        str = str.substring(0, str.length() - 1);
        str += "]}";
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
        String str = "\"Player\":{";
        str = str + "\"playerName\":\"" + player.name() + "\",";
        str = str + "\"Color\":\"" + player.getColor() + "\",";
        str = str + "\"Hand\":[";
        for (PlayableCard playableCard : player.getHand())
            str = str + objectToJSON(playableCard) + ",";
        str = str.substring(0, str.length() - 1) + "],";
        str = str + "\"PersonalGoal\":" + objectToJSON(player.getPersonalGoal()) + ",";
        str = str + "\"Score\":\"" + player.getScore() + "\",";
        str = str + objectToJSON(player.getSetup()).substring(1);
        str = str.substring(0, str.length() - 1) + ",";
        str = str + objectToJSON(player.getBoard()).substring(1);
        return wrapTextBrackets(str);
    }

    public String objectToJSON(PlayerSetup playerSetup) throws JsonException {
        String str = "\"PlayerSetup\":{";
        str = str + "\"StarterCard\":" + objectToJSON(playerSetup.getStarterCard()) + ",";
        str = str + "\"Goals\":[" + objectToJSON(playerSetup.getGoals()[0]) + "," + objectToJSON(playerSetup.getGoals()[1]) + "],";
        str = str + "\"Color\":\"" + playerSetup.getColor() + "\",";
        str = str + "\"Hand\":[";
        for (PlayableCard playableCard : playerSetup.hand())
            str = str + objectToJSON(playableCard) + ",";
        str = str.substring(0, str.length() - 1) + "],";
        str = str + "\"chosenGoalIndex\":\"" + playerSetup.getChosenGoalIndex() + "\"}";
        return wrapTextBrackets(str);
    }

    public String objectToJSON(PlacedCard placedCard) throws JsonException {
        String str ="\"PlacedCard\":";
        str = str + objectToJSON(placedCard.card()) ;
        str = str.substring(0,str.length()-1)  + ",\"Position\":\"";
        str = str + "X" + placedCard.position().getX() + "Y" + placedCard.position().getY() + "\"}";
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
    private CardInterface JSONToCard(String stringInput) throws JsonException, SyntaxException {
        CardFactory cardFactory = new CardFactory();
        checkNullObject(stringInput);
        if (!stringInput.contains("Card"))
            throw new JsonException(String.format(INVALID_INPUT, stringInput));
        stringInput = stringInput.substring(stringInput.indexOf("Card") + 7);
        stringInput = stringInput.substring(0, stringInput.indexOf("\""));
        switch (stringInput.charAt(0)) {
            case 'O':
                if (stringInput.length() == 6)
                    return cardFactory.newSerialCard(stringInput);
                else
                    return cardFactory.newSerialCard(stringInput);
            case 'N':
                return cardFactory.newSerialCard(stringInput);
            case 'G':
                return cardFactory.newSerialCard(stringInput);
            case 'S':
                return cardFactory.newSerialCard(stringInput);
            default:
                throw new JsonException(String.format(INVALID_INPUT, stringInput));
        }
    }

    /**
     * Converts a JSON representation of a board into a Board object.
     *
     * @param stringInput the JSON string representing the board.
     * @return the Board object created from the JSON input.
     * @throws JsonException   if there is an issue parsing the JSON input.
     * @throws SyntaxException if there is a syntax error in the JSON input.
     */
    public Board JSONToBoard(String stringInput) throws JsonException, SyntaxException {
        String auxString;
        checkNullObject(stringInput);
        Board convertedBoard = new Board();
        while (stringInput.contains("PlacedCard")) {
            auxString = stringInput.substring(stringInput.indexOf("{"), stringInput.indexOf("}") + 1);
            System.out.println(auxString);
            PlacedCard placedCard = (PlacedCard) JSONToObject(auxString);
            if (placedCard.card().asString().startsWith("S"))
                convertedBoard.start((StarterCard) placedCard.card());
            else
                convertedBoard.placeCard((PlayableCard) placedCard.card(),placedCard.position());
            System.out.println(stringInput);
            stringInput = stringInput.substring(stringInput.indexOf("}") + 3);
        }
        return convertedBoard;
    }

    private JsonConvertable JSONToPlayer(String stringInput) throws JsonException, SyntaxException {
        System.out.println(stringInput);
        String auxString = stringInput.substring(stringInput.indexOf("playerName") + 7);
        System.out.println(auxString);
        String name = auxString.substring(0, auxString.indexOf("\""));
        System.out.println(name);
        Character character = auxString.charAt(auxString.indexOf("Color") + 8);
        Color color = Color.fromChar(character);
        System.out.println(color);
        auxString = auxString.substring(auxString.indexOf("Hand") + 7, auxString.indexOf("]"));
        ArrayList<PlayableCard> hand = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            System.out.println(auxString);
            hand.add((PlayableCard) JSONToObject(auxString.substring(auxString.indexOf("{"), auxString.indexOf("}"))));
            auxString = (i != 2) ? auxString.substring(auxString.indexOf("}") + 2) : auxString;
        }
        for (PlayableCard playableCard : hand)
            System.out.println(playableCard.asString());
        auxString = stringInput.substring(stringInput.indexOf("Score") + 8);
        auxString = auxString.substring(0, auxString.indexOf("\""));
        int score = Integer.valueOf(auxString);

        PlayerSetup playerSetup = (PlayerSetup) JSONToSetupPlayer(stringInput.substring(stringInput.indexOf("PlayerSetup"), stringInput.length() - 2));
        System.out.println("{" + stringInput.substring(stringInput.indexOf("\"Board"),stringInput.length()-2));
        Board board = (Board) JSONToObject("{" + stringInput.substring(stringInput.indexOf("\"Board"),stringInput.length()-1));
        System.out.println(objectToJSON(board));
        Player playerConverted = new Player(name,playerSetup);
        playerConverted.applySetup();
        playerConverted.setBoard(board);
        return playerConverted;
    }

    private PlayerSetup JSONToSetupPlayer(String stringInput) throws JsonException, SyntaxException {
        String auxString = stringInput.substring(stringInput.indexOf("StarterCard") + 13);
        StarterCard starterCard = (StarterCard) JSONToCard(auxString.substring(0, auxString.indexOf(",")));
        auxString = stringInput.substring(stringInput.indexOf("Goals") + 8);
        auxString = auxString.substring(0, auxString.indexOf("]"));
        GoalCard[] goals = new GoalCard[2];
        for (int i = 0; i < 2; i++) {
            System.out.println(auxString);
            goals[i] = ((GoalCard) JSONToObject(auxString.substring(auxString.indexOf("{"), auxString.indexOf("}"))));
            auxString = (i != 1) ? auxString.substring(auxString.indexOf("}") + 2) : auxString;
        }
        System.out.println(auxString);
        auxString = stringInput.substring(stringInput.indexOf("Color") + 8);
        Color color = Color.fromChar(auxString.charAt(0));
        auxString = stringInput.substring(stringInput.indexOf("Hand") + 7);
        auxString = auxString.substring(0, auxString.indexOf("]"));
        ArrayList<PlayableCard> hand = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            hand.add((PlayableCard) JSONToObject(auxString.substring(auxString.indexOf("{"), auxString.indexOf("}"))));
            auxString = (i != 2) ? auxString.substring(auxString.indexOf("}") + 2) : auxString;
        }
        auxString = stringInput.substring(stringInput.indexOf("chosenGoalIndex") + 18);
        int chosenGoalIndex = Integer.valueOf(auxString.substring(0, auxString.indexOf("\"")));
        PlayerSetup playerSetupConverted = new PlayerSetup(starterCard,goals,hand,color);
        playerSetupConverted.selectGoal(goals[chosenGoalIndex]);
        return playerSetupConverted;
    }

    private JsonConvertable JSONToPlacedCard(String stringInput) throws JsonException, SyntaxException {
        checkNullObject(stringInput);
        String auxString = stringInput.substring(stringInput.indexOf("PlacedCard") + 12);
        CardInterface card = JSONToCard(auxString.substring(0,auxString.indexOf(",")));
        stringInput = stringInput.substring(stringInput.indexOf("X"));
        int X =Integer.valueOf(stringInput.substring(1,stringInput.indexOf("Y")));
        stringInput=stringInput.substring(stringInput.indexOf("Y")+1,stringInput.indexOf("\""));
        int Y =Integer.valueOf(stringInput);
        return (JsonConvertable) new PlacedCard((PlayableCard) card,new Position(X,Y));
    }


    /**
     * Converts a JSON representation of an object into the corresponding JsonConvertable object.
     *
     * @param stringInput the JSON string representing the object.
     * @return the JsonConvertable object created from the JSON input.
     * @throws JsonException   if there is an issue parsing the JSON input.
     * @throws SyntaxException if there is a syntax error in the JSON input.
     */
    public JsonConvertable JSONToObject(String stringInput) throws JsonException, SyntaxException {
        stringInput = stringInput.substring(stringInput.indexOf("\""));
        String objectType = stringInput.substring(1, stringInput.substring(1).indexOf("\"") + 1);
        switch (objectType) {
            case "Board":
                if (stringInput.contains("placedCards"))
                    return JSONToBoard(stringInput.substring(stringInput.indexOf("["), stringInput.indexOf("]") + 1));
                throw new JsonException(String.format(PLACEDCARDS_NOT_FOUND, stringInput));
            case "Player":
                return JSONToPlayer(stringInput);
            case "PlayerSetup":
                return JSONToSetupPlayer(stringInput);
            case "PlacedCard":
                return JSONToPlacedCard(stringInput);
            default:
                if (objectType.contains("Card"))
                    return JSONToCard(stringInput);
                throw new JsonException(String.format(INVALID_INPUT, stringInput));
        }
    }

    /**
     * Converts a JSON representation of a deck into a Deck object for a specific character.
     *
     * @param text      the JSON string representing the deck.
     * @param character the character associated with the deck.
     * @return the Deck object created from the JSON input.
     * @throws SyntaxException if there is a syntax error in the JSON input.
     */
    public Deck JSONToDeck(Character character) throws SyntaxException, FileNotFoundException {
        Scanner scFile = new Scanner(new File("resources/Cards.json"));
        String text = "";
        while (scFile.hasNextLine())
            text = text.concat(scFile.nextLine());
        ArrayList<CardInterface> deckCards = new ArrayList<>();
        CardFactory cardFactory = new CardFactory();
        Scanner sc = new Scanner(text);
        while (sc.hasNext()) {
            String word = sc.next();
            word = word.endsWith(",") ? word.substring(0, word.length() - 1) : word;
            if (word.startsWith("\"" + character) && word.length() > 5) {
                deckCards.add(cardFactory.newSerialCard(word.substring(1, word.length() - 1)));
            }
        }
        return new Deck(deckCards);
    }
}
