package core;

public class Room {
    private int x; // x-coordinate of bottom-left corner
    private int y; // y-coordinate of bottom-left corner
    private int width;
    private int height;

    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean intersects(Room other) {
        boolean toTheLeft = this.x + this.width <= other.x;
        boolean toTheRight = this.x >= other.x + other.width;
        boolean above = this.y >= other.y + other.height;
        boolean below = this.y + this.height <= other.y;

        return (toTheLeft && toTheRight && above && below);
    }
}
