package render;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import map.EdgeFeature;
import map.FeatureType;
import map.HexMap;
import map.HexTile;
import map.TerrainType;
import util.HexMath;

import java.util.List;
import units.Unit;

public class MapRenderer {
    public void render(GraphicsContext gc, HexMap map, double zoom, double offsetX, double offsetY, int hoverCol, int hoverRow, List<Unit> units) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        for (int c = 0; c < map.getWidth(); c++) {
            for (int r = 0; r < map.getHeight(); r++) {
                HexTile tile = map.get(c, r);
                if (tile == null) continue;
                Point2D[] corners = HexMath.polygonCorners(c, r, zoom, offsetX, offsetY);
                gc.setFill(colorForTerrain(tile.getTerrain()));
                double[] xs = new double[6];
                double[] ys = new double[6];
                for (int i = 0; i < 6; i++) {
                    xs[i] = corners[i].getX();
                    ys[i] = corners[i].getY();
                }
                gc.fillPolygon(xs, ys, 6);
                gc.setStroke(Color.color(0,0,0,0.3));
                gc.strokePolygon(xs, ys, 6);

                drawRivers(gc, corners, tile.getEdges());
                drawRoads(gc, map, c, r, zoom, offsetX, offsetY, tile.getEdges());
                drawFeature(gc, tile, HexMath.hexCenter(c, r, zoom, offsetX, offsetY));
            }
        }
        drawUnits(gc, units, zoom, offsetX, offsetY);
        if (hoverCol >=0 && hoverRow >=0 && map.get(hoverCol, hoverRow)!=null) {
            Point2D[] corners = HexMath.polygonCorners(hoverCol, hoverRow, zoom, offsetX, offsetY);
            double[] xs = new double[6];
            double[] ys = new double[6];
            for (int i=0;i<6;i++){xs[i]=corners[i].getX(); ys[i]=corners[i].getY();}
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(2);
            gc.strokePolygon(xs, ys, 6);
        }
    }

    private void drawRivers(GraphicsContext gc, Point2D[] corners, EdgeFeature edges) {
        gc.setStroke(Color.CORNFLOWERBLUE);
        gc.setLineWidth(2);
        for (int i = 0; i < 6; i++) {
            if (edges.hasRiver(i)) {
                Point2D a = corners[i];
                Point2D b = corners[(i + 1) % 6];
                gc.strokeLine(a.getX(), a.getY(), b.getX(), b.getY());
            }
        }
    }

    private void drawRoads(GraphicsContext gc, HexMap map, int col, int row, double zoom, double offsetX, double offsetY, EdgeFeature edges) {
        Point2D center = HexMath.hexCenter(col, row, zoom, offsetX, offsetY);
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(2);
        int[][] neighbors = HexMath.neighborCoords(col, row);
        for (int i = 0; i < 6; i++) {
            if (edges.hasRoad(i)) {
                int nc = neighbors[i][0];
                int nr = neighbors[i][1];
                if (map.get(nc, nr) == null) continue;
                Point2D nCenter = HexMath.hexCenter(nc, nr, zoom, offsetX, offsetY);
                gc.strokeLine(center.getX(), center.getY(), nCenter.getX(), nCenter.getY());
            }
        }
    }

    private Color colorForTerrain(TerrainType terrain) {
        return switch (terrain) {
            case PLAIN -> Color.BEIGE;
            case HILL -> Color.DARKKHAKI;
            case MOUNTAIN -> Color.GRAY;
            case FOREST -> Color.DARKGREEN;
            case JUNGLE -> Color.GREEN;
            case LAKE -> Color.LIGHTBLUE;
            case OCEAN -> Color.NAVY;
            case TOWN -> Color.BURLYWOOD;
            case CITY -> Color.LIGHTGRAY;
        };
    }

    private void drawFeature(GraphicsContext gc, HexTile tile, Point2D center) {
        if (tile.getTerrain() == TerrainType.CITY || tile.getTerrain() == TerrainType.TOWN) {
            gc.setFill(Color.DARKRED);
            gc.fillOval(center.getX() - 4, center.getY() - 4, 8, 8);
        }
        if (tile.getFeature() != FeatureType.NONE) {
            gc.setFill(Color.WHITE);
            gc.fillRect(center.getX() - 6, center.getY() - 6, 12, 12);
            gc.setFill(Color.BLACK);
            String code = featureCode(tile.getFeature());
            gc.fillText(code, center.getX() - 6, center.getY() + 4);
        }
    }

    private void drawUnits(GraphicsContext gc, List<Unit> units, double zoom, double offsetX, double offsetY) {
        gc.setFill(Color.ORANGE);
        for (Unit unit : units) {
            Point2D center = HexMath.hexCenter(unit.getCol(), unit.getRow(), zoom, offsetX, offsetY);
            gc.fillOval(center.getX() - 6, center.getY() - 6, 12, 12);
            gc.setFill(Color.BLACK);
            gc.fillText(unit.getType().name().substring(0, 1), center.getX() - 3, center.getY() + 4);
            gc.setFill(Color.ORANGE);
        }
    }

    private String featureCode(FeatureType feature) {
        return switch (feature) {
            case MILITARY_BASE -> "MB";
            case AIR_BASE -> "AB";
            case NAVAL_DOCKYARD -> "ND";
            case POWER_PLANT -> "PP";
            case OIL_DEPOT -> "OD";
            case SUPPLY_DEPOT -> "SD";
            case MILITARY_HQ -> "HQ";
            case GOVERNMENT_HQ -> "GH";
            case NAVAL_HQ -> "NH";
            case AIRFORCE_HQ -> "AH";
            default -> "";
        };
    }
}
