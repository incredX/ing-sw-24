package IS24_LB11.gui.scenesControllers.ScoreboardController;

public class AnimationInstruction {

    private final int pointRepresented;
    private final int X;
    private final int Y;


    public AnimationInstruction(int pointRepresented, int x, int y) {
        this.pointRepresented = pointRepresented;
        this.X = x;
        this.Y = y;
    }

    public int getPointRepresented() {
        return pointRepresented;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

}
