import core.AutograderBuddy;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;

public class WorldGenTests {
    @Test
    public void basicTest() {
        // put different seeds here to test different worlds
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n1234567890123456789s");

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000); // pause for 5 seconds so you can see the output
    }

    @Test
    public void basicInteractivityTest() {
        String input = "n123swasdwasd";

        TETile[][] tiles = AutograderBuddy.getWorldFromInput(input);

        if (tiles == null) {
            throw new AssertionError("Error: World is null");
        }

        int expectedWidth = 75;
        int expectedHeight = 50;
        if (tiles.length != expectedWidth) {
            throw new AssertionError("Error: World width should be " + expectedWidth);
        }
        if (tiles[0].length != expectedHeight) {
            throw new AssertionError("Error: World height should be " + expectedHeight);
        }

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000);
    }

    @Test
    public void basicSaveTest() {
        String saveInput = "n123swasd:q";
        String loadInput = "lwasd";

        TETile[][] tilesSave = AutograderBuddy.getWorldFromInput(saveInput);
        TETile[][] tilesLoad = AutograderBuddy.getWorldFromInput(loadInput);

        if (tilesSave == null) {
            throw new AssertionError("Error: Saved world is null");
        }
        if (tilesLoad == null) {
            throw new AssertionError("Error: Loaded world is null");
        }

        if (tilesSave.length != tilesLoad.length) {
            throw new AssertionError("Error: World width is not the same after loading");
        }
        if (tilesSave[0].length != tilesLoad[0].length) {
            throw new AssertionError("Error: World height is not the same after loading");
        }

        for (int x = 0; x < tilesSave.length; x++) {
            for (int y = 0; y < tilesSave[0].length; y++) {
                if (tilesSave[x][y] != tilesLoad[x][y]) {
                    throw new AssertionError("Error: Tile at position (" + x + ", " + y + ")"
                            + " is not the same after loading");
                }
            }
        }

        TERenderer ter = new TERenderer();
        ter.initialize(tilesLoad.length, tilesLoad[0].length);
        ter.renderFrame(tilesLoad);
        StdDraw.pause(5000);
    }
}
