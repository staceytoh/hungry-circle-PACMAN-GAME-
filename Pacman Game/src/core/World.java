package core;

import tileengine.TETile;
import tileengine.Tileset;
import java.awt.*;

import java.util.*;
import java.util.List;

import utils.RandomUtils;

public class World {

    // build your own world!
    private static final int CANVAS_WIDTH = 75;
    private static final int CANVAS_HEIGHT = 50;
    private static final int MIN_ROOM_WIDTH = 3;
    private static final int MIN_ROOM_HEIGHT = 3;
    private static final int MAX_ROOM_WIDTH = 15;
    private static final int MAX_ROOM_HEIGHT = 15;
    private static final int BORDER_FOR_HUD = 3;
    private int coinsCollected;
    private List<Point> coinPositions;

    private boolean lineOfSightEnabled = false;

    private TETile[][] tiles2DArray;
    private Random random;
    List<Room> rooms;
    long seed;

    private Avatar avatar;


    public World(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
        this.tiles2DArray = new TETile[CANVAS_WIDTH][CANVAS_HEIGHT];
        this.rooms = new ArrayList<>();
        this.coinPositions = new ArrayList<>();
        defaultCanvas();
        rooms.clear();

        generateAllRooms(RandomUtils.uniform(random, 15, 25));
        makeHallways();
        initializeAvatar();
    }

    public int getCanvasWidth() {
        return CANVAS_WIDTH;
    }

    public int getCanvasHeight() {
        return CANVAS_HEIGHT;
    }

    public TETile getTile(int x, int y) {
        if (x >= 0 && x < getCanvasWidth() && y >= 0 && y < getCanvasHeight()) {
            return tiles2DArray[x][y];
        } else {
            return null;
        }
    }

    private void initializeAvatar() {
        if (!rooms.isEmpty()) {
            Room startRoom = rooms.get(random.nextInt(rooms.size()));
            int startX = random.nextInt(startRoom.getWidth()) + startRoom.getX();
            int startY = random.nextInt(startRoom.getHeight()) + startRoom.getY();
            avatar = new Avatar(startX, startY);
        } else {
            avatar = new Avatar(0, 0);
        }
    }

    private void defaultCanvas() {
        for (int x = 0; x < CANVAS_WIDTH; x++) {
            for (int y = 0; y < CANVAS_HEIGHT; y++) {
                tiles2DArray[x][y] = Tileset.NOTHING;
            }
        }
    }

    private boolean areRoomsIntersecting(Room room) {
        for (Room otherRoom : rooms) {
            if (room.intersects(otherRoom)) {
                return true;
            }
        }
        return false;
    }

    private void addWallsAround(Room newRoom) {
        int minX = Math.max(newRoom.getX() - 1, 0);
        int maxX = Math.min(newRoom.getX() + newRoom.getWidth(), CANVAS_WIDTH);
        int minY = Math.max(newRoom.getY() - 1, 0);
        int maxY = Math.min(newRoom.getY() + newRoom.getHeight(), CANVAS_HEIGHT);

        for (int x = minX; x <= maxX; x++) {
            if (minY >= 0 && tiles2DArray[x][minY] == Tileset.NOTHING) {
                tiles2DArray[x][minY] = Tileset.WALL;
            }
            if (maxY <= CANVAS_HEIGHT && tiles2DArray[x][maxY] == Tileset.NOTHING) {
                tiles2DArray[x][maxY] = Tileset.WALL;
            }
        }

        for (int y = minY + 1; y <= maxY; y++) {
            if (minX >= 0 && tiles2DArray[minX][y] == Tileset.NOTHING) {
                tiles2DArray[minX][y] = Tileset.WALL;
            }
            if (maxX <= CANVAS_WIDTH && tiles2DArray[maxX][y] == Tileset.NOTHING) {
                tiles2DArray[maxX][y] = Tileset.WALL;
            }
        }
    }

    private Room generateANewRoom() {
        int attempt = 0;
        while (attempt < 25) {
            int width = RandomUtils.uniform(random, MIN_ROOM_WIDTH, MAX_ROOM_WIDTH);
            int height = RandomUtils.uniform(random, MIN_ROOM_HEIGHT, MAX_ROOM_HEIGHT);
            int x = RandomUtils.uniform(random, 1, CANVAS_WIDTH - width);
            int y = RandomUtils.uniform(random, 1, CANVAS_HEIGHT - height -  BORDER_FOR_HUD);
            Room newRoom = new Room(x, y, width, height);

            if (newRoom != null && !areRoomsIntersecting(newRoom) && hasSufficientSpace(newRoom)) {
                for (int a = x; a < x + width; a++) {
                    for (int b = y; b < y + height; b++) {
                        tiles2DArray[a][b] = Tileset.FLOOR;
                    }
                }
                addWallsAround(newRoom);
                return newRoom;
            }
            attempt++;
        }
        return null;
    }

    private boolean hasSufficientSpace(Room newRoom) {
        int buffer = 2;
        for (Room roomNow : rooms) {
            if (newRoom.getX() - buffer < roomNow.getX() + roomNow.getWidth()
                    && newRoom.getX() + newRoom.getWidth() + buffer > roomNow.getX()
                    && newRoom.getY() - buffer < roomNow.getY() + roomNow.getHeight()
                    && newRoom.getY() + newRoom.getHeight() + buffer > roomNow.getY()) {
                return false;
            }
        }
        return true;
    }

    private void makeHallways() {
        for (int i = 0; i < rooms.size() - 1; i++) {
            Room current = rooms.get(i);
            Room next = rooms.get(i + 1);

            Point centerOfCurrent = calculateCenter(current);
            Point centerOfNext = calculateCenter(next);

            createCorridor(centerOfCurrent, centerOfNext);
        }
    }

    private Point calculateCenter(Room room) {
        int centerX = random.nextInt(room.getWidth()) + room.getX();
        int centerY = random.nextInt(room.getHeight()) + room.getY();
        return new Point(centerX, centerY);
    }

    private void createCorridor(Point start, Point end) {
        for (int x = Math.min(start.x, end.x); x <= Math.max(start.x, end.x); x++) {
            if (tiles2DArray[x][start.y] != Tileset.FLOOR) {
                tiles2DArray[x][start.y] = Tileset.FLOOR;
            }
            addWallsAround(x, start.y);
        }

        for (int y = Math.min(start.y, end.y); y <= Math.max(start.y, end.y); y++) {
            if (tiles2DArray[end.x][y] != Tileset.FLOOR) {
                tiles2DArray[end.x][y] = Tileset.FLOOR;
            }
            addWallsAround(end.x, y);
        }
    }

    private void addWallsAround(int centerX, int centerY) {
        for (int offsetY = -1; offsetY <= 1; offsetY++) {
            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                int neighborX = centerX + offsetX;
                int neighborY = centerY + offsetY;

                if (isInBounds(neighborX, neighborY)) {
                    if (tiles2DArray[neighborX][neighborY] == Tileset.NOTHING) {
                        tiles2DArray[neighborX][neighborY] = Tileset.WALL;
                    }
                }
            }
        }
    }

    public void generateAllRooms(int numRooms) {
        for (int i = 0; i < numRooms; i++) {
            Room temp = generateANewRoom();
            if (temp != null) {
                rooms.add(temp);
            }
        }
    }

    public TETile[][] getWorld() {
        return tiles2DArray;
    }

    public TETile[][] getWorldWithAvatar() {
        TETile[][] worldCopy = deepCopy(tiles2DArray);

        worldCopy[avatar.getXPosition()][avatar.getYPosition()] = Tileset.AVATAR;
        return worldCopy;
    }

    private TETile[][] deepCopy(TETile[][] original) {
        TETile[][] copy = new TETile[original.length][];

        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }

        return copy;
    }

    public void moveAvatar(char direction) {
        int originalX = avatar.getXPosition();
        int originalY = avatar.getYPosition();

        switch (direction) {
            case 'W':
                avatar.goUp();
                break;
            case 'S':
                avatar.goDown();
                break;
            case 'A':
                avatar.goLeft();
                break;
            case 'D':
                avatar.goRight();
                break;
            default:
                System.out.println("Invalid move");
                return;
        }

        if (isInBounds(avatar.getXPosition(), avatar.getYPosition())) {
            TETile newPosTile = tiles2DArray[avatar.getXPosition()][avatar.getYPosition()];
            if (newPosTile != Tileset.FLOOR && newPosTile != Tileset.COIN) {
                avatar.setXPosition(originalX);
                avatar.setYPosition(originalY);
            } else if (newPosTile == Tileset.COIN) {
                collectCoin(avatar.getXPosition(), avatar.getYPosition());
            }
        } else {
            avatar.setXPosition(originalX);
            avatar.setYPosition(originalY);
        }
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < CANVAS_WIDTH && y >= 0 && y < CANVAS_HEIGHT;
    }

    public long getSeed() {
        return seed;
    }

    public int getAvatarX() {
        return avatar.getXPosition();
    }

    public int getAvatarY() {
        return avatar.getYPosition();
    }

    public void setAvatarPosition(int x, int y) {
        avatar.setXPosition(x);
        avatar.setYPosition(y);
    }

    public void placeCoins() {
        int numCoins = RandomUtils.uniform(random, 3, 6);
        List<Point> floorPositions = new ArrayList<>();

        for (int x = 0; x < CANVAS_WIDTH; x++) {
            for (int y = 0; y < CANVAS_HEIGHT; y++) {
                if (tiles2DArray[x][y] == Tileset.FLOOR) {
                    floorPositions.add(new Point(x, y));
                }
            }
        }

        Collections.shuffle(floorPositions, random);
        for (int i = 0; i < numCoins && i < floorPositions.size(); i++) {
            Point coinPosition = floorPositions.get(i);
            tiles2DArray[coinPosition.x][coinPosition.y] = Tileset.COIN;
            coinPositions.add(coinPosition);
        }
    }

    public void placeCoinsFromPositions(List<Point> positions) {
        clearCoins();
        for (Point position : positions) {
            if (isValidFloorTile(position.x, position.y)) {
                tiles2DArray[position.x][position.y] = Tileset.COIN;
                coinPositions.add(position);
            }
        }
    }

    private boolean isValidFloorTile(int x, int y) {
        return isInBounds(x, y) && tiles2DArray[x][y] == Tileset.FLOOR;
    }

    public void clearCoins() {
        for (Point coinPos : coinPositions) {
            tiles2DArray[coinPos.x][coinPos.y] = Tileset.FLOOR;
        }
        coinPositions.clear();
    }

    private void collectCoin(int x, int y) {
        tiles2DArray[x][y] = Tileset.FLOOR;
        coinsCollected++;
        coinPositions.remove(new Point(x, y));
    }

    public List<Point> getCoinPositions() {
        return coinPositions;
    }

    public int getCoinsCollected() {
        return coinsCollected;
    }

    public void setCoinsCollected(int coins) {
        this.coinsCollected = coins;
    }

    public boolean isLineOfSightToggleOn() {
        return lineOfSightEnabled;
    }

    public void toggleLineOfSight() {
        lineOfSightEnabled = !lineOfSightEnabled;
    }

    public boolean isWithinLineOfSight(int x, int y) {
        double distance = Math.sqrt(Math.pow(x - getAvatarX(), 2) + Math.pow(y - getAvatarY(), 2));

        return distance <= 7.0;
    }
}
