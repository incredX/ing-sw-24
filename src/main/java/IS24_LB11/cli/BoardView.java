package IS24_LB11.cli;

import IS24_LB11.cli.utils.Side;
import IS24_LB11.cli.view.CardViewFactory;
import IS24_LB11.cli.view.PlayableCardView;
import IS24_LB11.game.Board;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Position;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

import java.util.ArrayList;

public class BoardView extends Stage {
    private static final int OFFSET_X = 8;
    private static final int OFFSET_Y = 3;
    private static final int UNIT_X = PlayableCardView.WIDTH-7;
    private static final int UNIT_Y = PlayableCardView.HEIGHT-3;

    private final Board board;
    private final ArrayList<PlayableCardView> cardviews;
    private PlayableCardView currentCardView;
    private TerminalPosition gridBase;
    private Side lastVerticalShift;
    private Side lastHorizontalShift;

    public BoardView(TerminalSize terminalSize, Board board) {
        super(terminalSize);
        this.board = board;
        this.cardviews = new ArrayList<>();
        this.currentCardView = null;
        centerGridBase();
    }

    @Override
    public void build() {
        drawBorders();
        drawGrid();
        drawCards();
        drawPointer();
        drawCommandLine();
    }

    public void placeCurrentCard() {
        if (currentCardView == null) return;
        cardviews.add(currentCardView);
        currentCardView = null;
    }

    @Override
    public void shift(Side side) {
        if (currentCardView == null) return;
        if (lastVerticalShift == null)
            lastVerticalShift = (side.isVertical()) ? side : Side.fromInt(side.ordinal()+1);
        if (lastHorizontalShift == null)
            lastHorizontalShift = (!side.isVertical()) ? side : Side.fromInt(side.ordinal()+1);

        clearPointer();
        Side lastShift = side.isVertical() ? lastHorizontalShift : lastVerticalShift;
        currentCardView.move(side);
        currentCardView.move(lastShift);
        TerminalPosition shiftedPosition = convertPosition(currentCardView.getPosition());
        if (shiftedPosition.getColumn()+UNIT_X >= innerWidth() ||
                shiftedPosition.getRow()+UNIT_Y >= innerHeight() ||
                shiftedPosition.getColumn() < OFFSET_X ||
                shiftedPosition.getRow() < OFFSET_Y) {
            shiftGridBase(side.opposite());
            shiftGridBase(lastShift.opposite());
            rebuild();
        } else drawPointer();
        System.out.printf("V[%s] H[%s] %s\n", lastVerticalShift, lastHorizontalShift, side);
        if (side.isVertical()) lastVerticalShift = side;
        else lastHorizontalShift = side;
    }

    private void centerGridBase() {
        int x = (innerArea.getWidth()/2-OFFSET_X) / UNIT_X;
        int y = (innerArea.getHeight()/2-OFFSET_Y) / UNIT_Y;
        setGridBase(x, y);
    }

    private void shiftGridBase(Side side) {
        Position delta = side.asRelativePosition();
        gridBase = gridBase.withRelative(delta.getX()*UNIT_X, delta.getY()*UNIT_Y);
    }

    private void drawCards() {
        for (PlayableCardView cardView : cardviews) {
            TerminalSize size = cardView.getSize();
            TerminalPosition terminalPosition = convertPosition(cardView.getPosition());
            terminalPosition = terminalPosition.withRelative(-3, -1);
            cardView.setTerminalPosition(terminalPosition);
            draw(cardView);
            buildRelativeArea(size.withRelativeRows(1), terminalPosition.max(new TerminalPosition(0,0)));
        }
    }

    private void clearPointer() {
        drawPointer(' ', TextColor.ANSI.DEFAULT);
    }

    private void drawPointer() {
        if (currentCardView == null) return;
        TextColor color = (board.spotAvailable(currentCardView.getPosition()) ? TextColor.ANSI.WHITE : TextColor.ANSI.BLACK_BRIGHT);
        drawPointer('#', color);
    }

    private void drawPointer(char c, TextColor color) {
        if (currentCardView == null) return;
        int baseX = currentCardView.getPosition().getX()*UNIT_X+gridBase.getColumn()+2;
        int baseY = currentCardView.getPosition().getY()*UNIT_Y+gridBase.getRow()+UNIT_Y/2+1;
        for (int i=0; i<3; i++) {
            if (baseY+i < lastRow()-1) {
                drawCell(new TerminalPosition(baseX+2*i, baseY+i), c, color);
                drawCell(new TerminalPosition(baseX+UNIT_X-2*(i+1), baseY+i), c, color);
            }
            if (i>0) {
                drawCell(new TerminalPosition(baseX+2*i, baseY-i), c, color);
                drawCell(new TerminalPosition(baseX+UNIT_X-2*(i+1), baseY-i), c, color);
            }
        }
        buildRelativeArea(UNIT_X-1, UNIT_Y, baseX, baseY-UNIT_Y/2);
    }

    private void drawGrid() {
        String hline = "-".repeat(lastColumn());
        String vline = "¦".repeat(OFFSET_Y);
        for (int y=OFFSET_Y; y<innerArea.getHeight(); y+=UNIT_Y) {
            fillRow(firstRow()+y, hline);
            vline += "+" + "¦".repeat(UNIT_Y-1);
            buildRelativeArea(borderArea.getWidth(), 1, firstColumn(), firstRow()+y);
        }
        vline = vline.substring(0, lastRow()-2);
        for (int x=OFFSET_X; x<innerArea.getWidth(); x+=UNIT_X) {
            fillColumn(firstColumn()+x, vline);
            buildRelativeArea(1, innerHeight(), firstColumn()+x, firstRow());
        }
    }

    public void loadCardViews() {
        board.getPlacedCards().stream().skip(cardviews.size()).forEach(placedCard -> {
            cardviews.add(CardViewFactory.newPlayableCardView(placedCard.card()));
            cardviews.getLast().setPosition(placedCard.position());
        });
    }

    public void setCurrentCardView(PlayableCard playableCard, Position position) {
        currentCardView = CardViewFactory.newPlayableCardView(playableCard);
        currentCardView.setPosition(position);
    }

    public void setCurrentCardView(PlayableCard playableCard) {
        setCurrentCardView(playableCard, currentCardView.getPosition());
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
