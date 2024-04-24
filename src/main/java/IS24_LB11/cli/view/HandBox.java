package IS24_LB11.cli.view;

import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.cli.utils.CliBox;
import IS24_LB11.cli.utils.Side;
import IS24_LB11.cli.utils.TerminalRectangle;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Direction;
import com.googlecode.lanterna.TextColor;

import java.awt.*;
import java.util.ArrayList;

import static IS24_LB11.cli.utils.Side.EAST;
import static IS24_LB11.cli.view.PlayableCardView.HEIGHT;
import static IS24_LB11.cli.view.PlayableCardView.WIDTH;
import static IS24_LB11.game.utils.Direction.*;

public class HandBox extends CliBox {
    private final ArrayList<PlayableCardView> cardViews;
    private int selectedCard;

    public HandBox(ArrayList<PlayableCard> hand) {
        super(WIDTH+3, HEIGHT*3+2, 0, 0, new SingleBorderStyle());
        this.cardViews = new ArrayList<>(3);
        this.selectedCard = 0;
        setMargins(0);
        int y = 0;
        for(PlayableCard card: hand) {
            switch (card) {
                case GoldenCard goldenCard -> cardViews.add(new GoldenCardView(goldenCard));
                case NormalCard normalCard -> cardViews.add(new NormalCardView(normalCard));
                default -> throw new IllegalArgumentException("Invalid card: " + card.asString());
            }
            cardViews.getLast().setPosition(0, y);
            cardViews.getLast().build();
            y += HEIGHT;
        }
    }

    @Override
    public void build() {
        drawBorders();
        drawCards();
        drawPointer();
    }

    public void buildSelectedCard(PlayableCard card) {
        PlayableCardView cardView = cardViews.get(selectedCard);
        switch (card) {
            case GoldenCard goldenCard -> cardView = new GoldenCardView(goldenCard);
            case NormalCard normalCard -> cardView = new NormalCardView(normalCard);
            default -> throw new IllegalArgumentException("Invalid card: " + card.asString());
        };
        cardView.setPosition(0, selectedCard*HEIGHT);
        cardView.build();
    }

    @Override
    protected void drawBorders() {
        super.drawBorders();
        drawCell(getCornerPosition(UP_RIGHT), borderStyle.getSeparator(EAST));
        drawCell(getCornerPosition(DOWN_RIGHT), borderStyle.getSeparator(EAST));
    }

    private void drawCards() {
        for (PlayableCardView cardView : cardViews) {
            draw(cardView);
        }
    }

    private void drawPointer() {
        PlayableCardView cardView = cardViews.get(selectedCard);
        int baseX = firstColumn()+cardView.getX();
        int baseY = firstRow()+cardView.getY()+HEIGHT/2;
        fillRow(baseY, baseX+3, "> > >", TextColor.ANSI.YELLOW);
        fillRow(baseY, baseX+WIDTH-10, "< < <", TextColor.ANSI.YELLOW);
    }

    public void setSelectedCard(int selectedCard) {
        this.selectedCard = selectedCard;
    }
}
