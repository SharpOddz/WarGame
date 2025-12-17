package game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import map.HexMap;
import units.Unit;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private String version = "1.0";
    private ScenarioConfig config;
    private HexMap map;
    private List<Unit> units;
    private double cameraX;
    private double cameraY;
    private double zoom;
    private boolean dirty;

    @JsonCreator
    public GameState(@JsonProperty("version") String version,
                     @JsonProperty("config") ScenarioConfig config,
                     @JsonProperty("map") HexMap map,
                     @JsonProperty("units") List<Unit> units,
                     @JsonProperty("cameraX") double cameraX,
                     @JsonProperty("cameraY") double cameraY,
                     @JsonProperty("zoom") double zoom) {
        this.version = version == null ? "1.0" : version;
        this.config = config;
        this.map = map;
        this.units = units == null ? new ArrayList<>() : units;
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        this.zoom = zoom == 0 ? 1.0 : zoom;
        this.dirty = false;
    }

    public GameState(ScenarioConfig config, HexMap map) {
        this("1.0", config, map, new ArrayList<>(), 100, 100, 1.0);
        this.dirty = true;
    }

    public String getVersion() {
        return version;
    }

    public ScenarioConfig getConfig() {
        return config;
    }

    public HexMap getMap() {
        return map;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public double getCameraX() { return cameraX; }
    public double getCameraY() { return cameraY; }
    public double getZoom() { return zoom; }

    public void setCamera(double x, double y, double zoom) {
        this.cameraX = x;
        this.cameraY = y;
        this.zoom = zoom;
    }

    public boolean isDirty() { return dirty; }
    public void setDirty(boolean dirty) { this.dirty = dirty; }
    public void markDirty() { this.dirty = true; }
}
