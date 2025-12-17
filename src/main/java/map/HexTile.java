package map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HexTile {
    private TerrainType terrain;
    private FeatureType feature;
    private final EdgeFeature edges;

    @JsonCreator
    public HexTile(@JsonProperty("terrain") TerrainType terrain,
                   @JsonProperty("feature") FeatureType feature,
                   @JsonProperty("edges") EdgeFeature edges) {
        this.terrain = terrain == null ? TerrainType.PLAIN : terrain;
        this.feature = feature == null ? FeatureType.NONE : feature;
        this.edges = edges == null ? new EdgeFeature() : edges;
    }

    public HexTile(TerrainType terrain) {
        this(terrain, FeatureType.NONE, new EdgeFeature());
    }

    public TerrainType getTerrain() {
        return terrain;
    }

    public void setTerrain(TerrainType terrain) {
        this.terrain = terrain;
    }

    public FeatureType getFeature() {
        return feature;
    }

    public void setFeature(FeatureType feature) {
        this.feature = feature;
    }

    public EdgeFeature getEdges() {
        return edges;
    }
}
