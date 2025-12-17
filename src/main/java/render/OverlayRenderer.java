package render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import map.HexMap;
import map.HexTile;
import map.FeatureType;
import map.TerrainType;

import java.util.List;
import units.Unit;

public class OverlayRenderer {
    public void drawHoverInfo(GraphicsContext gc, HexMap map, int col, int row, List<Unit> units) {
        gc.setFill(Color.color(0,0,0,0.6));
        gc.fillRect(gc.getCanvas().getWidth() - 220, 10, 210, 80);
        gc.setFill(Color.WHITE);
        if (col < 0 || row < 0 || map.get(col, row)==null) {
            gc.fillText("Hover a hex", gc.getCanvas().getWidth() - 200, 30);
            return;
        }
        HexTile tile = map.get(col, row);
        long count = units.stream().filter(u -> u.getCol()==col && u.getRow()==row).count();
        String feature = tile.getFeature() == FeatureType.NONE ? "" : " Site: " + tile.getFeature().name();
        int riverEdges = countEdges(tile, true);
        int roadEdges = countEdges(tile, false);
        gc.fillText("X: " + col + " Y: " + row, gc.getCanvas().getWidth() - 200, 30);
        gc.fillText("Terrain: " + tile.getTerrain(), gc.getCanvas().getWidth() - 200, 45);
        gc.fillText("Units: " + count + feature, gc.getCanvas().getWidth() - 200, 60);
        gc.fillText("Rivers: " + riverEdges + " Roads: " + roadEdges, gc.getCanvas().getWidth() - 200, 75);
    }

    private int countEdges(HexTile tile, boolean rivers) {
        int count = 0;
        boolean[] arr = rivers ? tile.getEdges().getRivers() : tile.getEdges().getRoads();
        for (boolean b : arr) if (b) count++;
        return count;
    }
}
