package IS24_LB11.game;

import IS24_LB11.game.components.CardInterface;
import IS24_LB11.game.components.NormalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.SyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class Deck {
    private ArrayList<PlayableCard> cards = new ArrayList<PlayableCard>();
    private int index;

    public Deck () {
        cards = new ArrayList<PlayableCard> ();
        index = 0;
    }
    public void shuffle () {
        Collections.shuffle(cards);
    }

    /**
     *
     * @param cardIndex represent wich card draw: 0/1 = 1st/2nd visible card, 2 = covered card
     * @return an optional with the card if able, otherwise return an empty one
     */
    public Optional<PlayableCard> drawCard(int cardIndex) {
        // return empty if the wanted card doesn't exist into the deck or cardIndex isn't between 0 and 2 (included)
        if (cards.size() <= cardIndex || cardIndex < 0 || cardIndex > 2) {
            return Optional.empty();
        }
        PlayableCard card = cards.get(index+cardIndex);
        index++;
        return Optional.of(card);
    }
    /* example: (letters represent a generic card in the collection)
     * cards:   [A, B, C, D, E, F, G] with A: 1st visible card, B: 2nd visible card, C: covered card
     * index (0) ^
     *
     * we want to draw the 1st visible card:
     * cardIndex = 0  => this function will return cards[0 + 0] = A and update index to index+1 = 1.
     *
     * At the end of the function the internal state of this class will be:
     * cards:   [A, B, C, D, E, F, G] with B: 1st visible card, C: 2nd visible card, D: covered card
     * index (1)    ^
     */

}