package IS24_LB11.game;

import IS24_LB11.game.components.CardInterface;
import IS24_LB11.game.components.PlayableCard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

public class Deck {
    private ArrayList<CardInterface> cards = new ArrayList<CardInterface>();

    public Deck (ArrayList<CardInterface> cards) {
        this.cards = cards;
    }

    /**
     *
     * shuffle randomly sorts the cards of the deck
     */
    public void shuffle () {
        Collections.shuffle(cards);
    }

    /**
     *
     * @param cardIndex represent wich card draw: 1/2 = 1st/2nd visible card, 3 = covered card
     * @return the selected card removing it from the deck
     * @throw the DeckException when there aren't any cards left or the index is not allowed
     */
    public CardInterface drawCard(int cardIndex) throws DeckException {

        if (cards.isEmpty()) {
            throw new DeckException("The deck is empty. \n");
        }

        // throw exception if the wanted card doesn't exist into the deck or cardIndex isn't between 1 and 3 (included)
        if (cards.size() < cardIndex || cardIndex < 1 || cardIndex > 3) {
            throw new DeckException("Index out of bound. \n");
        }

        CardInterface card = cards.remove(cards.size()-cardIndex);
        return card;
    }


    public CardInterface drawCard() throws DeckException {

        if (cards.isEmpty()) {
            throw new DeckException("The deck is empty. \n");
        }
        CardInterface card = cards.removeLast();
        return card;
    }

    /**
     * This methods show the cards in the position 1,2,3
     * @param cardIndex represent wich card draw: 1/2 = 1st/2nd visible card, 3 = covered card
     * @return the selected card without removing it
     * @throw the DeckException when there aren't any cards left or the index is not allowed
     */

    public CardInterface showCard(int cardIndex) throws DeckException{
        if (cards.isEmpty()) {
            throw new DeckException("The deck is empty. \n");
        }

        if (cards.size() <= cardIndex || cardIndex < 1 || cardIndex > 3) {
            throw new DeckException("Index out of bound. \n");
        }

        CardInterface card = cards.get( cards.size()-cardIndex );
        return card;
    };

    /**
     * This methods will be used to understand if the deck is empty, in order to start the last turn of the game
     * @return True if the deck is empty
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public ArrayList<CardInterface> getCards() {
        return cards;
    }

    public int size() {
        return cards.size();
    }

    public boolean contains (CardInterface card) {
        return cards.contains(card);
    }

    public void reverse() {
        Collections.reverse(cards);
    }
}
