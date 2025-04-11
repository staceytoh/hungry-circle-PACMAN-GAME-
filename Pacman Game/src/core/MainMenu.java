package core;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.io.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainMenu {
    private final TERenderer ter = new TERenderer();
    private World currentWorld;

    public void displayMainMenu() {
        StdDraw.setCanvasSize(768, 824);
        StdDraw.setXscale(0, 768);
        StdDraw.setYscale(0, 824);
        drawMainMenu();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                key = Character.toUpperCase(key);

                switch (key) {
                    // load game
                    case 'N':
                        long seed = requestSeed();
                        currentWorld = new World(seed);
                        ter.initialize(75, 50, 0, 0);
                        currentWorld.placeCoins();
                        ter.renderFrame(currentWorld.getWorldWithAvatar());
                        displayDateAndTime();
                        startGame();
                        break;
                    // load game
                    case 'L':
                        Object[] loadedData = loadGame();
                        if (loadedData != null) {
                            long[] gameData = (long[]) loadedData[0];
                            java.util.List<Point> coinPositions = (java.util.List<Point>) loadedData[1];
                            currentWorld = new World(gameData[0]);
                            currentWorld.setAvatarPosition((int) gameData[1], (int) gameData[2]);
                            currentWorld.setCoinsCollected((int) gameData[3]);
                            currentWorld.placeCoinsFromPositions(coinPositions);
                            ter.initialize(75, 50, 0, 0);
                            ter.renderFrame(currentWorld.getWorldWithAvatar());
                            startGame();
                        } else {
                            System.out.println("Error: Failed to load game");
                            System.exit(0);
                        }
                        break;
                    // quit game
                    case 'Q':
                        saveAndQuit();
                        break;
                    default:
                        System.out.println("Unrecognized input: " + key);
                        drawMainMenu();
                        break;
                }
            }
        }
    }

    private void startGame() {
        boolean colonPressed = false;

        while (true) {
            displayDateAndTime();
            checkTileUnderCursor();
            displayCoinsCollected();
            if (StdDraw.hasNextKeyTyped()) {
                displayDateAndTime();
                char key = StdDraw.nextKeyTyped();
                String keyStr = Character.toString(key).toUpperCase();

                double previousPointerX = -1;
                double previousPointerY = -1;
                String lastDateTime = "";
                int previousCoinsCollected = -1;

                if (colonPressed && keyStr.equals("Q")) {
                    saveAndQuit();
                    return;
                } else if (keyStr.equals(":")) {
                    colonPressed = true;
                    continue;
                } else {
                    colonPressed = false;
                }
                if (key == 'T') {
                    currentWorld.toggleLineOfSight();
                    renderWithLineOfSight();
                } else {
                    switch (keyStr) {
                        case "W":
                            currentWorld.moveAvatar('W');
                            break;
                        case "A":
                            currentWorld.moveAvatar('A');
                            break;
                        case "S":
                            currentWorld.moveAvatar('S');
                            break;
                        case "D":
                            currentWorld.moveAvatar('D');
                            break;
                        case "T":
                            currentWorld.toggleLineOfSight();
                            renderWithLineOfSight();
                            break;
                        default:
                            System.out.println("Unrecognized command: " + keyStr);
                            break;
                    }
                }
                ter.renderFrame(currentWorld.getWorldWithAvatar());

                //next three ifS prevent flickering
                if (!currentWorld.isLineOfSightToggleOn()) {
                    renderWithLineOfSight();
                }
                if (StdDraw.mouseX() != previousPointerX || StdDraw.mouseY() != previousPointerY) {
                    previousPointerX = StdDraw.mouseX();
                    previousPointerY = StdDraw.mouseY();
                    checkTileUnderCursor();
                }

                String currentDateTime = getCurrentDateTime();
                if (!currentDateTime.equals(lastDateTime)) {
                    displayDateAndTime();
                    lastDateTime = currentDateTime;
                }

                int currentCoinsCollected = currentWorld.getCoinsCollected();
                if (currentCoinsCollected != previousCoinsCollected) {
                    displayCoinsCollected();
                    previousCoinsCollected = currentCoinsCollected;
                }
            }
            StdDraw.show();
            StdDraw.pause(100);
        }
    }

    private String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM dd yyyy HH:mm:ss");
        return now.format(formatter);
    }

    private void saveAndQuit() {
        saveWorld(currentWorld);
        System.exit(0);
    }

    public void saveWorld(World world) {
        try (PrintWriter out = new PrintWriter("world_state.txt")) {
            out.println(world.getSeed());
            out.println(world.getAvatarX());
            out.println(world.getAvatarY());
            out.println(world.getCoinsCollected());

            java.util.List<Point> coinPositions = world.getCoinPositions();
            out.println(coinPositions.size());
            for (Point position : coinPositions) {
                out.println(position.x + "," + position.y);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawMainMenu() {
        StdDraw.clear(StdDraw.BLACK);

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 60));
        StdDraw.text(384, 700, "CS61B: THE GAME");

        StdDraw.setFont(new Font("Arial", Font.PLAIN, 40));
        StdDraw.text(384, 450, "New Game (N)");
        StdDraw.text(384, 350, "Load Game (L)");
        StdDraw.text(384, 250, "Quit (Q)");

        StdDraw.show();
    }

    private void displayDateAndTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, MMM dd yyyy HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(73, 49, 20, 1);

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textRight(73, 49, formattedDateTime);
        StdDraw.show();
    }

    public void displayCoinsCollected() {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 14));

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(40, 49, 20, 1);

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textLeft(40, 49, "Coins collected: " + currentWorld.getCoinsCollected());
    }

    public void checkTileUnderCursor() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();

        if (x >= 0 && x < 75 && y >= 0 && y < 50) {
            TETile[][] tile = currentWorld.getWorld();
            String tileType = "";

            if (x == currentWorld.getAvatarX() && y == currentWorld.getAvatarY()) {
                tileType = "Avatar";
            } else if (tile[x][y] == Tileset.WALL) {
                tileType = "Wall";
            } else if (tile[x][y] == Tileset.FLOOR) {
                tileType = "Floor";
            } else if (tile[x][y] == Tileset.COIN) {
                tileType = "Coin";
            } else {
                tileType = "Out of the world";
            }

            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font("Arial", Font.BOLD, 14));

            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.filledRectangle(2, 49, 20, 1);
            StdDraw.setPenColor(StdDraw.WHITE);

            StdDraw.textLeft(2, 49, "Mouse pointer is at: " + tileType);
        }
    }


    private long requestSeed() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(384, 700, "Enter a seed and press 'S' to start:");
        StdDraw.show();

        StringBuilder seedBuilder = new StringBuilder();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                if (Character.toUpperCase(ch) == 'S' && seedBuilder.length() > 0) {
                    try {
                        return Long.parseLong(seedBuilder.toString());
                    } catch (NumberFormatException e) {
                        StdDraw.clear(StdDraw.BLACK);
                        StdDraw.text(384, 700, "Invalid seed. Please enter a valid seed:");
                        StdDraw.show();
                        seedBuilder = new StringBuilder();
                    }
                } else if (Character.isDigit(ch)) {
                    seedBuilder.append(ch);
                    StdDraw.clear(StdDraw.BLACK);
                    StdDraw.text(384, 700, "Enter a seed and press 'S' to start:");
                    StdDraw.text(384, 650, seedBuilder.toString());
                    StdDraw.show();
                }
            }
        }
    }

    /**
     * @source Assisted by ChatGPT - loadGame() function
     * Helped with choosing object type to return/save game info
    **/
    public Object[] loadGame() {
        File saveFile = new File("world_state.txt");
        if (!saveFile.exists()) {
            System.out.println("Error: No previous save found");
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(saveFile))) {
            long seed = Long.parseLong(br.readLine());
            int avatarX = Integer.parseInt(br.readLine());
            int avatarY = Integer.parseInt(br.readLine());
            int coinsCollected = Integer.parseInt(br.readLine());

            int coinCount = Integer.parseInt(br.readLine());
            java.util.List<Point> coinPositions = new ArrayList<>();
            for (int i = 0; i < coinCount; i++) {
                String[] coordinates = br.readLine().split(",");
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);
                coinPositions.add(new Point(x, y));
            }

            return new Object[]{new long[]{seed, avatarX, avatarY, coinsCollected}, coinPositions};
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error: Unable to load game state");
            e.printStackTrace();
            return null;
        }
    }

    public void renderWithLineOfSight() {
        for (int i = 0; i < currentWorld.getCanvasWidth(); i++) {
            for (int j = 0; j < currentWorld.getCanvasHeight(); j++) {
                if (!currentWorld.isWithinLineOfSight(i, j)) {
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.filledSquare(i + 1, j + 1, 1);
                }
            }
        }
    }
}
