package map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import util.HexMath;

public class HexMap {
    private final int width;
    private final int height;
    private final HexTile[][] tiles;

    @JsonCreator
    public HexMap(@JsonProperty("width") int width,
                  @JsonProperty("height") int height,
                  @JsonProperty("tiles") HexTile[][] tiles) {
        this.width = width;
        this.height = height;
        this.tiles = tiles == null ? new HexTile[width][height] : tiles;
    }

    public HexMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new HexTile[width][height];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public HexTile get(int col, int row) {
        if (col < 0 || row < 0 || col >= width || row >= height) return null;
        return tiles[col][row];
    }

    public void set(int col, int row, HexTile tile) {
        if (col < 0 || row < 0 || col >= width || row >= height) return;
        tiles[col][row] = tile;
    }

    public HexTile[][] getTiles() {
        return tiles;
    }

    public int[][] neighbors(int col, int row) {
        return HexMath.neighborCoords(col, row);
    }
}
