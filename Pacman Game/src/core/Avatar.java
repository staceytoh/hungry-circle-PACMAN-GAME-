package core;

public class Avatar {
    private int xPosition;
    private int yPosition;

    public Avatar(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
    }

    public void setXPosition(int x) {
        this.xPosition = x;
    }

    public void setYPosition(int y) {
        this.yPosition = y;
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public void goUp() {
        yPosition += 1;
    }

    public void goDown() {
        yPosition -= 1;
    }

    public void goLeft() {
        xPosition -= 1;
    }

    public void goRight() {
        xPosition += 1;
    }
}
