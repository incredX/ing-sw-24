package IS24_LB11.cli;

import IS24_LB11.cli.utils.Side;
import IS24_LB11.cli.view.*;
import IS24_LB11.game.Player;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Position;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

import java.util.ArrayList;

import static IS24_LB11.cli.view.PlayableCardView.WIDTH;

public class GameStage extends Stage {
    private static final int OFFSET_X = 4;
    private static final int OFFSET_Y = 1;
    private static final int UNIT_X = WIDTH-7;
    private static final int UNIT_Y = PlayableCardView.HEIGHT-3;

    private final Player player;
    private final ArrayList<PlayableCardView> cardviews;
    private final HandBox handBox;
    private Position pointer;
    private TerminalPosition gridBase;
    private boolean visibleHand;

    public GameStage(ViewHub viewHub, TerminalSize terminalSize, Player player) {
        super(viewHub, terminalSize);
        this.player = player;
        this.cardviews = new ArrayList<>();
        this.handBox = new HandBox(player.getHand());
        this.pointer = null;
        this.visibleHand = true;
        this.handBox.build();
        centerGridBase();
        resize(terminalSize);
    }

    @Override
    public void build() {
        drawBorders();
        drawGrid();
        drawPlacedCards();
        drawPointer();
        drawHand();
        updateViewHub();
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        super.resize(terminalSize);
        centerGridBase();
        setPointer(new Position(0,0));
        placeHand(terminalSize);
        updateViewHub();
    }

    @Override
    public void shift(Side side) {
        if (pointer == null) return;

        clearPointer();
        pointer = pointer.withRelative(side.asRelativePosition());
        TerminalPosition shiftedPosition = convertPosition(pointer);
        if (shiftedPosition.getColumn()+UNIT_X >= innerWidth() ||
                shiftedPosition.getRow()+UNIT_Y >= innerHeight() ||
                shiftedPosition.getColumn() < OFFSET_X ||
                shiftedPosition.getRow() < OFFSET_Y) {
            shiftGridBase(side.opposite());
            rebuild();
        } else {
            drawPointer();
            drawHand();
        }
    }

    public void buildHandCard(PlayableCard card) {
        handBox.buildSelectedCard(card);
        handBox.build();
        drawHand();
    }

    public void placeHand(TerminalSize terminalSize) {
        int x = terminalSize.getColumns()-handBox.getWidth();
        int y = (terminalSize.getRows()-handBox.getHeight())/2-2;
        handBox.setPosition(x, y);
    }

    public void centerGridBase() {
        int x = (innerArea.getWidth()/2-OFFSET_X) / UNIT_X;
        int y = (innerArea.getHeight()/2-OFFSET_Y) / UNIT_Y;
        setGridBase(x, y);
    }

    private void shiftGridBase(Side side) {
        Position delta = side.asRelativePosition();
        gridBase = gridBase.withRelative(delta.getX()*UNIT_X, delta.getY()*UNIT_Y);
    }

    private void drawPlacedCards() {
        for (PlayableCardView cardView : cardviews) {
            TerminalSize size = cardView.getSize();
            TerminalPosition terminalPosition = convertPosition(cardView.getBoardPosition());
            terminalPosition = terminalPosition.withRelative(-3, -1);
            cardView.setPosition(terminalPosition);
            draw(cardView);
            buildRelativeArea(size.withRelativeRows(1), terminalPosition.max(new TerminalPosition(0,0)));
        }
    }

    public void drawHand() {
        if (visibleHand && !isMininimalSize()) {
            draw(handBox);
            buildRelativeArea(handBox.getRectangle());
        }
    }

    public void clearPointer() {
        drawPointer(' ', TextColor.ANSI.DEFAULT);
    }

    private void drawPointer() {
        if (pointer == null) return;
        TextColor color = (player.getBoard().spotAvailable(pointer) ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.RED_BRIGHT);
        drawPointer('#', color);
    }

    private void drawPointer(char c, TextColor color) {
        if (pointer == null) return;
        int offsetX = 4;
        int baseX = pointer.getX()*UNIT_X+gridBase.getColumn()+1+offsetX;
        int baseY = pointer.getY()*UNIT_Y+gridBase.getRow()+UNIT_Y/2+1;
        for (int i=0; i<2; i++) {
            if (baseY+i < lastRow()-1) {
                drawCell(new TerminalPosition(baseX+2*i, baseY+i), c, color);
                drawCell(new TerminalPosition(baseX+UNIT_X-2*(i+offsetX), baseY+i), c, color);
            }
            if (i>0) {
                drawCell(new TerminalPosition(baseX+2*i, baseY-i), c, color);
                drawCell(new TerminalPosition(baseX+UNIT_X-2*(i+offsetX), baseY-i), c, color);
            }
        }
        buildRelativeArea(UNIT_X-1, UNIT_Y, baseX, baseY-UNIT_Y/2);
    }

    private void drawGrid() {
        String hline = "-".repeat(lastColumn());
        String vline = "¦".repeat(OFFSET_Y);
        for (int y=OFFSET_Y; y<=innerArea.getHeight(); y+=UNIT_Y) {
            fillRow(firstRow()+y, hline);
            vline += "+" + "¦".repeat(UNIT_Y-1);
            buildRelativeArea(borderArea.getWidth(), 1, firstColumn(), firstRow()+y);
        }
        for (int x=OFFSET_X; x<innerArea.getWidth(); x+=UNIT_X) {
            fillColumn(firstColumn()+x, vline);
            buildRelativeArea(1, innerHeight(), firstColumn()+x, firstRow());
        }
    }

    public void loadCardViews() {
        player.getBoard().getPlacedCards().stream().skip(cardviews.size()).forEach(placedCard -> {
            cardviews.add(CardViewFactory.newPlayableCardView(placedCard.card()));
            cardviews.getLast().setBoardPosition(placedCard.position());
        });
    }

    public void setSelectedCard(int selectedCard) {
        handBox.setSelectedCard(selectedCard);
        handBox.build();
        drawHand();
    }

    public void showHand() {
        visibleHand = true;
        drawHand();
    }

    public void hideHand() {
        visibleHand = false;
        rebuild();
    }

    private boolean isMininimalSize() {
        return innerHeight() < handBox.getHeight();
    }

    public void setPointer(Position pointer) {
        this.pointer = pointer;
    }

    private void setGridBase(int x, int y) {
        gridBase = new TerminalPosition(OFFSET_X + x * UNIT_X, OFFSET_Y + y * UNIT_Y);
    }

    private TerminalPosition convertPosition(Position position) {
        int x = position.getX()*UNIT_X+gridBase.getColumn();
        int y = position.getY()*UNIT_Y+gridBase.getRow();
        return new TerminalPosition(x, y);
    }
}
