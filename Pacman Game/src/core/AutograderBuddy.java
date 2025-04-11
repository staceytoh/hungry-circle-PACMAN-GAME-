package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.List;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     * @source Assisted by ChatGpt - loadWorld() function
     */
    public static TETile[][] getWorldFromInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println("Error: Input is either null or empty");
            return null;
        }

        String processedInput = input.trim().toLowerCase();
        int startingIndexForSeed = processedInput.indexOf('n') + 1;

        if (startingIndexForSeed > 0) {
            String seedString = processedInput.substring(startingIndexForSeed).replaceAll("\\D+", "");

            long gameSeed;
            if (seedString.isEmpty()) {
                gameSeed = 1234;
            } else {
                gameSeed = Long.parseLong(seedString);
            }

            World simulatedWorld = new World(gameSeed);

            int movementStartIndex = processedInput.indexOf('s') + 1;
            String movementCommands = processedInput.substring(movementStartIndex);
            processMovement(simulatedWorld, movementCommands);

            if (processedInput.endsWith(":q")) {
                saveWorld(simulatedWorld);
            }

            return simulatedWorld.getWorld();
        } else if (processedInput.startsWith("l")) {
            World simulatedWorld = loadWorld();

            if (simulatedWorld != null) {
                String movementCommands = processedInput.substring(1);
                processMovement(simulatedWorld, movementCommands);

                if (processedInput.endsWith(":q")) {
                    saveWorld(simulatedWorld);
                }

                return simulatedWorld.getWorld();
            }
        }

        System.out.println("Error: Invalid input format");
        return null;
    }

    private static void processMovement(World world, String commands) {
        for (char command : commands.toCharArray()) {
            switch (command) {
                case 'W':
                    world.moveAvatar('W');
                    break;
                case 'S':
                    world.moveAvatar('S');
                    break;
                case 'A':
                    world.moveAvatar('A');
                    break;
                case 'D':
                    world.moveAvatar('D');
                    break;
                default:
                    System.out.println("Invalid move");
                    return;
            }
        }
    }

    private static void saveWorld(World world) {
        MainMenu mainMenu = new MainMenu();
        mainMenu.saveWorld(world);
    }


    private static World loadWorld() {
        MainMenu mainMenu = new MainMenu();
        Object[] loadedData = mainMenu.loadGame();

        if (loadedData != null) {
            long[] gameData = (long[]) loadedData[0];
            List<Point> coinPositions = (List<Point>) loadedData[1];

            World loadedWorld = new World(gameData[0]);
            loadedWorld.setAvatarPosition((int) gameData[1], (int) gameData[2]);
            loadedWorld.setCoinsCollected((int) gameData[3]);
            loadedWorld.placeCoinsFromPositions(coinPositions);

            return loadedWorld;
        } else {
            System.out.println("Error: failed to load game");
            return null;
        }
    }

    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
