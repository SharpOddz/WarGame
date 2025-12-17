package map;

import game.GameState;
import game.ScenarioConfig;
import units.NationType;
import units.Unit;
import units.UnitType;
import util.HexMath;

import java.util.*;

public class MapGenerator {
    private record Point(int col, int row) {}

    public static GameState generate(ScenarioConfig config) {
        Random random = new Random(config.getSeed());
        HexMap map = new HexMap(config.getWidth(), config.getHeight());
        double[][] noise = generateNoise(config.getWidth() / 2 + 1, config.getHeight(), random);
        fillTerrain(map, config, noise);
        placeUrbanCenters(map, config, random);
        generateRivers(map, config, random);
        generateRoads(map, config, random);
        GameState state = new GameState(config, map);
        seedUnits(state, random);
        state.setDirty(true);
        return state;
    }

    private static double[][] generateNoise(int width, int height, Random random) {
        double[][] values = new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                values[x][y] = random.nextDouble();
            }
        }
        // simple smoothing
        for (int iter = 0; iter < 3; iter++) {
            double[][] next = new double[width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    double sum = 0; int count = 0;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            int nx = x + dx; int ny = y + dy;
                            if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
                                sum += values[nx][ny];
                                count++;
                            }
                        }
                    }
                    next[x][y] = sum / count;
                }
            }
            values = next;
        }
        return values;
    }

    private static void fillTerrain(HexMap map, ScenarioConfig config, double[][] noise) {
        int w = map.getWidth();
        int h = map.getHeight();
        for (int x = 0; x < w; x++) {
            int mirror = w - 1 - x;
            for (int y = 0; y < h; y++) {
                double value = noise[Math.min(x, mirror) % noise.length][y];
                TerrainType base = TerrainType.fromBiome(config.getBiome(), value);
                // Additional shaping
                if (config.getBiome().equalsIgnoreCase("temperate") && value > 0.85) base = TerrainType.FOREST;
                if (config.getBiome().equalsIgnoreCase("desert") && value < 0.2) base = TerrainType.PLAIN;
                if (config.getBiome().equalsIgnoreCase("arctic") && y < h * 0.2) base = TerrainType.OCEAN;
                map.set(x, y, new HexTile(base));
            }
        }
    }

    private static void placeUrbanCenters(HexMap map, ScenarioConfig config, Random random) {
        int cities = Math.max(3, map.getWidth() / 50);
        int towns = cities * 3;
        Set<String> placed = new HashSet<>();
        for (int i = 0; i < cities; i++) {
            int col = random.nextInt(map.getWidth() / 2);
            int row = random.nextInt(map.getHeight());
            placeUrbanPair(map, placed, col, row, TerrainType.CITY);
        }
        for (int i = 0; i < towns; i++) {
            int col = random.nextInt(map.getWidth() / 2);
            int row = random.nextInt(map.getHeight());
            placeUrbanPair(map, placed, col, row, TerrainType.TOWN);
        }
        // installations on cities
        for (String key : placed) {
            String[] parts = key.split(",");
            int c = Integer.parseInt(parts[0]);
            int r = Integer.parseInt(parts[1]);
            HexTile tile = map.get(c, r);
            if (tile.getTerrain() == TerrainType.CITY) {
                tile.setFeature(randomFeature(random));
                int mirror = map.getWidth() - 1 - c;
                map.get(mirror, r).setFeature(tile.getFeature());
            }
        }
    }

    private static FeatureType randomFeature(Random random) {
        FeatureType[] options = {
                FeatureType.MILITARY_BASE, FeatureType.AIR_BASE, FeatureType.NAVAL_DOCKYARD,
                FeatureType.POWER_PLANT, FeatureType.OIL_DEPOT, FeatureType.SUPPLY_DEPOT,
                FeatureType.MILITARY_HQ, FeatureType.GOVERNMENT_HQ, FeatureType.NAVAL_HQ, FeatureType.AIRFORCE_HQ
        };
        return options[random.nextInt(options.length)];
    }

    private static void placeUrbanPair(HexMap map, Set<String> placed, int col, int row, TerrainType type) {
        int mirror = map.getWidth() - 1 - col;
        HexTile tile = map.get(col, row);
        if (tile == null) return;
        if (tile.getTerrain() == TerrainType.OCEAN) return;
        if (placed.contains(col + "," + row)) return;
        tile.setTerrain(type);
        map.get(mirror, row).setTerrain(type);
        placed.add(col + "," + row);
        placed.add(mirror + "," + row);
    }

    private static void generateRivers(HexMap map, ScenarioConfig config, Random random) {
        int riverCount = switch (config.getBiome().toLowerCase()) {
            case "desert" -> 1;
            case "arctic", "islands" -> 2;
            case "tropical" -> 5;
            default -> 3;
        };
        for (int i = 0; i < riverCount; i++) {
            int col = random.nextInt(map.getWidth() / 2);
            int row = random.nextInt(map.getHeight());
            traceRiver(map, col, row, random);
        }
    }

    private static void traceRiver(HexMap map, int col, int row, Random random) {
        int length = 20 + random.nextInt(60);
        int currentCol = col;
        int currentRow = row;
        for (int i = 0; i < length; i++) {
            int dir = random.nextInt(6);
            int[][] neighbors = HexMath.neighborCoords(currentCol, currentRow);
            int nextCol = neighbors[dir][0];
            int nextRow = neighbors[dir][1];
            if (map.get(nextCol, nextRow) == null) continue;
            setRiverBetween(map, currentCol, currentRow, nextCol, nextRow);
            currentCol = nextCol;
            currentRow = nextRow;
        }
    }

    private static void setRiverBetween(HexMap map, int c1, int r1, int c2, int r2) {
        int dir = directionIndex(c1, r1, c2, r2);
        if (dir == -1) return;
        HexTile a = map.get(c1, r1);
        HexTile b = map.get(c2, r2);
        a.getEdges().setRiver(dir, true);
        b.getEdges().setRiver((dir + 3) % 6, true);
        int mirrorC1 = map.getWidth() - 1 - c1;
        int mirrorC2 = map.getWidth() - 1 - c2;
        HexTile ma = map.get(mirrorC1, r1);
        HexTile mb = map.get(mirrorC2, r2);
        ma.getEdges().setRiver(dir, true);
        mb.getEdges().setRiver((dir + 3) % 6, true);
    }

    private static int directionIndex(int c1, int r1, int c2, int r2) {
        int[][] dirs = HexMath.neighborCoords(c1, r1);
        for (int i = 0; i < 6; i++) {
            if (dirs[i][0] == c2 && dirs[i][1] == r2) return i;
        }
        return -1;
    }

    private static void generateRoads(HexMap map, ScenarioConfig config, Random random) {
        List<Point> cities = collect(map, TerrainType.CITY);
        if (cities.size() > 1) {
            connectNetwork(map, cities, random);
        }
        List<Point> towns = collect(map, TerrainType.TOWN);
        for (Point town : towns) {
            Point nearestCity = nearest(town, cities);
            if (nearestCity != null) {
                drawRoad(map, town, nearestCity);
            }
        }
    }

    private static List<Point> collect(HexMap map, TerrainType type) {
        List<Point> list = new ArrayList<>();
        for (int c = 0; c < map.getWidth(); c++) {
            for (int r = 0; r < map.getHeight(); r++) {
                HexTile tile = map.get(c, r);
                if (tile != null && tile.getTerrain() == type) {
                    list.add(new Point(c, r));
                }
            }
        }
        return list;
    }

    private static Point nearest(Point src, List<Point> points) {
        Point best = null;
        int bestDist = Integer.MAX_VALUE;
        for (Point p : points) {
            int dist = Math.abs(p.col - src.col) + Math.abs(p.row - src.row);
            if (dist < bestDist) {
                bestDist = dist;
                best = p;
            }
        }
        return best;
    }

    private static void connectNetwork(HexMap map, List<Point> cities, Random random) {
        Set<Point> connected = new HashSet<>();
        connected.add(cities.get(0));
        while (connected.size() < cities.size()) {
            Point from = null, to = null;
            int best = Integer.MAX_VALUE;
            for (Point c : connected) {
                for (Point target : cities) {
                    if (connected.contains(target)) continue;
                    int dist = Math.abs(c.col - target.col) + Math.abs(c.row - target.row);
                    if (dist < best) {
                        best = dist;
                        from = c;
                        to = target;
                    }
                }
            }
            if (from != null && to != null) {
                drawRoad(map, from, to);
                connected.add(to);
            } else break;
        }
        // extra connections
        for (int i = 0; i < Math.min(3, cities.size()); i++) {
            Point a = cities.get(random.nextInt(cities.size()));
            Point b = cities.get(random.nextInt(cities.size()));
            drawRoad(map, a, b);
        }
    }

    private static void drawRoad(HexMap map, Point a, Point b) {
        int[] axialA = HexMath.offsetToAxial(a.col, a.row);
        int[] axialB = HexMath.offsetToAxial(b.col, b.row);
        int N = Math.max(Math.abs(axialA[0] - axialB[0]), Math.abs(axialA[1] - axialB[1]));
        for (int i = 0; i <= N; i++) {
            double t = N == 0 ? 0 : (double) i / N;
            double q = axialA[0] + (axialB[0] - axialA[0]) * t;
            double r = axialA[1] + (axialB[1] - axialA[1]) * t;
            int[] rounded = HexMath.cubeToAxial(HexMath.cubeRound(q, -q - r, r));
            int[] offset = HexMath.axialToOffset(rounded[0], rounded[1]);
            int col = Math.max(0, Math.min(map.getWidth() - 1, offset[0]));
            int row = Math.max(0, Math.min(map.getHeight() - 1, offset[1]));
            connectRoadStep(map, col, row, a, b);
        }
    }

    private static void connectRoadStep(HexMap map, int col, int row, Point a, Point b) {
        int[][] neighbors = HexMath.neighborCoords(col, row);
        for (int i = 0; i < 6; i++) {
            int nc = neighbors[i][0];
            int nr = neighbors[i][1];
            if (map.get(nc, nr) == null) continue;
            if (distance(nc, nr, a.col, a.row) + distance(nc, nr, b.col, b.row)
                    < distance(col, row, a.col, a.row) + distance(col, row, b.col, b.row)) {
                map.get(col, row).getEdges().setRoad(i, true);
                map.get(nc, nr).getEdges().setRoad((i + 3) % 6, true);
                int mirrorC = map.getWidth() - 1 - col;
                int mirrorNc = map.getWidth() - 1 - nc;
                map.get(mirrorC, row).getEdges().setRoad(i, true);
                map.get(mirrorNc, nr).getEdges().setRoad((i + 3) % 6, true);
            }
        }
    }

    private static int distance(int c1, int r1, int c2, int r2) {
        return Math.abs(c1 - c2) + Math.abs(r1 - r2);
    }

    private static void seedUnits(GameState state, Random random) {
        HexMap map = state.getMap();
        List<Point> cities = collect(map, TerrainType.CITY);
        if (!cities.isEmpty()) {
            for (Point city : cities) {
                NationType nation = random.nextBoolean() ? NationType.fromString(state.getConfig().getUserNation()) : NationType.fromString(state.getConfig().getAiNation());
                state.getUnits().add(Unit.basic(UnitType.INFANTRY, nation, city.col, city.row));
                state.getUnits().add(Unit.basic(UnitType.ARMOR, nation, city.col, city.row));
            }
        }
    }
}
